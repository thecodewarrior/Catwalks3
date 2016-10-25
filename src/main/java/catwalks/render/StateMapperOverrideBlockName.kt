package catwalks.render

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class StateMapperOverrideBlockName(val rl: ResourceLocation) : StateMapperBase() {
    override fun getModelResourceLocation(state: IBlockState?): ModelResourceLocation {
        val rl = ModelResourceLocation(rl, this.getPropertyString(state?.getProperties()))
        return rl
    }
}
