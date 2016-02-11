package catwalks.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface ICatwalkConnect {

	public boolean isSideOpen(World world, BlockPos pos, EnumFacing side);
	public boolean canConnectToSide(World world, BlockPos pos, EnumFacing side);
	public boolean isWide(World world, BlockPos pos, EnumFacing side);
	
}
