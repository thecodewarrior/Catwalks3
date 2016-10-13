package catwalks.part

import catwalks.CatwalksMod
import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.block.EnumDecoration
import catwalks.part.data.StairSides
import catwalks.util.meta.*
import mcmultipart.multipart.Multipart
import net.minecraft.util.EnumFacing

/**
 * Created by TheCodeWarrior
 */
class PartStair : Multipart(), IDirtyable, IDecoratable {

    protected var storage = MetaStorage(allocator, this)

    //region api stuff

    var catwalkMaterial: EnumCatwalkMaterial
        get() = MATERIAL.get(storage)
        set(value) = MATERIAL.set(storage, value)

    override fun addDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id)!!)
            return false
        DECOR.set(storage, id, true)
        return true
    }

    override fun removeDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id)!!)
            return false
        DECOR.set(storage, id, true)
        return true
    }

    override fun hasDecoration(decor: EnumDecoration): Boolean {
        val mat = catwalkMaterial
        val id = mat.getID(decor)
        if (id < 0)
            return false
        if (DECOR.get(storage, id)!!)
            return false
        DECOR.set(storage, id, true)
        return true
    }

    var facing: EnumFacing
        get() = FACING.get(storage)
        set(value) = FACING.set(storage, value)

    fun getSide(side: StairSides): Boolean {
        return SIDES.get(storage, side)!!
    }

    fun setSide(side: StairSides, value: Boolean) {
        SIDES.set(storage, side, value)
    }

    //endregion

    override fun markDirty() {
        super.markDirty()
    }

    companion object {
        val ID = Const.MODID + ":stair"

        protected var allocator = MetaStorage.Allocator()

        var MATERIAL = CatwalksMod.allocate_material(allocator)
        var DECOR = CatwalksMod.allocate_decor(allocator)

        var FACING = allocator.allocateArray("facing", EnumFacing.HORIZONTALS, 2)
        var SIDES = allocator.allocateBoolMap("sides", StairSides.values(), StairSides.values().size)
    }
}
