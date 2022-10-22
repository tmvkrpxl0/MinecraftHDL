package minecrafthdl.synthesis;

import minecrafthdl.Demo;
import minecrafthdl.MHDLException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Created by Francis on 11/12/2016.
 */
public class LogicGates {


    public static void main(String[] args) {
        IntermediateCircuit ic = new IntermediateCircuit();
        ic.loadGraph(Demo.create4bitmuxgraph());
        ic.printLayers();
    }

    public static Gate Input(String id){

        Gate gate = new Gate(1, 2, 1, 1, 1, 0, 0, new int[]{0});

        gate.is_io = true;
        String[] id_txt = textToSignLines(id);

        BlockState signState = Blocks.OAK_SIGN.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, 8);
        BlockPos position = new BlockPos(0, 1, 0);
        SignBlockEntity sign = new SignBlockEntity(position, signState);

        for (int i = 0 ; i < id_txt.length; i++){
            sign.setMessage(i, Component.literal(id_txt[i]));
        }

        gate.te_map.put(position, sign);

        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 1, 0, Blocks.OAK_SIGN.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, 8));
        return gate;
    }

    public static Gate Output(String id){
        Gate gate = new Gate(1, 2, 1, 1, 1, 0, 0, new int[]{0});

        gate.is_io = true;
        String[] textLines = textToSignLines(id);

        BlockState signState = Blocks.OAK_SIGN.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, 0);
        BlockPos position = new BlockPos(0, 1, 0);
        SignBlockEntity sign = new SignBlockEntity(position, signState);

        for (int i = 0 ; i < textLines.length; i++){
            sign.setMessage(i, Component.literal(textLines[i]));
        }

        gate.te_map.put(position, sign);

        gate.setBlock(0, 0, 0, Blocks.REDSTONE_LAMP.defaultBlockState());
        gate.setBlock(0, 1, 0, signState);
        return gate;
    }

    private static String[] textToSignLines(String id){
        String[] txt = {"", "", ""};

        int i = 0;

        do {
            if (id.length() <= 15){
                txt[i] += id;
                break;
            } else {
                String line = id.substring(0, 15);
                id = id.substring(15);
                txt[i] += line;
                i++;
            }

        } while (i < 3);

        return  txt;
    }

    public static Gate NOT(){
        Gate gate = new Gate(1, 1, 3, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 1, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
        return gate;
    }

    public static Gate RELAY(){
        Gate gate = new Gate(1, 1, 3, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(0, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
        return gate;
    }

    public static Gate AND(int inputs) {
        if (inputs == 0) throw new MHDLException("Gate cannot have 0 inputs");
        int width;
        if (inputs == 1) width = 1;

        else width = (inputs * 2) - 1;

        Gate gate = new Gate(width, 2, 4, inputs, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 2, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(0, 0, 3, Blocks.REDSTONE_WIRE.defaultBlockState());

        for (int i = 0; i < width; i+=2) {
            gate.setBlock(i, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
            gate.setBlock(i, 0, 1, Blocks.WHITE_WOOL.defaultBlockState());
            gate.setBlock(i, 1, 0, Blocks.REDSTONE_TORCH.defaultBlockState());
            gate.setBlock(i, 1, 1, Blocks.REDSTONE_WIRE.defaultBlockState());

            if (i != width - 1) {
                gate.setBlock(i + 1, 0, 1, Blocks.WHITE_WOOL.defaultBlockState());
                if (i == 14) {
                    gate.setBlock(i + 1, 1, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));
                } else {
                    gate.setBlock(i + 1, 1, 1, Blocks.REDSTONE_WIRE.defaultBlockState());
                }
            }
        }

        return gate;
    }



    public static Gate OR(int inputs) {
        if (inputs == 0) throw new MHDLException("Gate cannot have 0 inputs");
        int width;
        if (inputs == 1) width = 1;
        else width = (inputs * 2) - 1;

        Gate gate = new Gate(width, 2, 4, inputs, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 3, Blocks.REDSTONE_WIRE.defaultBlockState());

        for (int i = 0; i < width; i+=2) {
            gate.setBlock(i, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
            gate.setBlock(i, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
            gate.setBlock(i, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
            if (i != width - 1) {
                if (i == 14) {
                    gate.setBlock(i + 1, 0, 2, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));
                } else {
                    gate.setBlock(i + 1, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
                }
            }
        }
        return gate;
    }

    public static Gate XOR(){
        Gate gate = new Gate(3, 2, 7, 2, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 1, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(0, 0, 3, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 4, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(0, 0, 5, Blocks.REDSTONE_WIRE.defaultBlockState());

        gate.setBlock(0, 1, 0, Blocks.REDSTONE_TORCH.defaultBlockState());
        gate.setBlock(0, 1, 3, Blocks.REDSTONE_WIRE.defaultBlockState());

        gate.setBlock(1, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(1, 1, 0, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(1, 0, 1, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(1, 1, 1, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(1, 0, 2, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(1, 0, 5, Blocks.REDSTONE_WIRE.defaultBlockState());


        gate.setBlock(2, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(2, 0, 1, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(2, 0, 3, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(2, 0, 4, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));

        gate.setBlock(2, 1, 0, Blocks.REDSTONE_TORCH.defaultBlockState());
        gate.setBlock(2, 1, 3, Blocks.REDSTONE_WIRE.defaultBlockState());

        gate.setBlock(0, 0, 6, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));

        return gate;
    }

//    public static Gate XOR(){
//        Gate gate = new Gate(3, 2, 6, 2, 1, 1, 0, new int[]{0});
//        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(0, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
//        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
//        gate.setBlock(0, 0, 3, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(0, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());
//
//        gate.setBlock(0, 1, 0, Blocks.STICKY_PISTON.defaultBlockState().withProperty(Utils.getPropertyByName(Blocks.STICKY_PISTON, "facing"), Direction.SOUTH));
//        gate.setBlock(0, 1, 1, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(0, 1, 3, Blocks.REDSTONE_WIRE.defaultBlockState());
//
//
//        gate.setBlock(2, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(2, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
//        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
//        gate.setBlock(2, 0, 3, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(2, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());
//
//        gate.setBlock(2, 1, 0, Blocks.STICKY_PISTON.defaultBlockState().withProperty(Utils.getPropertyByName(Blocks.STICKY_PISTON, "facing"), Direction.SOUTH));
//        gate.setBlock(2, 1, 1, Blocks.WHITE_WOOL.defaultBlockState());
//        gate.setBlock(2, 1, 3, Blocks.REDSTONE_WIRE.defaultBlockState());
//
//        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());
//        gate.setBlock(1, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());
//        gate.setBlock(0, 0, 5, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
//
//        return gate;
//    }

    public static Gate MUX() {
        Gate gate = new Gate(5, 2, 6, 3, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 1, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 2, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 3, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(0, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());
        gate.setBlock(0, 0, 5, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));

        gate.setBlock(1, 0, 2, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));
        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());

        gate.setBlock(2, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(2, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
        gate.setBlock(2, 0, 2, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(2, 0, 4, Blocks.REDSTONE_WIRE.defaultBlockState());

        gate.setBlock(3, 0, 2, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));
        gate.setBlock(3, 0, 4, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.WEST));

        gate.setBlock(4, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(4, 0, 1, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
        gate.setBlock(4, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockStateProperties.POWER, 10));
        gate.setBlock(4, 0, 3, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockStateProperties.POWER, 10));
        gate.setBlock(4, 0, 4, Blocks.WHITE_WOOL.defaultBlockState());

        gate.setBlock(0, 1, 0, Blocks.REDSTONE_TORCH.defaultBlockState());
        gate.setBlock(0, 1, 1, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockStateProperties.POWER, 10));
        gate.setBlock(0, 1, 2, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockStateProperties.POWER, 10));

        return gate;
    }

    public static Gate LOW(){
        Gate gate = new Gate(1, 1, 1, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        return gate;
    }

    public static Gate HIGH(){
        Gate gate = new Gate(1, 1, 1, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.REDSTONE_TORCH.defaultBlockState());
        return gate;
    }

    public static Gate D_LATCH() {
        Gate gate = new Gate(3, 1, 4, 2, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(0, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
        gate.setBlock(0, 0, 2, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
        gate.setBlock(0, 0, 3, Blocks.WHITE_WOOL.defaultBlockState());

        gate.setBlock(1, 0, 2, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));

        gate.setBlock(2, 0, 0, Blocks.WHITE_WOOL.defaultBlockState());
        gate.setBlock(2, 0, 1, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.defaultBlockState());

        return gate;
    }
}
