package dk.earthliving.core.discord;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.report.ReportCategory;
import dk.earthliving.core.report.ReportService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;

public final class DiscordReportImportService {
    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final ReportService reports;
    private Object jda;
    private Object listener;

    public DiscordReportImportService(JavaPlugin plugin, NotificationService notifications, ReportService reports) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.reports = reports;
    }

    public void startLater() {
        if (!enabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::start, 100L);
    }

    public void stop() {
        if (jda == null || listener == null) {
            return;
        }

        try {
            Method removeEventListener = jda.getClass().getMethod("removeEventListener", Object[].class);
            removeEventListener.invoke(jda, (Object) new Object[]{listener});
        } catch (ReflectiveOperationException | RuntimeException exception) {
            notifications.console("Could not unregister Discord report import listener: " + exception.getMessage());
        } finally {
            jda = null;
            listener = null;
        }
    }

    private void start() {
        if (!enabled()) {
            return;
        }

        Plugin discordSrv = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSrv == null || !discordSrv.isEnabled()) {
            notifications.console("Discord report import is waiting for DiscordSRV.");
            return;
        }

        try {
            Class<?> discordSrvClass = Class.forName("github.scarsz.discordsrv.DiscordSRV");
            Object discordSrvPlugin = discordSrvClass.getMethod("getPlugin").invoke(null);
            jda = discordSrvPlugin.getClass().getMethod("getJda").invoke(discordSrvPlugin);
            if (jda == null) {
                notifications.console("Discord report import could not start because JDA is not ready.");
                return;
            }

            Class<?> listenerClass = Class.forName("github.scarsz.discordsrv.dependencies.jda.api.hooks.EventListener");
            InvocationHandler handler = (proxy, method, args) -> {
                if ("onEvent".equals(method.getName()) && args != null && args.length == 1) {
                    handleEvent(args[0]);
                }
                return null;
            };
            listener = Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class<?>[]{listenerClass}, handler);

            Method addEventListener = jda.getClass().getMethod("addEventListener", Object[].class);
            addEventListener.invoke(jda, (Object) new Object[]{listener});
            notifications.console("Discord report import enabled for channel " + reportChannelId() + ".");
        } catch (ReflectiveOperationException | RuntimeException exception) {
            notifications.console("Could not start Discord report import: " + exception.getMessage());
        }
    }

    private void handleEvent(Object event) {
        if (event == null || !event.getClass().getName().endsWith(".GuildMessageReceivedEvent")) {
            return;
        }

        try {
            Object message = invoke(event, "getMessage");
            Object author = invoke(event, "getAuthor");
            Object channel = invoke(event, "getChannel");

            if (message == null || author == null || channel == null || asBoolean(invoke(author, "isBot"))) {
                return;
            }

            String channelId = asString(invoke(channel, "getId"));
            if (!reportChannelId().equals(channelId)) {
                return;
            }

            String content = asString(invoke(message, "getContentRaw")).trim();
            String prefix = plugin.getConfig().getString("discord.report-import.required-prefix", "!report").trim();
            if (!prefix.isEmpty() && !content.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
                return;
            }

            String body = prefix.isEmpty() ? content : content.substring(prefix.length()).trim();
            ParsedDiscordReport parsed = parse(body);
            if (parsed.note().isBlank()) {
                reply(message, "Write `!report <category> <message>` so staff know what to check.");
                return;
            }

            String discordUser = asString(invoke(author, "getAsTag"));
            if (discordUser.isBlank()) {
                discordUser = asString(invoke(author, "getName"));
            }
            String discordUserId = asString(invoke(author, "getId"));
            String finalDiscordUser = discordUser;
            Bukkit.getScheduler().runTask(plugin, () -> {
                int id = reports.createDiscordReport(finalDiscordUser, discordUserId, channelId, parsed.category(), parsed.note());
                reply(message, acknowledgement(id));
            });
        } catch (ReflectiveOperationException | RuntimeException exception) {
            notifications.console("Could not import Discord report: " + exception.getMessage());
        }
    }

    private ParsedDiscordReport parse(String body) {
        ReportCategory fallback = ReportCategory.fromId(
                plugin.getConfig().getString("discord.report-import.default-category", "bug"),
                ReportCategory.BUG
        );
        if (body == null || body.isBlank()) {
            return new ParsedDiscordReport(fallback, "");
        }

        String trimmed = body.trim();
        String[] parts = trimmed.split("\\s+", 2);
        ReportCategory category = ReportCategory.fromId(parts[0], null);
        if (category == null) {
            return new ParsedDiscordReport(fallback, trimmed);
        }

        String note = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedDiscordReport(category, note);
    }

    private boolean enabled() {
        return plugin.getConfig().getBoolean("modules.discord", false)
                && plugin.getConfig().getBoolean("discord.discordsrv.enabled", true)
                && plugin.getConfig().getBoolean("discord.report-import.enabled", false)
                && !reportChannelId().isBlank();
    }

    private String reportChannelId() {
        return plugin.getConfig().getString("discord.report-import.channel-id", "").trim();
    }

    private String acknowledgement(int id) {
        return plugin.getConfig()
                .getString("discord.report-import.acknowledgement", "Report #{id} is now in the Earth Living Report Center.")
                .replace("{id}", String.valueOf(id));
    }

    private void reply(Object message, String text) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Object action = message.getClass().getMethod("reply", CharSequence.class).invoke(message, text);
                action.getClass().getMethod("queue").invoke(action);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                notifications.console("Could not acknowledge Discord report: " + exception.getMessage());
            }
        });
    }

    private Object invoke(Object target, String methodName) throws ReflectiveOperationException {
        return target.getClass().getMethod(methodName).invoke(target);
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean asBoolean(Object value) {
        return value instanceof Boolean bool && bool;
    }

    private record ParsedDiscordReport(ReportCategory category, String note) {
    }
}
