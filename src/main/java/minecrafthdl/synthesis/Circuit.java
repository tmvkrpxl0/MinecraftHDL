package minecrafthdl.synthesis;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Francis on 10/28/2016.
 */
public class Circuit {
    public static boolean TEST = false;

    final int x, y, z;
    Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
    HashMap<Vec3i, BlockEntity> te_map = new HashMap<>();

    public Circuit(int sizeX, int sizeY, int sizeZ){
        x = sizeX;
        y = sizeY;
        z = sizeZ;
        if (!Circuit.TEST) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int z = 0; z < sizeZ; z++) {
                        this.blocks.put(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }



    public void setBlock(int x, int y, int z, BlockState blockstate) {
        if (TEST) return;
        this.blocks.put(new BlockPos(x, y, z), blockstate);
    }

    public void placeInWorld(Level worldIn, BlockPos pos, Direction direction) {
        int width = x;
        int height = y;
        int length = z;

        int start_x = pos.getX();
        int start_y = pos.getY();
        int start_z = pos.getZ();

        if (direction == Direction.NORTH){
            start_z += 2;
        } else if (direction == Direction.SOUTH) {
            start_z -= length + 1;
        } else if (direction == Direction.EAST){
            start_x -= width + 1;
        } else if (direction == Direction.WEST) {
            start_x -= width + 1;
        }

        int y = start_y - 1;
        for (int z = start_z - 1; z < start_z + length + 1; z ++){
            for (int x = start_x - 1; x < start_x + width + 1; x++){
                worldIn.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.STONE_BRICKS.defaultBlockState());
            }
        }

        HashMap<Vec3i ,BlockState> torches = new HashMap<>();

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < length; k++) {
                    if (this.getState(i, j, k).getBlock().defaultBlockState() == Blocks.REDSTONE_TORCH.defaultBlockState()) {
                        torches.put(new Vec3i(i, j, k), this.getState(i, j, k));
                    } else {
                        BlockPos blk_pos = new BlockPos(start_x + i, start_y + j, start_z + k);
                        worldIn.setBlockAndUpdate(blk_pos, this.getState(i, j, k));

                        BlockEntity te = this.te_map.get(new Vec3i(i, j, k));
                        assert te.getBlockPos().equals(blk_pos);
                        worldIn.setBlockEntity(te);
                    }
                }
            }
        }

        for (Map.Entry<Vec3i, BlockState> set : torches.entrySet()){
            worldIn.setBlockAndUpdate(new BlockPos(start_x + set.getKey().getX(), start_y + set.getKey().getY(), start_z + set.getKey().getZ()), set.getValue());
        }
    }

    public int getSizeX() {
        return x;
    }

    public int getSizeY() {
        return y;
    }

    public int getSizeZ() {
        return z;
    }

    public BlockState getState(int x, int y, int z){
        return this.blocks.get(new BlockPos(x, y, z));
    }

    public void insertCircuit(int x_offset, int y_offset, int z_offset, Circuit c) {
        for (int x = 0; x < c.getSizeX(); x++) {
            for (int y = 0; y < c.getSizeY(); y++) {
                for (int z = 0; z < c.getSizeZ(); z++) {
                    this.setBlock(x + x_offset, y + y_offset, z + z_offset, c.getState(x, y, z));

                    BlockEntity te = c.te_map.get(new Vec3i(x, y, z));
                    if (te != null) {
                        this.te_map.put(new Vec3i(x + x_offset, y + y_offset, z + z_offset), te);
                    }
                }
            }
        }
    }
}
