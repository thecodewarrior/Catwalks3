package catwalks.util


import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class AABB : AxisAlignedBB {

    constructor(otherBB: AxisAlignedBB) : super(otherBB.minX, otherBB.minY, otherBB.minZ, otherBB.maxX, otherBB.maxY, otherBB.maxZ) {
    }

    constructor(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) : super(x1, y1, z1, x2, y2, z2) {
    }

    constructor(pos: BlockPos) : super(pos) {
    }

    constructor(pos1: BlockPos, pos2: BlockPos) : super(pos1, pos2) {
    }

    constructor(vec1: Vec3d, vec2: Vec3d) : super(vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord) {
    }

    /**
     * Offset side of an AxisAlignedBB
     * @param side Side to change
     * *
     * @param amount amount to offset, positive expands the AABB
     */
    fun expand(side: EnumFacing, amount: Double): AABB {

        var minX = this.minX
        var minY = this.minY
        var minZ = this.minZ

        var maxX = this.maxX
        var maxY = this.maxY
        var maxZ = this.maxZ


        when (side) {
            EnumFacing.UP -> {
                maxY += amount
                minY -= amount
                minZ -= amount
                maxZ += amount
                minX -= amount
                maxX += amount
            }
            EnumFacing.DOWN -> {
                minY -= amount
                minZ -= amount
                maxZ += amount
                minX -= amount
                maxX += amount
            }
            EnumFacing.NORTH -> {
                minZ -= amount
                maxZ += amount
                minX -= amount
                maxX += amount
            }
            EnumFacing.SOUTH -> {
                maxZ += amount
                minX -= amount
                maxX += amount
            }
            EnumFacing.EAST -> {
                minX -= amount
                maxX += amount
            }
            EnumFacing.WEST -> maxX += amount
        }

        return AABB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun rotate(rotation: Int): AABB {

        var minX = this.minX
        var minY = this.minY
        var minZ = this.minZ

        var maxX = this.maxX
        var maxY = this.maxY
        var maxZ = this.maxZ

        val center = Vec3d((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0)

        // min
        var vec = Vec3d(minX, minY, minZ).subtract(center)
        vec = GeneralUtil.rotateVector(rotation, vec)
        vec = vec.add(center)

        minX = vec.xCoord
        minY = vec.yCoord
        minZ = vec.zCoord

        // max
        vec = Vec3d(maxX, maxY, maxZ).subtract(center)
        vec = GeneralUtil.rotateVector(rotation, vec)
        vec = vec.add(center)

        maxX = vec.xCoord
        maxY = vec.yCoord
        maxZ = vec.zCoord

        return AABB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    //#########################################################################
    //############### Reimplementing with correct return types ################
    //#########################################################################

    override fun setMaxY(y2: Double): AABB {
        return AABB(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ)
    }

    /**
     * Adds a coordinate to the bounding box, extending it if the point lies outside the current ranges.
     */
    override fun addCoord(x: Double, y: Double, z: Double): AABB {
        var d0 = this.minX
        var d1 = this.minY
        var d2 = this.minZ
        var d3 = this.maxX
        var d4 = this.maxY
        var d5 = this.maxZ

        if (x < 0.0) {
            d0 += x
        } else if (x > 0.0) {
            d3 += x
        }

        if (y < 0.0) {
            d1 += y
        } else if (y > 0.0) {
            d4 += y
        }

        if (z < 0.0) {
            d2 += z
        } else if (z > 0.0) {
            d5 += z
        }

        return AABB(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Creates a new bounding box that has been expanded. If negative values are used, it will shrink.
     */
    override fun expand(x: Double, y: Double, z: Double): AABB {
        val d0 = this.minX - x
        val d1 = this.minY - y
        val d2 = this.minZ - z
        val d3 = this.maxX + x
        val d4 = this.maxY + y
        val d5 = this.maxZ + z
        return AABB(d0, d1, d2, d3, d4, d5)
    }

    override fun expandXyz(value: Double): AABB {
        return this.expand(value, value, value)
    }

    override fun union(other: AxisAlignedBB): AABB {
        val d0 = Math.min(this.minX, other.minX)
        val d1 = Math.min(this.minY, other.minY)
        val d2 = Math.min(this.minZ, other.minZ)
        val d3 = Math.max(this.maxX, other.maxX)
        val d4 = Math.max(this.maxY, other.maxY)
        val d5 = Math.max(this.maxZ, other.maxZ)
        return AABB(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    override fun offset(x: Double, y: Double, z: Double): AABB {
        return AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z)
    }

    override fun offset(pos: BlockPos): AABB {
        return AABB(this.minX + pos.x.toDouble(), this.minY + pos.y.toDouble(), this.minZ + pos.z.toDouble(), this.maxX + pos.x.toDouble(), this.maxY + pos.y.toDouble(), this.maxZ + pos.z.toDouble())
    }

    override fun contract(value: Double): AABB {
        return this.expandXyz(-value)
    }
}
