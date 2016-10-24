package catwalks.render

import catwalks.SCREAM_AT_DEV
import com.google.common.collect.Lists
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.util.EnumFacing

/**
 * Created by TheCodeWarrior
 */
class QuadManager(internal var state: IBlockState, internal var side: EnumFacing?, internal var rand: Long) {
    internal var quads: MutableList<BakedQuad> = Lists.newArrayList<BakedQuad>()

    fun add(handle: ModelHandle?) {
        if (handle == null) {
            SCREAM_AT_DEV()
            return
        }
        add(handle.get())
    }

    fun add(handle: StateHandle?) {
        if (handle == null) {
            SCREAM_AT_DEV()
            return
        }
        add(handle.get())
    }

    fun add(model: IBakedModel?) {
        if (model == null) {
            SCREAM_AT_DEV()
            return
        }
        quads.addAll(model.getQuads(state, side, rand))
    }

    fun getQuads(): List<BakedQuad> {
        return quads
    }
}
