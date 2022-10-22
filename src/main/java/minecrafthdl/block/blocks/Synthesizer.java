package minecrafthdl.block.blocks;

import GraphBuilder.GraphBuilder;
import minecrafthdl.network.HDLPackets;
import minecrafthdl.synthesis.Circuit;
import minecrafthdl.synthesis.IntermediateCircuit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

/**
 * Created by Francis on 10/28/2016.
 */
public class Synthesizer extends Block {

    public static String file_to_gen;
    public static int check_threshold = 100;

    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    private int check_counter = 0;
    private boolean to_check = false;
    private Circuit c_check = null;
    private BlockPos p_check = null;

    public Synthesizer() {
        super(BlockBehaviour.Properties.of(Material.STONE)/*.randomTicks()*/);
        registerDefaultState(this.getStateDefinition().any().setValue(TRIGGERED, false));
        System.out.println("hello");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TRIGGERED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide){
            return InteractionResult.SUCCESS;
        } else {
            var packet = new HDLPackets.OpenSynthesizerPacket(pos);
            HDLPackets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), packet);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(!level.isClientSide()) {
            boolean isTriggered = state.getValue(TRIGGERED);
            boolean hasSignal = level.hasNeighborSignal(pos);

            if (isTriggered != hasSignal) {
                level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, hasSignal));
                if (hasSignal) {
                    if (Synthesizer.file_to_gen != null){
                        synth_gen(level, pos);
                    }
                }
            }
        }
    }

    private void synth_gen(Level level, BlockPos pos){
        try {
            IntermediateCircuit ic = new IntermediateCircuit();
            ic.loadGraph(GraphBuilder.buildGraph(Synthesizer.file_to_gen));
            ic.buildGates();
            ic.routeChannels();
            this.c_check = ic.genCircuit();
            c_check.placeInWorld(level, pos, Direction.NORTH);
            this.to_check = true;
            this.p_check = pos;

        } catch (Exception e){
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("An error occurred while generating the circuit, check the logs! Sorry!"));
            e.printStackTrace();
        }
    }
}
