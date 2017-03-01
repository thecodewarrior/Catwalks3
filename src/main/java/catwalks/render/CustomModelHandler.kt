package catwalks.render

import catwalks.splitOn
import com.google.common.base.Function
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.IRetexturableModel
import net.minecraftforge.common.model.IModelState

/**
 * `catwalks:some/Model/Location@loader`
 */
object CustomModelHandler : ICustomModelLoader {
    private val models = mutableMapOf<String, (String) -> IModel>()

    fun registerModel(name: String, lambda: (String) -> IModel) {
        models[name] = lambda
    }

    fun register(name: String, lambda: (String, IModelState) -> IBakedModel) {
        registerModel(name) {
            BasicCustomModel(it, lambda)
        }
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return parse(modelLocation) != null
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val parsed = parse(modelLocation)

        parsed ?: throw IllegalArgumentException("CustomModelHandler.accepts was not checked! Invalid model location! `$modelLocation`")

        return parsed.second(parsed.first)
    }

    fun parse(modelLocation: ResourceLocation): Pair<String, (String) -> IModel>? {
        if("@" !in modelLocation.resourcePath)
            return null

        val index = modelLocation.toString().lastIndexOf("@")
        val (loc, model) = modelLocation.toString().splitOn(index)

        val modelLoader = models.get(model)
        modelLoader ?: return null

        return Pair(loc, modelLoader)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager?) {
        // noop
    }
}

internal class BasicCustomModel(val str: String, val lambda: (String, IModelState) -> IBakedModel) : IModel, IRetexturableModel {

    override fun getDependencies(): Collection<ResourceLocation> {
        val dependencies = Lists.newArrayList<ResourceLocation>()
        return dependencies
    }

    override fun getTextures(): Collection<ResourceLocation> {
        return emptyList()
    }

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return lambda(str, state)
    }

    override fun getDefaultState(): IModelState? {
        return null
    }

    override fun retexture(textures: ImmutableMap<String, String>): IModel {
        return BasicCustomModel(str, lambda)
    }
}
