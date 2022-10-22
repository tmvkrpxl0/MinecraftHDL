package minecrafthdl.block;

import minecrafthdl.MinecraftHDL;
import minecrafthdl.block.blocks.Synthesizer;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Francis on 10/5/2016.
 */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftHDL.MODID);

    public static final RegistryObject<Block> synthesizer = BLOCKS.register("synthesizer", Synthesizer::new);

    public static void registerBlock(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
