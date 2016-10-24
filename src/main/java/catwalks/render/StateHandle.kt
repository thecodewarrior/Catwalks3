package catwalks.render

import catwalks.Const
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.crash.CrashReport
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by TheCodeWarrior
 */
class StateHandle(val loc: ModelResourceLocation) {

    fun get(): IBakedModel {
        return getModel(this.loc)
    }

    fun load(): StateHandle {
        getModel(this.loc)
        return this
    }

    fun reload(): StateHandle {
        loadModel(this.loc)
        return this
    }

    val isMissing: Boolean
        get() {
            getModel(this.loc)
            return missingModels.contains(this.loc)
        }

    companion object {

        protected var cache: MutableMap<ModelResourceLocation, IBakedModel> = mutableMapOf()
        protected var missingModels: MutableSet<ModelResourceLocation> = mutableSetOf()
        protected var errors: MutableMap<ResourceLocation, MutableList<String>> = mutableMapOf()

        // ========================================================= STATIC METHODS

        //region creators

        fun of(model: String, variant: String): StateHandle {
            return of(ResourceLocation(model), variant)
        }

        fun of(model: ResourceLocation, variant: String): StateHandle {
            return of(ModelResourceLocation(model, variant))
        }

        fun ofLazy(model: String, variant: String): StateHandle {
            return ofLazy(ResourceLocation(model), variant)
        }

        fun ofLazy(model: ResourceLocation, variant: String): StateHandle {
            return ofLazy(ModelResourceLocation(model, variant))
        }

        fun ofLazy(loc: ModelResourceLocation): StateHandle {
            return StateHandle(loc)
        }

        fun of(loc: ModelResourceLocation): StateHandle {
            return StateHandle(loc).reload()
        }

        //endregion

        private fun getModel(loc: ModelResourceLocation): IBakedModel {
            var model: IBakedModel? = cache[loc]
            if (model != null)
                return model

            loadModel(loc)
            model = cache[loc]
            if (model == null)
                throw IllegalStateException("Cache contained null even after loading for model " + loc)
            return model
        }

        private fun loadModel(loc: ModelResourceLocation) {
            try {
                val mod = if(Const.developmentEnvironment) {
                    val baseLoc = ResourceLocation(loc.resourcePath, loc.resourceDomain)
                    val m = try {
                        ModelLoaderRegistry.getModel(loc)
                    } catch (e: Exception) {
                        errors.getOrPut(baseLoc) { mutableListOf() }

                        val sw = StringWriter()
                        e.printStackTrace(PrintWriter(sw))
                        errors[baseLoc]?.add("`#${loc.variant}`\n${sw.toString()}")

                        ModelLoaderRegistry.getMissingModel()
                    }

                    m
                } else {
                    ModelLoaderRegistry.getModelOrMissing(loc)
                }

                if (mod === ModelLoaderRegistry.getMissingModel()) {
                    missingModels.add(loc)
                }
                val model = mod.bake(mod.defaultState, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter())
                cache.put(loc, model)
            } catch (e: Exception) {
                throw ReportedException(CrashReport("Error loading custom model " + loc, e))
            }

        }

        fun init() {
            val rm = Minecraft.getMinecraft().resourceManager
            if (rm is IReloadableResourceManager) {
                rm.registerReloadListener {
                    cache.clear()
                    missingModels.clear()
                    errors.clear()
                }
            }
        }
    }
}
