package dk.johna.simplerubymod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(SimpleRubyMod.MOD_ID)
public final class SimpleRubyMod {
    public static final String MOD_ID = "simple_ruby_mod";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties().setId(ITEMS.key("ruby"))));

    public static final RegistryObject<Block> RUBY_BLOCK = BLOCKS.register("ruby_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .setId(BLOCKS.key("ruby_block"))
                    .mapColor(MapColor.COLOR_RED)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new BlockItem(RUBY_BLOCK.get(), new Item.Properties().setId(ITEMS.key("ruby_block"))));

    public static final RegistryObject<Item> RUBY_SWORD = ITEMS.register("ruby_sword",
            () -> new Item(ToolMaterial.DIAMOND.applySwordProperties(
                    new Item.Properties().setId(ITEMS.key("ruby_sword")),
                    3.0F,
                    -2.4F)));

    public static final RegistryObject<CreativeModeTab> RUBY_TAB = CREATIVE_TABS.register("ruby_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.INGREDIENTS)
                    .icon(() -> RUBY.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(RUBY.get());
                        output.accept(RUBY_BLOCK_ITEM.get());
                        output.accept(RUBY_SWORD.get());
                    })
                    .build());

    public SimpleRubyMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        BLOCKS.register(modBusGroup);
        ITEMS.register(modBusGroup);
        CREATIVE_TABS.register(modBusGroup);

        BuildCreativeModeTabContentsEvent.BUS.addListener(SimpleRubyMod::addCreativeItems);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(RUBY.get());
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(RUBY_BLOCK_ITEM.get());
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(RUBY_SWORD.get());
        }
    }
}
