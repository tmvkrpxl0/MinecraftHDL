package minecrafthdl;

import minecrafthdl.block.ModBlocks;
import minecrafthdl.network.HDLPackets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(MinecraftHDL.MODID)
public class MinecraftHDL
{
    public static final String MODID = "minecrafthdl";

    public MinecraftHDL() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.registerBlock(modbus);
        HDLPackets.registerPackets();
    }
}
