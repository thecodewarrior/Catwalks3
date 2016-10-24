package catwalks.part

import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.block.BlockScaffolding
import catwalks.part.data.ScaffoldRenderData
import catwalks.register.ItemRegister
import catwalks.util.GeneralUtil
import catwalks.util.meta.IDirtyable
import catwalks.util.meta.MetaStorage
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.ISolidPart
import mcmultipart.multipart.Multipart
import mcmultipart.raytrace.PartMOP
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class PartScaffold : Multipart(), ISolidPart, IDirtyable {

    protected var selectionBoxes: MutableList<AxisAlignedBB> = ArrayList()

    protected var storage = MetaStorage(allocator, this)

    init {
        val p = (1 / 16f).toDouble()
        val P = 1 - p
        val t = (4 / 16f).toDouble()
        val T = 1 - t
        selectionBoxes.add(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
    }

    var catwalkMaterial: EnumCatwalkMaterial
        get() = MATERIAL.get(storage)
        set(value) {
            if (value != catwalkMaterial)
                markDirty()
            MATERIAL.set(storage, value)
        }

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

    override fun canRenderInLayer(layer: BlockRenderLayer?): Boolean {
        return layer == catwalkMaterial.LAYER
    }

    //region Here be blockstates

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(MCMultiPartMod.multipart, arrayOf(Const.MATERIAL), arrayOf(Const.SCAFFOLD_RENDER_DATA))
    }

    override fun getActualState(state: IBlockState): IBlockState {
        return state.withProperty(Const.MATERIAL, MATERIAL.get(storage))
    }

    override fun getExtendedState(state: IBlockState): IBlockState {
        if(state !is IExtendedBlockState)
            return state

        val arr = BooleanArray(EnumFacing.values().size) { true }

        for(dir in EnumFacing.values()) {
            val poff = pos.offset(dir)
            if(world.isSideSolid(poff, dir.opposite)) {
                arr[dir.ordinal] = false
            }
            if(arr[dir.ordinal]) {
                val adjacent = world.getBlockState(poff)
                val block = adjacent.block
                if(block is BlockScaffolding) {
                    if(adjacent.getValue(Const.MATERIAL) == catwalkMaterial) {
                        arr[dir.ordinal] = false
                    }
                }
            }
            if(arr[dir.ordinal]) {
                val part = GeneralUtil.getPart(PartScaffold::class.java, world, poff)
                if(part != null && part.catwalkMaterial == catwalkMaterial) {
                    arr[dir.ordinal] = false
                }
            }
        }

        return state.withProperty(Const.SCAFFOLD_RENDER_DATA, ScaffoldRenderData(arr))
    }

    //endregion

    override fun isSideSolid(side: EnumFacing): Boolean {
        return true
    }

    override fun addSelectionBoxes(list: MutableList<AxisAlignedBB>?) {
        list!!.addAll(selectionBoxes)
    }

    override fun addCollisionBoxes(mask: AxisAlignedBB?, list: MutableList<AxisAlignedBB>?, collidingEntity: Entity?) {
        val axisalignedbb = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        if (mask!!.intersectsWith(axisalignedbb)) {
            list!!.add(axisalignedbb)
        }
    }

    override fun occlusionTest(part: IMultipart?): Boolean {
        return super.occlusionTest(part) || part is PartScaffold
    }

    override fun markDirty() {
        super.markDirty()
    }

    override fun getPickBlock(player: EntityPlayer?, hit: PartMOP?): ItemStack {
        return ItemStack(ItemRegister.scaffold, 1, catwalkMaterial.ordinal)
    }

    companion object {
        val ID = Const.MODID + ":scaffold"

        protected var allocator = MetaStorage.Allocator()
        var MATERIAL = allocator.allocateArray("material", EnumCatwalkMaterial.values(), 7)
    }
}
