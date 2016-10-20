package catwalks.render.part

import com.google.common.base.Function
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.IRetexturableModel
import net.minecraftforge.common.model.IModelState

/**
 * Created by TheCodeWarrior
 */
object BaseModel {
    class BakedModel(private val particle: TextureAtlasSprite?) : IBakedModel {

        override fun getQuads(normalState: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            return emptyList()
        }

        override fun isAmbientOcclusion(): Boolean {
            return true
        }

        override fun isGui3d(): Boolean {
            return true
        }

        override fun isBuiltInRenderer(): Boolean {
            return false
        }

        override fun getParticleTexture(): TextureAtlasSprite? {
            return particle
        }

        @Deprecated("")
        override fun getItemCameraTransforms(): ItemCameraTransforms? {
            return null
        }

        override fun getOverrides(): ItemOverrideList? {
            return null
        }
    }

    class Model : IModel, IRetexturableModel {
        private val particle: ResourceLocation?

        constructor() {
            this.particle = null
        }

        constructor(particle: String?) {
            this.particle = if (particle == null) null else ResourceLocation(particle)
        }

        override fun getDependencies(): Collection<ResourceLocation> {
            val dependencies = Lists.newArrayList<ResourceLocation>()
            return dependencies
        }

        override fun getTextures(): Collection<ResourceLocation> {
            if (particle != null)
                return listOf<ResourceLocation>(particle)
            return emptyList<ResourceLocation>()
        }

        override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
            var part: TextureAtlasSprite? = null
            if (particle != null) part = bakedTextureGetter.apply(particle)
            return BakedModel(part)
        }

        override fun getDefaultState(): IModelState? {
            return null
        }

        override fun retexture(textures: ImmutableMap<String, String>): IModel {
            return Model(textures["particle"])
        }
    }

    class ModelLoader : ICustomModelLoader {

        override fun accepts(modelLocation: ResourceLocation): Boolean {
            return false
        }

        @Throws(Exception::class)
        override fun loadModel(modelLocation: ResourceLocation): IModel {
            return Model(null)
        }

        override fun onResourceManagerReload(resourceManager: IResourceManager) {
            // Nothing to do
        }
    }
}
