package catwalks.part

import catwalks.CatwalksMod
import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.EnumDecoration
import catwalks.part.data.CatwalkRenderData
import catwalks.register.ItemRegister
import catwalks.util.GeneralUtil
import catwalks.util.NeighborCache
import catwalks.util.meta.IDirtyable
import catwalks.util.meta.MetaStorage
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.*
import mcmultipart.raytrace.PartMOP
import mcmultipart.raytrace.RayTraceUtils
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class PartCatwalk : Multipart(), ISlottedPart, INormallyOccludingPart, ISolidPart, IDirtyable, IDecoratable {

    protected var storage = MetaStorage(allocator, this)

    //region click stuff

    override fun onActivated(player: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, hit: PartMOP?): Boolean {
        if (heldItem != null && heldItem.item === ItemRegister.tool) {
            setSide(hit!!.sideHit, !getSide(hit.sideHit))
            return true
        }
        return false
    }

    override fun getPickBlock(player: EntityPlayer?, hit: PartMOP?): ItemStack {
        return ItemStack(ItemRegister.catwalk, 1, catwalkMaterial.ordinal)
    }

    //endregion

    //region decoration stuff

    override fun addDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id))
            return false
        DECOR.set(storage, id, true)
        return true
    }

    override fun removeDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id))
            return false
        DECOR.set(storage, id, true)
        return true
    }

    override fun hasDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id))
            return false
        DECOR.set(storage, id, true)
        return true
    }

    //endregion

    //region api stuff
    var catwalkMaterial: EnumCatwalkMaterial
        get() = MATERIAL.get(storage)
        set(value) = MATERIAL.set(storage, value)

    fun getSide(side: EnumFacing): Boolean {
        return SIDES.get(storage, side)
    }

    fun setSide(side: EnumFacing, value: Boolean) {
        SIDES.set(storage, side, value)
    }
    //endregion

    //region collision and raytrace stuff

    override fun addCollisionBoxes(mask: AxisAlignedBB?, list: MutableList<AxisAlignedBB>?, collidingEntity: Entity?) {
        for (facing in EnumFacing.values()) {
            if (facing == EnumFacing.UP)
                continue
            if (SIDES.get(storage, facing)) {
                val box = sideBoxes[facing.ordinal]
                if (mask!!.intersectsWith(box))
                    list!!.add(box)
            }
        }
    }

    override fun collisionRayTrace(start: Vec3d, end: Vec3d): RayTraceUtils.AdvancedRayTraceResultPart? {
        val list = ArrayList<AxisAlignedBB>()
        addSelectionBoxes(list)
        val result = RayTraceUtils.collisionRayTrace(world, pos, start, end, list) ?: return null

        val hit = result.hit.hitVec.subtract(Vec3d(pos))
        var sideHit = result.hit.sideHit
        if (hit.yCoord == 0.0) {
            sideHit = EnumFacing.DOWN
        } else if (hit.yCoord == 1.0) {
            sideHit = EnumFacing.UP
        } else if (hit.zCoord == 0.0) {
            sideHit = EnumFacing.NORTH
        } else if (hit.zCoord == 1.0) {
            sideHit = EnumFacing.SOUTH
        } else if (hit.xCoord == 0.0) {
            sideHit = EnumFacing.WEST
        } else if (hit.xCoord == 1.0) {
            sideHit = EnumFacing.EAST
        }
        result.hit.sideHit = sideHit

        return RayTraceUtils.AdvancedRayTraceResultPart(result, this)
    }

    override fun addSelectionBoxes(list: MutableList<AxisAlignedBB>?) {
        list!!.addAll(sideBoxes)
    }

    //endregion

    //region mcmultipart stuff

    override fun onAdded() {
        super.onAdded()
        val container = container
        for (facing in EnumFacing.values()) {
            if (facing == EnumFacing.UP)
                continue
            if (OcclusionHelper.slotOcclusionTest(PartSlot.getFaceSlot(facing), container))
                SIDES.set(storage, facing, true)
        }
        if(world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP))
            SIDES.set(storage, EnumFacing.DOWN, false)
        onPlaceUpdateSides()
    }

    override fun onRemoved() {
        onBreakUpdateSides()
    }

    override fun addOcclusionBoxes(list: MutableList<AxisAlignedBB>) {
        for (facing in EnumFacing.values()) {
            if (facing == EnumFacing.UP)
                continue
            if (SIDES.get(storage, facing))
                list.add(sideBoxes[facing.ordinal])
        }
    }

    override fun getSlotMask(): EnumSet<PartSlot> {
        val slots = EnumSet.noneOf<PartSlot>(PartSlot::class.java)
        for (facing in EnumFacing.values()) {
            if (facing == EnumFacing.UP)
                continue
            if (SIDES.get(storage, facing))
                slots.add(PartSlot.getFaceSlot(facing))
        }
        return slots
    }

    override fun isSideSolid(side: EnumFacing): Boolean {
        return SIDES.get(storage, side)
    }

    override fun occlusionTest(part: IMultipart?): Boolean {
        return super.occlusionTest(part) && part !is PartCatwalk
    }

    // endregion

    //region nbt and packet stuff
    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        val sup = super.writeToNBT(tag)
        sup.setByteArray("m", storage.toByteArray())
        return sup
    }

    override fun readFromNBT(tag: NBTTagCompound?) {
        super.readFromNBT(tag)
        storage.fromByteArray(tag!!.getByteArray("m"))
    }

    override fun writeUpdatePacket(buf: PacketBuffer?) {
        super.writeUpdatePacket(buf)
        buf!!.writeByteArray(storage.toByteArray())
    }

    override fun readUpdatePacket(buf: PacketBuffer?) {
        super.readUpdatePacket(buf)
        storage.fromByteArray(buf!!.readByteArray())
    }
    //endregion

    //region model


    override fun canRenderInLayer(layer: BlockRenderLayer?): Boolean {
        return layer == catwalkMaterial.LAYER
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(MCMultiPartMod.multipart, arrayOf<IProperty<*>>(Const.MATERIAL), arrayOf<IUnlistedProperty<*>>(Const.CATWALK_RENDER_DATA))
    }

    override fun getActualState(state: IBlockState): IBlockState {
        return state.withProperty(Const.MATERIAL, MATERIAL.get(storage))
    }

    override fun getExtendedState(state: IBlockState): IBlockState {
        val estate = state as IExtendedBlockState
        val renderData = CatwalkRenderData()

        val cache = caches.get()
        cache.init(pos) { pos -> GeneralUtil.getPart<PartCatwalk>(PartCatwalk::class.java, world, pos) }

        for (d in EnumFacing.HORIZONTALS) {
            if (!getSide(d))
                continue

            val right = d.rotateY()
            val left = d.rotateYCCW()

            val data = CatwalkRenderData.CatwalkSideRenderData()
            renderData.sides.put(d, data)

            data.right = sideLogic(cache, d, right)
            data.left = sideLogic(cache, d, left)
        }

        renderData.corner_ne = cornerLogic(cache, EnumFacing.NORTH, EnumFacing.EAST)
        renderData.corner_nw = cornerLogic(cache, EnumFacing.NORTH, EnumFacing.WEST)
        renderData.corner_se = cornerLogic(cache, EnumFacing.SOUTH, EnumFacing.EAST)
        renderData.corner_sw = cornerLogic(cache, EnumFacing.SOUTH, EnumFacing.WEST)

        renderData.bottom = if (getSide(EnumFacing.DOWN)) EnumFacing.Axis.Z else null
        if (renderData.bottom != null) {
            renderData.bottomNorth = bottomLogic(cache, EnumFacing.NORTH)
            renderData.bottomSouth = bottomLogic(cache, EnumFacing.SOUTH)
            renderData.bottomEast = bottomLogic(cache, EnumFacing.EAST)
            renderData.bottomWest = bottomLogic(cache, EnumFacing.WEST)

            renderData.bottomNE = renderData.bottomNorth || renderData.bottomEast || bottomCornerLogic(cache, EnumFacing.NORTH, EnumFacing.EAST)
            renderData.bottomNW = renderData.bottomNorth || renderData.bottomWest || bottomCornerLogic(cache, EnumFacing.NORTH, EnumFacing.WEST)
            renderData.bottomSE = renderData.bottomSouth || renderData.bottomEast || bottomCornerLogic(cache, EnumFacing.SOUTH, EnumFacing.EAST)
            renderData.bottomSW = renderData.bottomSouth || renderData.bottomWest || bottomCornerLogic(cache, EnumFacing.SOUTH, EnumFacing.WEST)

            if (renderData.bottomEast && renderData.bottomWest && !(renderData.bottomNorth && renderData.bottomSouth)) {
                renderData.bottom = EnumFacing.Axis.X
            }
        }
        cache.clear()
        return estate.withProperty(Const.CATWALK_RENDER_DATA, renderData)
    }

    private fun bottomLogic(cache: NeighborCache<PartCatwalk?>, dir: EnumFacing): Boolean {
        if (getSide(dir))
            return true
        val adjacent = cache.get(dir)

        if (adjacent != null) {
            if (adjacent.catwalkMaterial == catwalkMaterial && !adjacent.getSide(dir.opposite) && adjacent.getSide(EnumFacing.DOWN)) {
                return false
            }
        }

        return true
    }

    private fun bottomCornerLogic(cache: NeighborCache<PartCatwalk?>, front: EnumFacing, side: EnumFacing): Boolean {
        if (getSide(front) || getSide(side))
            return true
        val ahead = cache.get(front)
        val adjacent = cache.get(side)
        val diagonal = cache.get(side, front)

        if (adjacent != null && ahead != null && diagonal != null) {

            if (adjacent.catwalkMaterial == catwalkMaterial && ahead.catwalkMaterial == catwalkMaterial && diagonal.catwalkMaterial == catwalkMaterial &&
                    adjacent.getSide(EnumFacing.DOWN) && ahead.getSide(EnumFacing.DOWN) && diagonal.getSide(EnumFacing.DOWN) &&
                    !(adjacent.getSide(front) || diagonal.getSide(front.opposite) ||
                            ahead.getSide(side) || diagonal.getSide(side.opposite))) {
                return false
            }
        }

        return true
    }

    private fun cornerLogic(cache: NeighborCache<PartCatwalk?>, front: EnumFacing, side: EnumFacing): CatwalkRenderData.EnumCatwalkCornerType? {

        if (getSide(front) && getSide(side))
            return CatwalkRenderData.EnumCatwalkCornerType.INNER

        if (getSide(front) || getSide(side))
            return null

        val ahead = cache.get(front)
        val adjacent = cache.get(side)

        if (ahead == null || adjacent == null)
            return null
        if(adjacent.catwalkMaterial != catwalkMaterial || ahead.catwalkMaterial != catwalkMaterial)
            return null

        if (adjacent.getSide(front) && !adjacent.getSide(side.opposite) &&
                !ahead.getSide(front.opposite) && ahead.getSide(side)) {
            return CatwalkRenderData.EnumCatwalkCornerType.OUTER
        }

        val diagonal = cache.get(side, front)

        if (diagonal != null) {
            if(diagonal.catwalkMaterial != catwalkMaterial)
                return null
            if (!adjacent.getSide(side.opposite) && !adjacent.getSide(front) &&
                    !diagonal.getSide(front.opposite) && diagonal.getSide(side.opposite) &&
                    !ahead.getSide(front.opposite) && ahead.getSide(side)) {
                return CatwalkRenderData.EnumCatwalkCornerType.OUTER_180
            }

            if (!adjacent.getSide(side.opposite) && adjacent.getSide(front) &&
                    !ahead.getSide(side) && !ahead.getSide(front.opposite) &&
                    diagonal.getSide(front.opposite) && !diagonal.getSide(side.opposite)) {
                return CatwalkRenderData.EnumCatwalkCornerType.OUTER_180
            }
        }
        return null
    }

    private fun sideLogic(cache: NeighborCache<PartCatwalk?>, front: EnumFacing, side: EnumFacing): CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType {
        if (getSide(side)) {
            return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.INNER_CORNER
        }

        val adjacent = cache.get(side)
        var diagonal: PartCatwalk? = null

        if (adjacent != null) {
            if(adjacent.catwalkMaterial != catwalkMaterial)
                return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END

            if (adjacent.getSide(front) && !adjacent.getSide(side.opposite)) {
                return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE
            }
            diagonal = cache.get(front, side) // moved here for efficiency
            // corner end logic
            if (diagonal != null) {
                if(diagonal.catwalkMaterial != catwalkMaterial)
                    return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END
                if (!adjacent.getSide(front) && !adjacent.getSide(side.opposite) &&
                        diagonal.getSide(side.opposite) && !diagonal.getSide(front.opposite)) {
                    return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.OUTER_CORNER
                }
            }
        }

        val ahead = cache.get(front)

        // 180° wrap around end logic
        if (adjacent != null && diagonal != null && ahead != null) {
            if(ahead.catwalkMaterial != catwalkMaterial)
                return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END
            if (ahead.getSide(front.opposite) && !ahead.getSide(side) &&
                    !adjacent.getSide(side.opposite) && !adjacent.getSide(front) &&
                    !diagonal.getSide(front.opposite) && !diagonal.getSide(side.opposite)) {
                return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.OUTER_CORNER_180
            }
        }
        return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END
    }

    //endregion

    //region auto-open stuff

    fun onPlaceUpdateSides() {
        for (f in EnumFacing.HORIZONTALS) {
            val part = GeneralUtil.getPart<PartCatwalk>(PartCatwalk::class.java, world, pos.offset(f))

            setSide(f, part == null)
            part?.setSide(f.opposite, false)
        }
    }

    fun onBreakUpdateSides() {
        for (f in EnumFacing.HORIZONTALS) {
            val part = GeneralUtil.getPart<PartCatwalk>(PartCatwalk::class.java, world, pos.offset(f))
            part?.setSide(f.opposite, true)
        }
    }

    //endregion

    override fun markDirty() {
        super.markDirty()
        sendUpdatePacket(true)
    }

    companion object {
        val ID = Const.MODID + ":catwalk"
        val caches: ThreadLocal<NeighborCache<PartCatwalk?>> = object : ThreadLocal<NeighborCache<PartCatwalk?>>() {
            override fun initialValue(): NeighborCache<PartCatwalk?> {
                return NeighborCache()
            }
        }

        @SuppressWarnings("unchecked")
        var sideBoxes = listOf(
                AxisAlignedBB(0.0, 0.0, 0.0, /**/ 1.0, 0.0, 1.0), // down
                AxisAlignedBB(0.0, 0.0, 0.0, /**/ 0.0, 0.0, 0.0), // up
                AxisAlignedBB(0.0, 0.0, 0.0, /**/ 1.0, 1.0, 0.0), // north
                AxisAlignedBB(0.0, 0.0, 1.0, /**/ 1.0, 1.0, 1.0), // south
                AxisAlignedBB(0.0, 0.0, 0.0, /**/ 0.0, 1.0, 1.0), // west
                AxisAlignedBB(1.0, 0.0, 0.0, /**/ 1.0, 1.0, 1.0)  // east
        )

        var SIDE_LIST = arrayOf(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST)

        protected var allocator = MetaStorage.Allocator()
        var MATERIAL = CatwalksMod.allocate_material(allocator)
        var DECOR = CatwalksMod.allocate_decor(allocator)
        var SIDES = allocator.allocateBoolMap("sides", EnumFacing.values(), 6)
    }
}
