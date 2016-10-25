package catwalks.render.part

import catwalks.Const
import catwalks.render.QuadManager
import catwalks.render.StateHandle
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Created by TheCodeWarrior
 */
class ScaffoldBakedModel(loc: String) : BaseBakedModel(loc) {

    private val handle: StateHandle = StateHandle.of(ModelResourceLocation(loc))

    override fun getQuads(normalState: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (normalState == null || normalState !is IExtendedBlockState)
            return emptyList()

        val qm = QuadManager(normalState, side, rand)

        val data = normalState.getValue(Const.SCAFFOLD_RENDER_DATA)

        if(data.sides[side?.ordinal ?: 0])
            qm.add(handle)

        return qm.getQuads()
    }
}
