package thecodewarrior.catwalks.model

import com.google.common.collect.ImmutableList
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState

/**
 * TODO: Document file CatwalkModel
 *
 * Created by TheCodeWarrior
 */
object CatwalkModelLoader : ICustomModelLoader {

    private lateinit var manager: IResourceManager

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        val v = modelLocation.resourcePath.endsWith("!catwalks:catwalk", true)
        return v
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val path = modelLocation.resourcePath
                .substring(0, modelLocation.resourcePath.length - "!catwalks:catwalk".length)
                .substring("models/".length)
        return CatwalkModelWrapper(modelLocation.resourceDomain, path)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        manager = resourceManager
    }
}

class CatwalkModelWrapper(domain: String, path: String) : IModel {
    val main_rl = ResourceLocation(domain, path)
    val cmm_rl = ResourceLocation(domain, if("." in path) path.substring(0, path.lastIndexOf('.')) + ".cmm" + path.substring(path.lastIndexOf('.')) else path + ".cmm")
    val main: IModel by lazy {
        ModelLoaderRegistry.getModel(main_rl)
    }

    val cmm: IModel by lazy {
        ModelLoaderRegistry.getModel(cmm_rl)
    }

    override fun getDependencies(): Collection<ResourceLocation> {
        return ImmutableList.of(main_rl, cmm_rl)
    }

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return CatwalkModel(main.bake(state, format, bakedTextureGetter), cmm.bake(state, format, bakedTextureGetter))
    }
}
