package catwalks.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDecoratable {

	/**
	 * @return true if decoration changed
	 */
	public boolean putDecoration(World world, BlockPos pos, String name, boolean value);
	public boolean hasDecoration(World world, BlockPos pos, String name);
	public default boolean canGiveSpeedBoost(World world, BlockPos pos) {
		return true;
	}
}
