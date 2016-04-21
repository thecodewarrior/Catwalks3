package catwalks.block.extended;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ICustomLadder {
	
	/**
	 * Check whether the ladder's climb speed should be applied
	 */
	public boolean shouldApplyClimbing(World world, BlockPos pos, EntityLivingBase entity);
	
	/**
	 * Check whether the ladder's falling speed should be applied
	 */
	public boolean shouldApplyFalling(World world, BlockPos pos, EntityLivingBase entity);
	
	/**
	 * Get speed multipler for climbing the ladder
	 */
	public double climbSpeed(World world, BlockPos pos, EntityLivingBase entity);
	
	/**
	 * Get max speed multiplier for falling through ladder
	 * (max speed down is 0.15*mul blocks/tick)
	 */
	public double fallSpeed(World world, BlockPos pos, EntityLivingBase entity);
	
	/**
	 * Get speed limit multiplier for moving horizonally through ladder
	 * (max speed on X and Z axes is 0.15*mul blocks/tick)
	 */
	public double horizontalSpeed(World world, BlockPos pos, EntityLivingBase entity);
	
}
