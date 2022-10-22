package minecrafthdl.network;

import minecrafthdl.MinecraftHDL;
import minecrafthdl.gui.SynthesiserGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public final class HDLPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MinecraftHDL.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        INSTANCE.registerMessage(0, OpenSynthesizerPacket.class, OpenSynthesizerPacket::encode, OpenSynthesizerPacket::decode, OpenSynthesizerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static class OpenSynthesizerPacket {
        final BlockPos blockPos;

        public OpenSynthesizerPacket(BlockPos blockPos) {
            this.blockPos = blockPos;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(blockPos);
        }

        public static OpenSynthesizerPacket decode(FriendlyByteBuf buf) {
            return new OpenSynthesizerPacket(buf.readBlockPos());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            var context = supplier.get();
            var minecraft = Minecraft.getInstance();
            context.enqueueWork(() -> {
                if (minecraft.level.isLoaded(blockPos)) minecraft.setScreen(new SynthesiserGUI(minecraft.level, blockPos));
            });
            context.setPacketHandled(true);
        }
    }
}
