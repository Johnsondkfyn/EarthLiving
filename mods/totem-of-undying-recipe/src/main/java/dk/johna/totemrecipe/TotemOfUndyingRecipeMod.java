package dk.johna.totemrecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(TotemOfUndyingRecipeMod.MOD_ID)
public final class TotemOfUndyingRecipeMod {
    public static final String MOD_ID = "totem_of_undying_recipe";

    private static final ToolMaterial OBSIDIAN_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1150,
            5.0F,
            4.0F,
            7,
            ItemTags.DIAMOND_TOOL_MATERIALS);

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<Item> TOTEM_FRAGMENT = ITEMS.register("totemfragment",
            () -> new Item(new Item.Properties()
                    .setId(ITEMS.key("totemfragment"))
                    .stacksTo(64)));

    public static final RegistryObject<Item> OBSIDIAN_SWORD = ITEMS.register("obsidiansword",
            () -> new Item(OBSIDIAN_MATERIAL.applySwordProperties(
                    new Item.Properties().setId(ITEMS.key("obsidiansword")),
                    3.0F,
                    -1.0F)));

    public static final RegistryObject<CreativeModeTab> BETTER_TOOLS_TAB = CREATIVE_TABS.register("better_tools",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> OBSIDIAN_SWORD.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(TOTEM_FRAGMENT.get());
                        output.accept(OBSIDIAN_SWORD.get());
                    })
                    .build());

    public TotemOfUndyingRecipeMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        ITEMS.register(modBusGroup);
        CREATIVE_TABS.register(modBusGroup);
    }
}
