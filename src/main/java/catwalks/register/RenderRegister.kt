package catwalks.register

import catwalks.CatwalksMod
import catwalks.register.ItemRegister.renderRegisterItems
import catwalks.render.CustomModelHandler
import catwalks.render.StateMapperOverrideBlockName
import catwalks.render.part.CatwalkBakedModel
import catwalks.render.part.ScaffoldBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
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
            //			MultipartRegistryClient.registerSpecialPartStateMapper(new ResourceLocation(PartScaffold.ID), new CatwalkBakedModel.Statemapper());
        }
    }

    object Blocks {
        fun initRender() {

            ModelLoaderRegistry.registerLoader(CustomModelHandler)
            CustomModelHandler.register("catwalksDynamic", ::CatwalkBakedModel)
            CustomModelHandler.register("scaffoldSides", ::ScaffoldBakedModel)

            val rl = ResourceLocation(CatwalksMod.MODID, "scaffold")
            BlockRegister.scaffolds.forEach {
                ModelLoader.setCustomStateMapper(it, StateMapperOverrideBlockName(rl))
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
            for (item in renderRegisterItems) {
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
