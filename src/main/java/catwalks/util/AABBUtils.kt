package catwalks.util

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

object AABBUtils {
    /**
     * Offset side of an AxisAlignedBB
     * @param side Side to change
     * *
     * @param amount amount to offset, positive expands the AABB
     */
    fun offsetSide(aabb: AxisAlignedBB, side: EnumFacing, amount: Double): AxisAlignedBB {
        when (side) {
            EnumFacing.UP -> return AxisAlignedBB(
                    aabb.minX, aabb.minY, aabb.minZ,
                    aabb.maxX, aabb.maxY + amount, aabb.maxZ)
            EnumFacing.DOWN -> return AxisAlignedBB(
                    aabb.minX, aabb.minY - amount, aabb.minZ,
                    aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.NORTH -> return AxisAlignedBB(
                    aabb.minX, aabb.minY, aabb.minZ - amount,
                    aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.SOUTH -> return AxisAlignedBB(
                    aabb.minX, aabb.minY, aabb.minZ,
                    aabb.maxX, aabb.maxY, aabb.maxZ + amount)
            EnumFacing.EAST -> return AxisAlignedBB(
                    aabb.minX - amount, aabb.minY, aabb.minZ,
                    aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.WEST -> return AxisAlignedBB(
                    aabb.minX, aabb.minY, aabb.minZ,
                    aabb.maxX + amount, aabb.maxY, aabb.maxZ)
            else -> return aabb
        }
    }
}
