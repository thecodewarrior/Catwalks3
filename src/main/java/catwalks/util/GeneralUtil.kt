package catwalks.util

import catwalks.Const
import com.google.common.collect.ImmutableList
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.MultipartHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*
import java.util.function.Predicate

object GeneralUtil {
    private val RANDOM = Random()

    fun <T : IMultipart> getParts(clazz: Class<T>, world: IBlockAccess, pos: BlockPos, exact: Boolean): List<T> {
        val container = MultipartHelper.getPartContainer(world, pos)
        if (container != null) {
            val list = ArrayList<T>()
            val parts = container.parts
            for (part in parts) {
                if (exact) {
                    if (clazz == part.javaClass) {
                        list.add(part as T)
                    }
                } else {
                    if (clazz.isAssignableFrom(part.javaClass)) {
                        list.add(part as T)
                    }
                }
            }
            return list
        }
        return listOf()
    }

    fun <T : IMultipart> getPart(clazz: Class<T>, world: IBlockAccess, pos: BlockPos, exact: Boolean): T? {
        val list = getParts(clazz, world, pos, exact)
        if (list.size == 0)
            return null
        return list[0]
    }

    fun <T : IMultipart> getParts(clazz: Class<T>, world: IBlockAccess, pos: BlockPos): List<T> {
        return getParts(clazz, world, pos, false)
    }

    fun <T : IMultipart> getPart(clazz: Class<T>, world: IBlockAccess, pos: BlockPos): T? {
        return getPart(clazz, world, pos, false)
    }

    fun isHolding(player: EntityPlayer, test: Predicate<ItemStack>): Boolean {
        if (player.heldItemMainhand != null && test.test(player.heldItemMainhand))
            return true
        if (player.heldItemOffhand != null && test.test(player.heldItemOffhand))
            return true
        return false
    }

    fun getHeld(player: EntityPlayer, test: Predicate<ItemStack>): ItemStack? {
        if (player.heldItemMainhand != null && test.test(player.heldItemMainhand))
            return player.heldItemMainhand
        if (player.heldItemOffhand != null && test.test(player.heldItemOffhand))
            return player.heldItemOffhand
        return null
    }

    fun snapToGrid(`in`: Vec3d, gridSize: Double): Vec3d {
        return Vec3d(
                snapToNearestMultiple(`in`.xCoord, gridSize),
                snapToNearestMultiple(`in`.yCoord, gridSize),
                snapToNearestMultiple(`in`.zCoord, gridSize))
    }

    fun snapToNearestMultiple(`in`: Double, multiple: Double): Double {
        return multiple * Math.round(`in` / multiple)
    }

    fun markForUpdate(world: World, pos: BlockPos) {
        val state = world.getBlockState(pos)
        world.notifyBlockUpdate(pos, state, state, 8)
    }

    fun getWorldPosLogInfo(world: World, pos: BlockPos): String {
        return String.format("(%d, %d, %d) in dim %s (%d)", pos.x, pos.y, pos.z, world.provider.dimensionType.getName(), world.provider.dimension)
    }

    fun spawnItemStack(worldIn: World, x: Double, y: Double, z: Double, stack: ItemStack) {
        val f = RANDOM.nextFloat() * 0.8f + 0.1f
        val f1 = RANDOM.nextFloat() * 0.8f + 0.1f
        val f2 = RANDOM.nextFloat() * 0.8f + 0.1f

        while (stack.stackSize > 0) {
            var i = RANDOM.nextInt(21) + 10

            if (i > stack.stackSize) {
                i = stack.stackSize
            }

            stack.stackSize -= i
            val entityitem = EntityItem(worldIn, x + f.toDouble(), y + f1.toDouble(), z + f2.toDouble(), ItemStack(stack.item, i, stack.metadata))

            if (stack.hasTagCompound()) {
                entityitem.entityItem.setTagCompound(stack.tagCompound!!.copy() as NBTTagCompound)
            }

            val f3 = 0.05f
            entityitem.motionX = RANDOM.nextGaussian() * f3.toDouble()
            entityitem.motionY = RANDOM.nextGaussian() * f3.toDouble() + 0.20000000298023224
            entityitem.motionZ = RANDOM.nextGaussian() * f3.toDouble()
            worldIn.spawnEntityInWorld(entityitem)
        }
    }

    // private so it doesn't conflict with the method when autocompleting.
    private val APPROX_EQ_ACC = (1 / 10000f).toDouble()

    fun approxEq(a: Double, b: Double): Boolean {
        return a <= b + APPROX_EQ_ACC && a >= b - APPROX_EQ_ACC
    }

    fun getAABB(point1: Vec3d, point2: Vec3d): AxisAlignedBB {
        return AxisAlignedBB(
                Math.min(point1.xCoord, point2.xCoord),
                Math.min(point1.yCoord, point2.yCoord),
                Math.min(point1.zCoord, point2.zCoord),

                Math.max(point1.xCoord, point2.xCoord),
                Math.max(point1.yCoord, point2.yCoord),
                Math.max(point1.zCoord, point2.zCoord))
    }

    fun getAABBSide(aabb: AxisAlignedBB, side: EnumFacing): Double {
        when (side) {
            EnumFacing.UP -> return aabb.maxY
            EnumFacing.DOWN -> return aabb.minY
            EnumFacing.NORTH -> return aabb.minZ
            EnumFacing.SOUTH -> return aabb.maxZ
            EnumFacing.WEST -> return aabb.minX
            EnumFacing.EAST -> return aabb.maxX
            else -> return 0.0
        }
    }

    fun simulateEntityMove(entity: Entity, movement: Vec3d): Vec3d {

        var x = movement.xCoord
        var y = movement.yCoord
        var z = movement.zCoord

        val collisionBoxes = entity.worldObj.getCollisionBoxes(entity, entity.entityBoundingBox.addCoord(x, y, z))
        var entityBox = entity.entityBoundingBox
        var i = 0

        val j = collisionBoxes.size
        while (i < j) {
            y = (collisionBoxes[i] as AxisAlignedBB).calculateYOffset(entityBox, y)
            ++i
        }

        entityBox = entityBox.offset(0.0, y, 0.0)
        var j4 = 0

        val k = collisionBoxes.size
        while (j4 < k) {
            x = (collisionBoxes[j4] as AxisAlignedBB).calculateXOffset(entityBox, x)
            ++j4
        }

        entityBox = entityBox.offset(x, 0.0, 0.0)
        j4 = 0

        val k4 = collisionBoxes.size
        while (j4 < k4) {
            z = (collisionBoxes[j4] as AxisAlignedBB).calculateZOffset(entityBox, z)
            ++j4
        }

        entityBox = entityBox.offset(0.0, 0.0, z)

        return Vec3d(x, y, z)
    }

    fun getDesiredMoveVector(entity: EntityLivingBase): Vec3d {

        val f = MathHelper.cos(-entity.rotationYawHead * 0.017453292f - Math.PI.toFloat())
        val f1 = MathHelper.sin(-entity.rotationYawHead * 0.017453292f - Math.PI.toFloat())
        val look = Vec3d((-f1).toDouble(), 0.0, (-f).toDouble())

        val forwardVec = Vec3d(look.xCoord, 0.0, look.zCoord).normalize().scale(entity.moveForward.toDouble())
        val straifVec = Vec3d(look.zCoord, 0.0, -look.xCoord).normalize().scale(entity.moveStrafing.toDouble())

        return forwardVec.add(straifVec).normalize()
    }

    fun getRotation(from: EnumFacing?, to: EnumFacing?): Int {
        if (from == null || to == null)
            return 0
        if (from.axis == Axis.Y || to.axis == Axis.Y) {
            return 0
        }
        return to.horizontalIndex - from.horizontalIndex
    }

    fun rotateVectorCenter(rotation: Int, vec: Vec3d): Vec3d {
        return rotateVector(rotation, vec.add(Const.VEC_ANTICENTER)).add(Const.VEC_CENTER)
    }

    fun rotateVector(rotation: Int, vec: Vec3d): Vec3d {
        val out: Vec3d
        var i = rotation % EnumFacing.HORIZONTALS.size
        if (i < 0)
            i = 4 + i
        when (i) {
            0 -> out = Vec3d(vec.xCoord, vec.yCoord, vec.zCoord)
            1 -> out = Vec3d(-vec.zCoord, vec.yCoord, vec.xCoord)
            2 -> out = Vec3d(-vec.xCoord, vec.yCoord, -vec.zCoord)
            3 -> out = Vec3d(vec.zCoord, vec.yCoord, -vec.xCoord)
            else -> out = vec
        }

        return out
    }
}
