package minecrafthdl;

import minecrafthdl.block.ModBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(MinecraftHDL.MODID)
public class MinecraftHDL
{
    public static final String MODID = "minecrafthdl";

    @SidedProxy(clientSide="minecrafthdl.ClientProxy", serverSide="minecrafthdl.ServerProxy")
    public static CommonProxy proxy;

    public MinecraftHDL() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.registerBlock(modbus);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }
}
