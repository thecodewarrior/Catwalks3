package catwalks.block.extended;

import net.minecraftforge.common.property.IExtendedBlockState;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ITileStateProvider {

	public IExtendedBlockState getTileState(IBlockState state, IBlockAccess worldIn, BlockPos pos);
	
}
