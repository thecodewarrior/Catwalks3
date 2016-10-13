package catwalks.register

import catwalks.register.ItemRegister.renderRegsiterItems
import catwalks.render.part.CatwalkBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
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
