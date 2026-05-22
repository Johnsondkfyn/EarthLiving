<link rel="stylesheet" href="/assets/extensions/earthlivingcore/admin.style.css?v=20260522-report-workflow">
<style>
    .earthliving-extension-page {
        color: #edf3ec;
        background: #0b1110;
        padding: 18px;
        min-height: 100vh;
    }

    .earthliving-extension-page h2,
    .earthliving-extension-page h3,
    .earthliving-extension-page h4,
    .earthliving-extension-page strong {
        color: #ffffff;
    }

    .earthliving-extension-page p,
    .earthliving-extension-page span,
    .earthliving-extension-page small {
        color: #cbd8cb;
    }
</style>

@php
    $marketplaceSections = [
        [
            'title' => 'Pterodactyl Themes',
            'icon' => 'fa-paint-brush',
            'summary' => 'Visual upgrades for the panel. Use Blueprint-compatible themes first so updates stay safer.',
            'items' => [
                ['name' => 'Euphoria Theme', 'tag' => 'Research', 'risk' => 'Medium', 'url' => 'https://euphoriatheme.uk/', 'note' => 'Blueprint-powered theme. Compare against Earth Living carbon style before installing.'],
                ['name' => 'Blueprint Night Theme', 'tag' => 'Optional', 'risk' => 'Low', 'url' => 'https://www.sourcexchange.net/products/blueprint-night-theme', 'note' => 'Free dark admin theme idea source. We already have a custom carbon theme.'],
                ['name' => 'Custom Earth Living Core', 'tag' => 'Active', 'risk' => 'Low', 'url' => route('admin.extensions'), 'note' => 'Current local Blueprint extension controlling branding, buttons, cards and owner shortcuts.'],
            ],
        ],
        [
            'title' => 'Plugin Installers',
            'icon' => 'fa-download',
            'summary' => 'Tools for browsing and staging Minecraft plugins without installing directly into production.',
            'items' => [
                ['name' => 'Earth Living Plugin Gate', 'tag' => 'Active', 'risk' => 'Low', 'url' => route('admin.plugins'), 'note' => 'Current staging-first workflow for Spigot research and test-server approval.'],
                ['name' => 'Minecraft Plugin Manager', 'tag' => 'Research', 'risk' => 'High', 'url' => 'https://www.sourcexchange.net/products/minecraft-plugin-manager-for-pterodactyl', 'note' => 'One-click plugin installs are useful, but must be adapted to staging/test approval first.'],
                ['name' => 'Modrinth/Hangar/Spigot Browser', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.plugins'), 'note' => 'Future unified search with compatibility notes for Paper 26.1.2 and Java 25.'],
            ],
        ],
        [
            'title' => 'Player Managers',
            'icon' => 'fa-users',
            'summary' => 'Owner tools for whitelist, roles, punishments, reports and player lookup.',
            'items' => [
                ['name' => 'Whitelist Manager', 'tag' => 'Planned', 'risk' => 'Low', 'url' => route('admin.users'), 'note' => 'Add/remove test players and later sync with Discord verification.'],
                ['name' => 'LuckPerms Role Overview', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.servers'), 'note' => 'Read-only owner dashboard first, then safe role actions later.'],
                ['name' => 'Player Reports Dashboard', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.plugins'), 'note' => 'Moderation queue for chat, griefing, bugs and staff alerts.'],
            ],
        ],
        [
            'title' => 'Admin Tools',
            'icon' => 'fa-wrench',
            'summary' => 'Operational tools that help keep Earth Living stable before public launch.',
            'items' => [
                ['name' => 'Backup Monitor', 'tag' => 'Recommended', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Show latest backup age, size and failed backup warnings.'],
                ['name' => 'BlueMap Status', 'tag' => 'Recommended', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Show render state, web status, disk usage and map links.'],
                ['name' => 'Velocity Network View', 'tag' => 'In Progress', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Visual overview of Velocity, Earth Living, Standard Survival and test server routing.'],
            ],
        ],
        [
            'title' => 'External Ecosystem',
            'icon' => 'fa-external-link',
            'summary' => 'Places to research addons. Anything from here should be reviewed before it touches production.',
            'items' => [
                ['name' => 'Blueprint Framework', 'tag' => 'Trusted base', 'risk' => 'Low', 'url' => 'https://github.com/BlueprintFramework/framework', 'note' => 'Framework already used for this panel customization.'],
                ['name' => 'Euphoria Development', 'tag' => 'Research', 'risk' => 'Medium', 'url' => 'https://euphoriadevelopment.uk/index.html', 'note' => 'Blueprint themes and extensions for Pterodactyl. Review compatibility first.'],
                ['name' => 'SourceXchange Pterodactyl Addons', 'tag' => 'Research', 'risk' => 'High', 'url' => 'https://www.sourcexchange.net/', 'note' => 'Marketplace with paid addons. Treat as research until tested in staging.'],
            ],
        ],
    ];

    $reportSources = [
        'Main Server' => '/var/lib/pterodactyl/volumes/0157164c-e4e1-4979-935c-d703ddd6706e/plugins/EarthLivingCore/reports-panel.json',
        'Test Server' => '/var/lib/pterodactyl/volumes/d554d2b4-ac4b-4b48-b004-35f1b73feadc/plugins/EarthLivingCore/reports-panel.json',
    ];

    $reportSourceName = 'Not connected';
    $reportSourcePath = null;
    $reportData = ['generatedAt' => null, 'openCount' => 0, 'reports' => []];

    foreach ($reportSources as $sourceName => $sourcePath) {
        if (is_readable($sourcePath)) {
            $decodedReports = json_decode(file_get_contents($sourcePath), true);
            if (is_array($decodedReports)) {
                $reportSourceName = $sourceName;
                $reportSourcePath = $sourcePath;
                $reportData = array_merge($reportData, $decodedReports);
                break;
            }
        }
    }

    $panelReports = array_slice($reportData['reports'] ?? [], 0, 8);
    $reportGeneratedAt = $reportData['generatedAt'] ?? null;
    if (is_string($reportGeneratedAt) && strlen($reportGeneratedAt) > 19) {
        $reportGeneratedAt = str_replace('T', ' ', substr($reportGeneratedAt, 0, 19));
    }

    $earthLivingView = request()->query('view', 'marketplace');
    $showReports = $earthLivingView === 'reports';
    $showMarketplace = ! $showReports;
@endphp

<div class="earthliving-extension-page earthliving-marketplace-page">
    <section class="earthliving-extension-hero earthliving-marketplace-hero">
        <div>
            <span class="earthliving-kicker">Owner toolkit</span>
            <h2>{{ $showReports ? 'Report Center' : 'Panel Marketplace' }}</h2>
            <p>
                @if ($showReports)
                    In-game and Discord reports from EarthLivingCore, shown as a read-only operations queue for staff review.
                @else
                    Curated Pterodactyl themes, plugin installers, player managers, admin tools and network utilities for Earth Living.
                @endif
            </p>
        </div>
        <div class="earthliving-extension-actions">
            <a class="btn btn-success" href="{{ url('/admin/extensions/earthlivingcore?view=reports') }}">
                <i class="fa fa-flag"></i> Report Center
            </a>
            <a class="btn btn-primary" href="{{ route('admin.plugins') }}">
                <i class="fa fa-puzzle-piece"></i> Plugin Library
            </a>
            <a class="btn btn-default" href="{{ url('/admin/extensions/earthlivingcore?view=marketplace') }}">
                <i class="fa fa-cubes"></i> Marketplace
            </a>
            <a class="btn btn-default" href="{{ route('admin.servers') }}">
                <i class="fa fa-server"></i> Servers
            </a>
        </div>
    </section>

    <div class="earthliving-policy-strip">
        <div>
            <span>Install policy</span>
            <strong>Research -> staging -> backup -> production</strong>
        </div>
        <div>
            <span>Runtime target</span>
            <strong>Paper 26.1.2 build 64 + Java 25</strong>
        </div>
        <div>
            <span>Network plan</span>
            <strong>Velocity -> Earth Living / Survival / Test</strong>
        </div>
    </div>

    @if ($showReports)
    <section id="earthliving-report-center" class="earthliving-report-center">
        <header>
            <div>
                <span class="earthliving-kicker">Operations queue</span>
                <h3>Report Center</h3>
                <p>Reports exported by EarthLivingCore from both the in-game EarthOS flow and the Discord bug-reports channel.</p>
            </div>
            <div class="earthliving-report-stats">
                <div>
                    <span>Source</span>
                    <strong>{{ $reportSourceName }}</strong>
                </div>
                <div>
                    <span>Open</span>
                    <strong>{{ $reportData['openCount'] ?? 0 }}</strong>
                </div>
                <div>
                    <span>Updated</span>
                    <strong>{{ $reportGeneratedAt ?? 'No export yet' }}</strong>
                </div>
            </div>
        </header>

        @if (count($panelReports) === 0)
            <article class="earthliving-report-empty">
                <i class="fa fa-inbox"></i>
                <div>
                    <h4>No reports exported yet</h4>
                    <p>Create a report in EarthOS or write a Discord report, then refresh this panel page.</p>
                    @if ($reportSourcePath)
                        <small>{{ $reportSourcePath }}</small>
                    @endif
                </div>
            </article>
        @else
            <div class="earthliving-report-list">
                @foreach ($panelReports as $report)
                    <article class="earthliving-report-card" data-report-id="{{ $report['id'] ?? '?' }}">
                        @php
                            $source = strtolower($report['source'] ?? 'minecraft');
                            $isDiscordReport = $source === 'discord';
                            $sourceLabel = $isDiscordReport ? 'Discord' : 'In-game';
                            $reportIdentity = $isDiscordReport ? ($report['discordUser'] ?? $report['playerName'] ?? 'Unknown') : ($report['playerName'] ?? 'Unknown');
                            $reportLocation = $isDiscordReport ? 'Discord bug-reports channel' : (($report['world'] ?? 'world') . ' ' . ($report['x'] ?? 0) . ' ' . ($report['y'] ?? 0) . ' ' . ($report['z'] ?? 0));
                            $reportId = $report['id'] ?? '?';
                            $chatGptPrompt = "Analyze this EarthLiving report and suggest likely cause, severity, next checks, and a safe fix plan. Do not ask for secrets.\n\n"
                                . "Report ID: #" . $reportId . "\n"
                                . "Source: " . $sourceLabel . "\n"
                                . "Status: " . ($report['status'] ?? 'unknown') . "\n"
                                . "Category: " . ($report['categoryTitle'] ?? 'Report') . "\n"
                                . "Reporter: " . $reportIdentity . "\n"
                                . "Location/Channel: " . $reportLocation . "\n"
                                . "Created: " . ($report['createdAt'] ?? 'unknown') . "\n"
                                . "Note: " . ($report['note'] ?? '') . "\n\n"
                                . "Expected output:\n"
                                . "1. Short diagnosis\n"
                                . "2. Risk/severity\n"
                                . "3. What logs/config/code to inspect\n"
                                . "4. Recommended Codex handoff\n"
                                . "5. Player/staff reply draft";
                            $codexPrompt = "## Goal\nInvestigate and propose a safe fix for EarthLiving report #" . $reportId . ".\n\n"
                                . "## Report\n"
                                . "- Source: " . $sourceLabel . "\n"
                                . "- Status: " . ($report['status'] ?? 'unknown') . "\n"
                                . "- Category: " . ($report['categoryTitle'] ?? 'Report') . "\n"
                                . "- Reporter: " . $reportIdentity . "\n"
                                . "- Location/Channel: " . $reportLocation . "\n"
                                . "- Created: " . ($report['createdAt'] ?? 'unknown') . "\n"
                                . "- Note: " . ($report['note'] ?? '') . "\n\n"
                                . "## Instructions\n"
                                . "- Check relevant config, plugin code, panel/report export, and recent logs.\n"
                                . "- Do not expose secrets or tokens.\n"
                                . "- If a code/config fix is needed, implement it, test it, and update docs/status.\n"
                                . "- Report back with changed files, verification, and any remaining risk.";
                            $repairPrompt = "EarthLiving staff repair approval\n\n"
                                . "Report: #" . $reportId . "\n"
                                . "Reporter: " . $reportIdentity . "\n"
                                . "Issue: " . ($report['note'] ?? '') . "\n\n"
                                . "Repair approval checklist:\n"
                                . "- Root cause identified\n"
                                . "- Fix tested on test server or low-risk config reviewed\n"
                                . "- No secrets exposed\n"
                                . "- Main server deploy/restart risk understood\n"
                                . "- Player/staff reply prepared";
                            $playerReply = "Tak for din report #" . $reportId . ". Vi har kigget på den og følger op i staff-flowet. "
                                . "Hvis problemet sker igen, må du meget gerne sende en ny besked med tidspunkt og hvad du gjorde lige før fejlen.";
                            $closePrompt = "Close EarthLiving report #" . $reportId . "\n\n"
                                . "Reporter: " . $reportIdentity . "\n"
                                . "Source: " . $sourceLabel . "\n"
                                . "Original report: " . ($report['note'] ?? '') . "\n\n"
                                . "Close checklist:\n"
                                . "- Fix/decision approved by staff\n"
                                . "- Player reply sent or not needed\n"
                                . "- Any code/config change documented\n"
                                . "- Report can be marked completed in EarthLivingCore";
                        @endphp
                        <div class="earthliving-report-card-head">
                            <strong>#{{ $report['id'] ?? '?' }} {{ $report['categoryTitle'] ?? 'Report' }}</strong>
                            <span>{{ $sourceLabel }} · {{ $report['status'] ?? 'unknown' }}</span>
                        </div>
                        <p>{{ $report['note'] ?? '' }}</p>
                        <div class="earthliving-report-meta">
                            <span><i class="fa {{ $isDiscordReport ? 'fa-comments' : 'fa-user' }}"></i> {{ $isDiscordReport ? ($report['discordUser'] ?? $report['playerName'] ?? 'Unknown') : ($report['playerName'] ?? 'Unknown') }}</span>
                            @if ($isDiscordReport)
                                <span><i class="fa fa-comments"></i> Discord report</span>
                            @else
                                <span><i class="fa fa-map-marker"></i> {{ $report['world'] ?? 'world' }} {{ $report['x'] ?? 0 }} {{ $report['y'] ?? 0 }} {{ $report['z'] ?? 0 }}</span>
                            @endif
                            <span><i class="fa fa-clock-o"></i> {{ $report['createdAt'] ?? 'unknown' }}</span>
                        </div>
                        <div class="earthliving-report-actions">
                            <button
                                type="button"
                                class="btn btn-primary btn-xs earthliving-copy-report"
                                data-copy-label="ChatGPT package copied"
                                data-report-prompt="{{ e($chatGptPrompt) }}"
                            >
                                <i class="fa fa-magic"></i> Analyze with ChatGPT
                            </button>
                            <button
                                type="button"
                                class="btn btn-success btn-xs earthliving-copy-report"
                                data-copy-label="Codex handoff copied"
                                data-report-prompt="{{ e($codexPrompt) }}"
                            >
                                <i class="fa fa-code"></i> Send to Codex
                            </button>
                            <button
                                type="button"
                                class="btn btn-default btn-xs earthliving-toggle-report"
                                aria-expanded="false"
                            >
                                <i class="fa fa-file-text-o"></i> View package
                            </button>
                        </div>
                        <div class="earthliving-report-workflow" data-workflow-state="open">
                            <span class="earthliving-workflow-status">
                                <i class="fa fa-circle-o"></i> Workflow: Open
                            </span>
                            <button
                                type="button"
                                class="btn btn-warning btn-xs earthliving-workflow-action earthliving-copy-report"
                                data-workflow-state="repair-approved"
                                data-copy-label="Repair approval copied"
                                data-report-prompt="{{ e($repairPrompt) }}"
                            >
                                <i class="fa fa-check-square-o"></i> Godkend fix
                            </button>
                            <button
                                type="button"
                                class="btn btn-info btn-xs earthliving-copy-report"
                                data-copy-label="Player reply copied"
                                data-report-prompt="{{ e($playerReply) }}"
                            >
                                <i class="fa fa-reply"></i> Svar til spiller
                            </button>
                            <button
                                type="button"
                                class="btn btn-default btn-xs earthliving-copy-report"
                                data-copy-label="Close package copied"
                                data-report-prompt="{{ e($closePrompt) }}"
                            >
                                <i class="fa fa-archive"></i> Afslutningspakke
                            </button>
                            <button
                                type="button"
                                class="btn btn-danger btn-xs earthliving-workflow-action"
                                data-workflow-state="completed"
                            >
                                <i class="fa fa-flag-checkered"></i> Afsluttet
                            </button>
                            <button
                                type="button"
                                class="btn btn-default btn-xs earthliving-workflow-action"
                                data-workflow-state="open"
                            >
                                <i class="fa fa-undo"></i> Genåbn
                            </button>
                        </div>
                        <textarea class="earthliving-report-package" readonly>{{ $chatGptPrompt }}</textarea>
                    </article>
                @endforeach
            </div>
        @endif
    </section>
    @endif

    @if ($showMarketplace)
    <div class="row earthliving-market-grid">
        @foreach ($marketplaceSections as $section)
            <div class="col-md-6">
                <section class="earthliving-market-section">
                    <header>
                        <i class="fa {{ $section['icon'] }}"></i>
                        <div>
                            <h3>{{ $section['title'] }}</h3>
                            <p>{{ $section['summary'] }}</p>
                        </div>
                    </header>

                    @foreach ($section['items'] as $item)
                        <article class="earthliving-market-item">
                            <div>
                                <h4>{{ $item['name'] }}</h4>
                                <p>{{ $item['note'] }}</p>
                            </div>
                            <div class="earthliving-market-meta">
                                <span class="earthliving-badge">{{ $item['tag'] }}</span>
                                <span class="earthliving-risk earthliving-risk-{{ strtolower($item['risk']) }}">{{ $item['risk'] }} risk</span>
                                <a class="btn btn-default btn-xs" href="{{ $item['url'] }}" target="_blank" rel="noreferrer">
                                    <i class="fa fa-external-link"></i> Open
                                </a>
                            </div>
                        </article>
                    @endforeach
                </section>
            </div>
        @endforeach
    </div>
    @endif

    <div class="row">
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Current priority</span>
                <strong>Do not auto-install to production</strong>
                <p>Every panel addon should be tested after a backup and checked against Blueprint/Pterodactyl updates.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Useful next build</span>
                <strong>Velocity Network View</strong>
                <p>Show proxy, Earth Living, Standard Survival and test server health in one owner dashboard.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Research queue</span>
                <strong>Plugin manager + player tools</strong>
                <p>Browse options here, then turn the best ones into Earth Living-safe workflows.</p>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('click', function (event) {
    var workflowButton = event.target.closest('.earthliving-workflow-action');
    if (workflowButton) {
        var workflowCard = workflowButton.closest('.earthliving-report-card');
        var nextState = workflowButton.getAttribute('data-workflow-state') || 'open';
        if (workflowCard) {
            setWorkflowState(workflowCard, nextState, true);
        }
    }

    var copyButton = event.target.closest('.earthliving-copy-report');
    if (copyButton) {
        var text = copyButton.getAttribute('data-report-prompt') || '';
        var original = copyButton.innerHTML;
        var label = copyButton.getAttribute('data-copy-label') || 'Copied';
        var markCopied = function () {
            copyButton.innerHTML = '<i class="fa fa-check"></i> ' + label;
            window.setTimeout(function () {
                copyButton.innerHTML = original;
            }, 1800);
        };

        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(markCopied).catch(function () {
                fallbackCopy(text, markCopied);
            });
        } else {
            fallbackCopy(text, markCopied);
        }
        return;
    }

    var toggleButton = event.target.closest('.earthliving-toggle-report');
    if (toggleButton) {
        var card = toggleButton.closest('.earthliving-report-card');
        var packageBox = card ? card.querySelector('.earthliving-report-package') : null;
        if (!packageBox) {
            return;
        }

        var expanded = packageBox.classList.toggle('is-visible');
        toggleButton.setAttribute('aria-expanded', expanded ? 'true' : 'false');
        toggleButton.innerHTML = expanded
            ? '<i class="fa fa-eye-slash"></i> Hide package'
            : '<i class="fa fa-file-text-o"></i> View package';
    }
});

function fallbackCopy(text, callback) {
    var area = document.createElement('textarea');
    area.value = text;
    area.setAttribute('readonly', 'readonly');
    area.style.position = 'fixed';
    area.style.left = '-9999px';
    document.body.appendChild(area);
    area.select();
    document.execCommand('copy');
    document.body.removeChild(area);
    callback();
}

function setWorkflowState(card, state, persist) {
    var reportId = card.getAttribute('data-report-id') || '';
    var workflow = card.querySelector('.earthliving-report-workflow');
    var status = card.querySelector('.earthliving-workflow-status');
    if (!workflow || !status) {
        return;
    }

    var labels = {
        open: '<i class="fa fa-circle-o"></i> Workflow: Open',
        'repair-approved': '<i class="fa fa-check-square-o"></i> Workflow: Fix godkendt',
        completed: '<i class="fa fa-flag-checkered"></i> Workflow: Afsluttet lokalt'
    };

    workflow.setAttribute('data-workflow-state', state);
    status.innerHTML = labels[state] || labels.open;
    card.classList.toggle('is-workflow-completed', state === 'completed');

    if (persist && reportId && window.localStorage) {
        window.localStorage.setItem('earthliving-report-workflow-' + reportId, state);
    }
}

document.querySelectorAll('.earthliving-report-card[data-report-id]').forEach(function (card) {
    var reportId = card.getAttribute('data-report-id');
    if (!reportId || !window.localStorage) {
        return;
    }

    var savedState = window.localStorage.getItem('earthliving-report-workflow-' + reportId);
    if (savedState) {
        setWorkflowState(card, savedState, false);
    }
});
</script>
