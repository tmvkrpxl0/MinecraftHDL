package minecrafthdl.block;

import minecrafthdl.MinecraftHDL;
import minecrafthdl.block.blocks.Synthesizer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Francis on 10/5/2016.
 */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftHDL.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftHDL.MODID);

    public static final RegistryObject<Block> SYNTHESIZER = BLOCKS.register("synthesizer", Synthesizer::new);
    public static final RegistryObject<Item> SYNTHESIZER_ITEM = BLOCK_ITEMS.register("synthesizer", () -> new BlockItem(SYNTHESIZER.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static void registerBlock(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }
}
