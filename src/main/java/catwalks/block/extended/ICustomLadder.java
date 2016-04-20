package catwalks.block.extended;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ICustomLadder {
	
	/**
	 * Check whether the ladder should be applied
	 * @param world
	 * @param pos
	 * @param player
	 * @return
	 */
	public boolean shouldApply(World world, BlockPos pos, EntityPlayer player);
	
	/**
	 * Get speed multipler for climbing the ladder
	 * @param world
	 * @param pos
	 * @return
	 */
	public double climbSpeed(World world, BlockPos pos);
	
	/**
	 * Get max speed multiplier for falling through ladder
	 * (max speed down is 0.15*mul blocks/tick)
	 * @param world
	 * @param pos
	 * @return
	 */
	public double fallSpeed(World world, BlockPos pos);
	
	/**
	 * Get speed limit multiplier for moving horizonally through ladder
	 * (max speed on X and Z axes is 0.15*mul blocks/tick)
	 * @param world
	 * @param pos
	 * @return
	 */
	public double horizontalSpeed(World world, BlockPos pos);
	
}
