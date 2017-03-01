package catwalks.render.part

import catwalks.Const
import catwalks.render.ModelHandle
import catwalks.render.QuadManager
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Created by TheCodeWarrior
 */
class ScaffoldBakedModel(loc: String, modelState: IModelState) : BaseBakedModel(loc, modelState) {

    private val handle: ModelHandle

    init {
        val rl = ResourceLocation(loc)
        val newrl = ResourceLocation(rl.resourceDomain, rl.resourcePath.substring("models/".length))
        handle = ModelHandle.of(newrl)
    }

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
