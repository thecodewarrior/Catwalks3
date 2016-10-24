package catwalks.render.part

import catwalks.Const
import catwalks.render.QuadManager
import catwalks.render.StateHandle
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
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Created by TheCodeWarrior
 */
object ScaffoldModel {
    class BakedModel(private val mat: String, private val particle: TextureAtlasSprite?) : IBakedModel {

        private val handle: StateHandle = StateHandle.of(Const.location("internal/scaffold"), mat)

        override fun getQuads(normalState: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (normalState == null || normalState !is IExtendedBlockState)
                return emptyList()

            val qm = QuadManager(normalState, side, rand)

            val data = normalState.getValue(Const.SCAFFOLD_RENDER_DATA)

            if(data.sides[side?.ordinal ?: 0])
                qm.add(handle)

            return qm.getQuads()
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
        private val mat: String

        constructor() {
            this.particle = null
            this.mat = ""
        }

        constructor(mat: String, particle: String?) {
            this.mat = mat;
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
            return BakedModel(mat, part)
        }

        override fun getDefaultState(): IModelState? {
            return null
        }

        override fun retexture(textures: ImmutableMap<String, String>): IModel {
            return Model(mat, textures["particle"])
        }
    }

    class ModelLoader : ICustomModelLoader {

        override fun accepts(modelLocation: ResourceLocation): Boolean {
            if (modelLocation.resourceDomain != Const.MODID)
                return false
            return modelLocation.resourcePath.startsWith(PREFIX)
        }

        @Throws(Exception::class)
        override fun loadModel(modelLocation: ResourceLocation): IModel {
            return Model(modelLocation.resourcePath.substring(PREFIX.length + 1), null)
        }

        override fun onResourceManagerReload(resourceManager: IResourceManager) {
            // Nothing to do
        }

        companion object {
            val PREFIX = "models/block/internal/scaffold"
        }
    }
}
