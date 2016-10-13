package catwalks.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class AABBUtils {
	/**
	 * Offset side of an AxisAlignedBB
	 * @param side Side to change
	 * @param amount amount to offset, positive expands the AABB
	 */
	public static AxisAlignedBB offsetSide(AxisAlignedBB aabb, EnumFacing side, double amount) {
		switch (side) {
		case UP:
			return new AxisAlignedBB(
					aabb.minX,			aabb.minY,			aabb.minZ,
					aabb.maxX, 			aabb.maxY+amount, 	aabb.maxZ
				);
		case DOWN:
			return new AxisAlignedBB(
					aabb.minX,			aabb.minY-amount,	aabb.minZ,
					aabb.maxX,			aabb.maxY,			aabb.maxZ
				);
		case NORTH:
			return new AxisAlignedBB(
					aabb.minX,			aabb.minY, 			aabb.minZ-amount,
					aabb.maxX, 			aabb.maxY, 			aabb.maxZ
				);
		case SOUTH:
			return new AxisAlignedBB(
					aabb.minX,			aabb.minY, 			aabb.minZ,
					aabb.maxX, 			aabb.maxY, 			aabb.maxZ+amount
				);
		case EAST:
			return new AxisAlignedBB(
					aabb.minX-amount,	aabb.minY, 			aabb.minZ,
					aabb.maxX, 			aabb.maxY, 			aabb.maxZ
				);
		case WEST:
			return new AxisAlignedBB(
					aabb.minX, 			aabb.minY, 			aabb.minZ,
					aabb.maxX+amount, 	aabb.maxY, 			aabb.maxZ
				);
		default:
			return aabb;
		}
	}
}
