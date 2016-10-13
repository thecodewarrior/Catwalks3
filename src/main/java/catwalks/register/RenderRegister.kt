package catwalks.register

import catwalks.CatwalksMod
import catwalks.register.ItemRegister.renderRegsiterItems
import catwalks.render.part.CatwalkBakedModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by TheCodeWarrior
 */
@SideOnly(Side.CLIENT)
class RenderRegister {
    object Parts {
        fun initRender() {
            ModelLoaderRegistry.registerLoader(CatwalkBakedModel.ModelLoader())
            //			MultipartRegistryClient.registerSpecialPartStateMapper(new ResourceLocation(PartScaffold.ID), new CatwalkBakedModel.Statemapper());
        }
    }

    object Blocks {
        fun initRender() {
            val rl = ResourceLocation(CatwalksMod.MODID, "scaffold")
            BlockRegister.scaffolds.forEach {
                ModelLoader.setCustomStateMapper(it, object : StateMapperBase() {
                    override fun getModelResourceLocation(state: IBlockState?): ModelResourceLocation {
                        return ModelResourceLocation(rl, this.getPropertyString(state?.getProperties()))
                    }
                })
            }
        }
    }

    object Items {
        @SideOnly(Side.CLIENT)
        fun initRender() {
            registerRender()
        }

        @SideOnly(Side.CLIENT)
        private fun registerRender() {
            for (item in renderRegsiterItems) {
                val customVariants = item.customRenderVariants
                if (customVariants == null) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName, ""))
                } else {
                    for (i in customVariants.indices) {
                        if ("" == customVariants[i]) {
                            ModelLoader.setCustomModelResourceLocation(item, i, ModelResourceLocation(item.registryName, ""))
                        } else {
                            ModelLoader.setCustomModelResourceLocation(item, i, ModelResourceLocation("${item.registryName}.${customVariants[i]}", ""))
                        }
                    }
                }
                //			item.initModel();
            }
        }
    }
}
