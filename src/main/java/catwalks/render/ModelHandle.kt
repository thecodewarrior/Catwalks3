package catwalks.render

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.crash.CrashReport
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.*
import net.minecraftforge.client.model.pipeline.LightUtil
import net.minecraftforge.common.model.IModelState
import org.lwjgl.opengl.GL11

/**
 * Credit where credit is due: https://github.com/gigaherz/Everpipe/blob/master/src/main/java/gigaherz/everpipe/client/ModelHandle.java
 */
class ModelHandle {

    private val textureReplacements = Maps.newHashMap<String, String>()
    val model: ResourceLocation
    val key: String
    val vertexFormat: VertexFormat
    val state: IModelState?
    private val uvLock: Boolean


    private constructor(model: ResourceLocation) {
        this.model = model
        this.vertexFormat = DefaultVertexFormats.ITEM
        this.state = null
        this.uvLock = false
        this.key = computeKey()
    }

    private constructor(handle: ModelHandle, texChannel: String, resloc: String) {
        this.model = handle.model
        this.vertexFormat = handle.vertexFormat
        this.state = handle.state
        this.uvLock = handle.uvLock
        textureReplacements.putAll(handle.textureReplacements)
        textureReplacements.put(texChannel, resloc)
        this.key = computeKey()
    }

    private constructor(handle: ModelHandle, fmt: VertexFormat) {
        this.model = handle.model
        this.vertexFormat = fmt
        this.state = handle.state
        this.uvLock = handle.uvLock
        textureReplacements.putAll(handle.textureReplacements)
        this.key = computeKey()
    }

    private constructor(handle: ModelHandle, state: IModelState) {
        this.model = handle.model
        this.vertexFormat = handle.vertexFormat
        this.state = state
        this.uvLock = handle.uvLock
        textureReplacements.putAll(handle.textureReplacements)
        this.key = computeKey()
    }

    private constructor(handle: ModelHandle, uvLock: Boolean) {
        this.model = handle.model
        this.vertexFormat = handle.vertexFormat
        this.state = handle.state
        this.uvLock = uvLock
        textureReplacements.putAll(handle.textureReplacements)
        this.key = computeKey()
    }

    private fun computeKey(): String {
        val b = StringBuilder()
        b.append(model.toString())
        for ((key1, value) in textureReplacements) {
            b.append("//")
            b.append(key1)
            b.append("/")
            b.append(value)
        }
        b.append("//VF:")
        b.append(vertexFormat.hashCode())
        b.append("//S:")
        b.append(if (state != null) state.hashCode() else "n")
        b.append("//UVL:")
        b.append(uvLock)
        return b.toString()
    }

    fun replace(texChannel: String, resloc: String): ModelHandle {
        if (textureReplacements.containsKey(texChannel) && textureReplacements[texChannel] == resloc)
            return this
        return ModelHandle(this, texChannel, resloc)
    }

    fun vertexFormat(fmt: VertexFormat): ModelHandle {
        if (vertexFormat === fmt)
            return this
        return ModelHandle(this, fmt)
    }

    fun state(newState: IModelState): ModelHandle {
        if (state === newState)
            return this
        return ModelHandle(this, newState)
    }

    fun uvLock(uvLock: Boolean): ModelHandle {
        if (this.uvLock == uvLock)
            return this
        return ModelHandle(this, uvLock)
    }

    fun getTextureReplacements(): Map<String, String> {
        return textureReplacements
    }

    fun uvLocked(): Boolean {
        return uvLock
    }

    fun get(): IBakedModel {
        return loadModel(this)
    }

    fun render() {
        renderModel(get(), vertexFormat)
    }

    fun render(color: Int) {
        renderModel(get(), vertexFormat, color)
    }

    companion object {
        internal var loadedModels: MutableMap<String, IBakedModel> = Maps.newHashMap<String, IBakedModel>()

        // ========================================================= STATIC METHODS

        fun init() {
            val rm = Minecraft.getMinecraft().resourceManager
            if (rm is IReloadableResourceManager) {
                rm.registerReloadListener(IResourceManagerReloadListener { loadedModels.clear() })
            }
        }

        fun of(model: String): ModelHandle {
            return ModelHandle(ResourceLocation(model))
        }

        fun of(model: ResourceLocation): ModelHandle {
            return ModelHandle(model)
        }

        private fun renderModel(model: IBakedModel, fmt: VertexFormat) {
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.buffer
            worldrenderer.begin(GL11.GL_QUADS, fmt)
            for (bakedquad in model.getQuads(null, null, 0)) {
                worldrenderer.addVertexData(bakedquad.vertexData)
            }
            tessellator.draw()
        }

        private fun renderModel(model: IBakedModel, fmt: VertexFormat, color: Int) {
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.buffer
            worldrenderer.begin(GL11.GL_QUADS, fmt)
            for (bakedquad in model.getQuads(null, null, 0)) {
                LightUtil.renderQuadColor(worldrenderer, bakedquad, color)
            }
            tessellator.draw()
        }

        private fun loadModel(handle: ModelHandle): IBakedModel {
            var model: IBakedModel? = loadedModels[handle.key]
            if (model != null)
                return model

            try {
                var mod = ModelLoaderRegistry.getModelOrMissing(handle.model)
                if (mod is IRetexturableModel && handle.getTextureReplacements().size > 0) {
                    mod = mod.retexture(ImmutableMap.copyOf(handle.getTextureReplacements()))
                }
                if (handle.uvLocked() && mod is IModelUVLock) {
                    mod = mod.uvlock(true)
                }
                var state: IModelState? = handle.state
                if (state == null) state = mod.defaultState
                model = mod.bake(state, handle.vertexFormat, ModelLoader.defaultTextureGetter())
                loadedModels.put(handle.key, model)
                return model
            } catch (e: Exception) {
                throw ReportedException(CrashReport("Error loading custom model " + handle.model, e))
            }

        }
    }
}
