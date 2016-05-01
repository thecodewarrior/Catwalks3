package catwalks.block;

import catwalks.block.extended.CubeEdge;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface ICatwalkConnect {
	
	/**
	 * Returns true if the side of the block currently exists
	 */
	public boolean hasSide(World world, BlockPos pos, EnumFacing side);
	
	/**
	 * Returns true if one of the existing side's edges
	 */
	public default boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		return hasSide(world, pos, edge.dir1) != hasSide(world, pos, edge.dir2);
	}
	
	/**
	 * Get edge type of specified edge, can return regardless of whether said edge exists.
	 * 
	 * Often this is fixed for a block type, but that isn't guaranteed.
	 */
	public EnumEdgeType edgeType(World world, BlockPos pos, CubeEdge edge);
	
	/**
	 * Sets the side's state
	 */
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value);
	
	/**
	 * Returns the type of the specified side
	 */
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side);
	
	/**
	 * Returns additional data for the side
	 */
	public Object sideData(World world, BlockPos pos, EnumFacing side);
	
	/**
	 * Update the supplied side based on the surrounding blocks
	 */
	public default void updateSide(World world, BlockPos pos, EnumFacing side) {
		BlockPos aPos = pos.offset(side);
		
		boolean shouldHaveSide = true;
		Block block = world.getBlockState(aPos).getBlock();
		if(block instanceof ICatwalkConnect) {
			ICatwalkConnect connect = (ICatwalkConnect)block;
			
			if(
				connect.sideType(world, aPos, side.getOpposite()) == this.sideType(world, pos, side) &&
				connect.sideData(world, aPos, side) == this.sideData(world, pos, side)
			) {
				shouldHaveSide = false;
			}
			
		}
		
		this.setSide(world, pos, side, shouldHaveSide);
	}
	
	public static enum EnumSideType {
		FULL, LADDER, SLOPE_BOTTOM, SLOPE_TOP;
	}
	
	public static enum EnumEdgeType {
		FULL, LADDER
	}
}
