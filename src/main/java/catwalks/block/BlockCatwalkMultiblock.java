package catwalks.block;

import java.util.List;

import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.ExtendedFlatHighlightMOP;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCatwalkMultiblock extends BlockBase {

	public BlockCatwalkMultiblock() {
		super(Material.iron, "multiblockPart");
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start,
			Vec3 end) {
		IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
		if(state.getBlock() instanceof BlockBase) {
			MovingObjectPosition mop = state.getBlock().collisionRayTrace(world, pos.offset(EnumFacing.DOWN), start, end);
			return mop;
		}
		return null;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

	public int getRenderType() {
        return -1;
	}
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
    	IBlockState below = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
    	
    	below.getBlock().addCollisionBoxesToList(worldIn, pos.offset(EnumFacing.DOWN), below, mask, list, collidingEntity);
    }
    
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }
	
}
