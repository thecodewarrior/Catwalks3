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

    private val regex = Regex("^(.*)!(.*?)!catwalks:catwalk$")
    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return regex.containsMatchIn(modelLocation.resourcePath)
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val match = regex.find(modelLocation.resourcePath)!!

        var (path, postfix) = match.destructured
        path = path.substring("models/".length)

        return CatwalkModelWrapper(modelLocation.resourceDomain, path, postfix)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        manager = resourceManager
    }
}

class CatwalkModelWrapper(domain: String, path: String, postfix: String) : IModel {
    val item_rl = ResourceLocation(domain, "$path/complete$postfix")
    val rails_rl = ResourceLocation(domain, "$path/rails$postfix")
    val floor_rl = ResourceLocation(domain, "$path/floor$postfix")

    val item: IModel by lazy { ModelLoaderRegistry.getModelOrMissing(item_rl) }
    val rails: IModel by lazy { ModelLoaderRegistry.getModelOrMissing(rails_rl) }
    val floor: IModel by lazy { ModelLoaderRegistry.getModelOrMissing(floor_rl) }

    override fun getDependencies(): Collection<ResourceLocation> {
        return ImmutableList.of(item_rl, floor_rl, rails_rl)
    }

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return CatwalkModel(
                item.bake(state, format, bakedTextureGetter),
                rails.bake(state, format, bakedTextureGetter),
                floor.bake(state, format, bakedTextureGetter)
        )
    }
}
