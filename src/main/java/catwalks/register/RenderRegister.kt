package catwalks.register

import catwalks.CatwalksMod
import catwalks.register.ItemRegister.renderRegsiterItems
import catwalks.render.part.CatwalkBakedModel
import catwalks.render.part.ScaffoldModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by TheCodeWarrior
 */
@SideOnly(Side.CLIENT)
object RenderRegister {
    object Parts {
        fun initRender() {
            ModelLoaderRegistry.registerLoader(CatwalkBakedModel.ModelLoader())
            ModelLoaderRegistry.registerLoader(ScaffoldModel.ModelLoader())
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
                if(item !is Item)
                    continue
                val customVariants = item.customRenderVariants
                if (customVariants == null) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName, ""))
                } else {
                    for (i in customVariants.indices) {
                        if ("" == customVariants[i]) {
                            ModelLoader.setCustomModelResourceLocation(item, i, ModelResourceLocation(item.registryName, ""))
                        } else if('#' in customVariants[i]) {
                            val loc = customVariants[i].indexOf('#')
                            val file = customVariants[i].substring(0, loc)
                            val variant = customVariants[i].substring(loc+1)
                            ModelLoader.setCustomModelResourceLocation(item, i, ModelResourceLocation(ResourceLocation(item.registryName.resourceDomain, file), variant))
                        } else {
                            ModelLoader.setCustomModelResourceLocation(item, i, ModelResourceLocation("${item.registryName}.${customVariants[i]}", ""))
                        }
                    }
                }
            }
        }
    }
}
