package catwalks.part

import mcmultipart.multipart.INormallyOccludingPart
import mcmultipart.multipart.ISolidPart
import mcmultipart.multipart.Multipart
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by TheCodeWarrior
 */
class PartLadder : Multipart(), INormallyOccludingPart, ISolidPart {

    override fun addOcclusionBoxes(list: MutableList<AxisAlignedBB>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSideSolid(side: EnumFacing?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
