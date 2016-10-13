package catwalks.render.part

import catwalks.Const
import catwalks.SCREAM_AT_DEV
import catwalks.part.data.CatwalkRenderData
import catwalks.render.ModelHandle
import catwalks.render.StateHandle
import catwalks.util.EnumLeftRight
import catwalks.util.nestedmap.HierarchyMap
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
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class CatwalkBakedModel(private val mat: String, private val particle: TextureAtlasSprite?) : IBakedModel {

    internal val CATWALK_BLOCKSTATE_LOC: ResourceLocation

    private val bottom_edge_north: StateHandle
    private val bottom_edge_south: StateHandle
    private val bottom_edge_east: StateHandle
    private val bottom_edge_west: StateHandle
    private val bottom_corner_north_east: StateHandle
    private val bottom_corner_north_west: StateHandle
    private val bottom_corner_south_east: StateHandle
    private val bottom_corner_south_west: StateHandle

    private val bottom_xaxis: StateHandle
    private val bottom_zaxis: StateHandle

    private val bottom_xaxis_nsew: Array<StateHandle?>
    private val bottom_zaxis_nsew: Array<StateHandle?>

    private val corner_north_east: EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>
    private val corner_north_west: EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>
    private val corner_south_east: EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>
    private val corner_south_west: EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>

    private val sides: HierarchyMap<StateHandle> // side, left/right, endType

    init {
        val TF = booleanArrayOf(true, false)

        CATWALK_BLOCKSTATE_LOC = Const.location("internal/catwalk/" + mat)

        bottom_xaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_xaxis")
        bottom_zaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_zaxis")

        bottom_xaxis_nsew = arrayOfNulls<StateHandle>(16)
        bottom_zaxis_nsew = arrayOfNulls<StateHandle>(16)

        for (north in TF) {
            for (south in TF) {
                for (east in TF) {
                    for (west in TF) {
                        val i = encodeSides(north, south, east, west)
                        val nsew = (if (north) "N" else "n") + (if (south) "S" else "s") + (if (east) "E" else "e") + if (west) "W" else "w"

                        bottom_xaxis_nsew[i] = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_xaxis_" + nsew)
                        bottom_zaxis_nsew[i] = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_zaxis_" + nsew)

                    }
                }
            }
        }

        corner_north_east = EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>(CatwalkRenderData.EnumCatwalkCornerType::class.java)
        corner_north_west = EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>(CatwalkRenderData.EnumCatwalkCornerType::class.java)
        corner_south_east = EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>(CatwalkRenderData.EnumCatwalkCornerType::class.java)
        corner_south_west = EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>(CatwalkRenderData.EnumCatwalkCornerType::class.java)

        for (corner in CatwalkRenderData.EnumCatwalkCornerType.values()) {
            corner_north_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_east_" + corner.name.toLowerCase()))
            corner_north_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_west_" + corner.name.toLowerCase()))
            corner_south_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_east_" + corner.name.toLowerCase()))
            corner_south_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_west_" + corner.name.toLowerCase()))
        }

        bottom_edge_north = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_north")
        bottom_edge_south = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_south")
        bottom_edge_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_east")
        bottom_edge_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_west")

        bottom_corner_north_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_east")
        bottom_corner_north_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_west")
        bottom_corner_south_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_east")
        bottom_corner_south_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_west")


        sides = HierarchyMap<StateHandle>(3)

        for (facing in EnumFacing.HORIZONTALS) {
            for (leftRight in EnumLeftRight.values()) {
                for (end in CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.values()) {
                    sides.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("side_%s_%s_%s", facing.name, leftRight.name, end.name).toLowerCase()),
                            facing, leftRight, end)
                }
            }
        }
    }

    private fun encodeSides(north: Boolean, south: Boolean, east: Boolean, west: Boolean): Int {
        return (if (north) 1 else 0) or
                ((if (south) 1 else 0) shl 1) or
                ((if (east) 1 else 0) shl 2) or
                ((if (west) 1 else 0) shl 3)
    }

    override fun getQuads(normalState: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (normalState != null) {
            val state = normalState as IExtendedBlockState

            val quads = QuadManager(normalState, side, rand)

            val data = state.getValue(Const.CATWALK_RENDER_DATA)

            // region sides
            for (facing in EnumFacing.HORIZONTALS) {

                data.sides[facing]?.let {
                    it.left?.let {
                        quads.add(sides.get(facing, EnumLeftRight.LEFT, it))
                    }
                    it.right?.let {
                        quads.add(sides.get(facing, EnumLeftRight.RIGHT, it))
                    }
                }
            }
            // endregion

            // region corners
            if (data.corner_ne != null) {
                quads.add(corner_north_east[data.corner_ne])
            }
            if (data.corner_nw != null) {
                quads.add(corner_north_west[data.corner_nw])
            }
            if (data.corner_se != null) {
                quads.add(corner_south_east[data.corner_se])
            }
            if (data.corner_sw != null) {
                quads.add(corner_south_west[data.corner_sw])
            }
            // endregion

            // region bottom middle
            if (data.bottom == EnumFacing.Axis.X) {
                val handle = bottom_xaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)]
                if (handle == null || handle.isMissing) {
                    quads.add(bottom_xaxis)
                } else {
                    quads.add(handle)
                }
            }
            if (data.bottom == EnumFacing.Axis.Z) {
                val handle = bottom_zaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)]
                if (handle == null || handle.isMissing) {
                    quads.add(bottom_zaxis)
                } else {
                    quads.add(handle)
                }
            }
            // endregion

            // region bottom edges
            if (data.bottomNorth && !bottom_edge_north.isMissing) {
                quads.add(bottom_edge_north)
            }
            if (data.bottomSouth && !bottom_edge_south.isMissing) {
                quads.add(bottom_edge_south)
            }
            if (data.bottomEast && !bottom_edge_east.isMissing) {
                quads.add(bottom_edge_east)
            }
            if (data.bottomWest && !bottom_edge_west.isMissing) {
                quads.add(bottom_edge_west)
            }
            // endregion

            // region bottom corners
            if (data.bottomNE && !bottom_corner_north_east.isMissing) {
                quads.add(bottom_corner_north_east)
            }
            if (data.bottomNW && !bottom_corner_north_west.isMissing) {
                quads.add(bottom_corner_north_west)
            }
            if (data.bottomSE && !bottom_corner_south_east.isMissing) {
                quads.add(bottom_corner_south_east)
            }
            if (data.bottomSW && !bottom_corner_south_west.isMissing) {
                quads.add(bottom_corner_south_west)
            }
            // endregion

            return quads.getQuads()
        }

        return emptyList()
    }

    private class QuadManager(internal var state: IBlockState, internal var side: EnumFacing?, internal var rand: Long) {
        internal var quads: MutableList<BakedQuad> = Lists.newArrayList<BakedQuad>()

        fun add(handle: ModelHandle?) {
            if (handle == null)
                SCREAM_AT_DEV()
            add(handle!!.get())
        }

        fun add(handle: StateHandle?) {
            if (handle == null)
                SCREAM_AT_DEV()
            add(handle!!.get())
        }

        fun add(model: IBakedModel?) {
            if (model == null)
                SCREAM_AT_DEV()
            quads.addAll(model!!.getQuads(state, side, rand))
        }

        fun getQuads(): List<BakedQuad> {
            return quads
        }
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

    class Model : IModel, IRetexturableModel {
        private val particle: ResourceLocation?
        private val mat: String

        constructor() {
            this.particle = null
            this.mat = ""
        }

        constructor(particle: String?, mat: String) {
            this.particle = if (particle == null) null else ResourceLocation(particle)
            this.mat = mat
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
            return CatwalkBakedModel(mat, part)
        }

        override fun getDefaultState(): IModelState? {
            return null
        }

        override fun retexture(textures: ImmutableMap<String, String>): IModel {
            return Model(textures["particle"], mat)
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
            return Model(null, modelLocation.resourcePath.substring(PREFIX.length + 1))
        }

        override fun onResourceManagerReload(resourceManager: IResourceManager) {
            // Nothing to do
        }

        companion object {
            val PREFIX = "models/block/internal/catwalk"
        }
    }
}
