package catwalks.render.part

import catwalks.Const
import catwalks.part.data.CatwalkRenderData
import catwalks.render.QuadManager
import catwalks.render.StateHandle
import catwalks.util.EnumLeftRight
import catwalks.util.nestedmap.HierarchyMap
import mcmultipart.MCMultiPartMod
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.MultiModelState
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class CatwalkBakedModel(loc: String, modelState: IModelState) : BaseBakedModel(loc, modelState) {

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

        val rl = ResourceLocation(loc)
        CATWALK_BLOCKSTATE_LOC = ResourceLocation(rl.resourceDomain, rl.resourcePath.substring("models/block/".length))

        bottom_xaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_xaxis")
        bottom_zaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_zaxis")

        bottom_xaxis_nsew = arrayOfNulls<StateHandle>(16)
        bottom_zaxis_nsew = arrayOfNulls<StateHandle>(16)

        for (north in TF) {
            for (south in TF) {
                for (east in TF) {
                    for (west in TF) {
                        val i = encodeSides(north, south, east, west)
                        var nsew = ""
                        if (north) nsew += "N" else nsew += "n"
                        if (south) nsew += "S" else nsew += "s"
                        if (east) nsew += "E" else nsew += "e"
                        if (west) nsew += "W" else nsew += "w"

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
            corner_north_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_north_east_" + corner.name.toLowerCase()))
            corner_north_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_north_west_" + corner.name.toLowerCase()))
            corner_south_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_south_east_" + corner.name.toLowerCase()))
            corner_south_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_south_west_" + corner.name.toLowerCase()))
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
        val quads = QuadManager(normalState?:BlockStateContainer(MCMultiPartMod.multipart).baseState, side, rand)
        val data = if (normalState != null) {
            val state = normalState as IExtendedBlockState
            state.getValue(Const.CATWALK_RENDER_DATA)
        } else {
            CatwalkRenderData.DEFAULT
        }
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

        var hadCombo = false
        // region bottom middle
        if (data.bottom == EnumFacing.Axis.X) {
            val handle = bottom_xaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)]
            if (handle == null || handle.isMissing) {
                quads.add(bottom_xaxis)
            } else {
                hadCombo = true
                quads.add(handle)
            }
        }
        if (data.bottom == EnumFacing.Axis.Z) {
            val handle = bottom_zaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)]
            if (handle == null || handle.isMissing) {
                quads.add(bottom_zaxis)
            } else {
                hadCombo = true
                quads.add(handle)
            }
        }
        // endregion

        if(!hadCombo) {
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
        }
        return quads.getQuads()
    }
}
