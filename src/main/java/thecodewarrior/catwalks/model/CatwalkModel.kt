package thecodewarrior.catwalks.model

import com.google.common.collect.ImmutableList
import com.teamwizardry.librarianlib.features.helpers.vec
import gnu.trove.set.TIntSet
import gnu.trove.set.hash.TIntHashSet
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.property.IExtendedBlockState
import thecodewarrior.catwalks.BlockCatwalk
import java.util.*

// Model in blockstate: "modid:resource/path!.postfix!catwalks:catwalk"
// (postfix used for stuff like .obj models. Just use !! if you don't need a postfix)
//
// Resource location: "modid:resource/path/complete.postfix"
//
// 0, 0
// ├─────────────┐
// │╔═══════════╗│
// │║           ║│
// │║           ║│
// │║           ║│
// │║           ║│
// │╚═══════════╝│
// └─────────────┤
//          16, 16
//
// Resource location: "modid:resource/path/rails.postfix"
//
// -16, -16
// ├─────────────┬─────────────┬─────────────┐ +x
// │             │║           ║│╦           ╦│
// │             │║           ║│║           ║│
// │      0      │║     1     ║│║     2     ║│
// │             │║           ║│║           ║│
// │             │║           ║│║           ║│
// │             │║           ║│╩           ╩│
// ├─────────────┼─────────────┼─────────────┤
// │═════════════│╝           ╚│             │
// │             │             │             │
// │      3      │      4      │             │
// │             │             │             │
// │             │             │             │
// │═════════════│╗           ╔│             │
// ├─────────────┼─────────────┼─────────────┤
// │╠═══════════╣│             │╔═══════════╗│
// │             │             │║           ║│
// │      5      │             │║     6     ║│
// │             │             │║           ║│
// │             │             │║           ║│
// │╠═══════════╣│             │╚═══════════╝│
// └─────────────┴─────────────┴─────────────┤
// +z                                   32, 32
//
// 0 - Middle
// 1 - Z axis connected rails
// 2 - Z axis end rails
// 3 - X axis connected rails
// 4 - Inner corner rails
// 5 - X axis end rails
// 6 - Outer corner rails
//
// Resource location: "modid:resource/path/floor.postfix"
//
// -16, -16
// ├─────────────┬─────────────┬─────────────┐ +x
// │             │║           ║│             │
// │             │║           ║│             │
// │      0      │║     1     ║│             │
// │             │║           ║│             │
// │             │║           ║│             │
// │             │║           ║│             │
// ├─────────────┼─────────────┼─────────────┤
// │═════════════│╝           ╚│             │
// │             │             │             │
// │      1      │      2      │             │
// │             │             │             │
// │             │             │             │
// │═════════════│╗           ╔│             │
// ├─────────────┼─────────────┼─────────────┤
// │             │             │╔═══════════╗│
// │             │             │║           ║│
// │             │             │║     3     ║│
// │             │             │║           ║│
// │             │             │║           ║│
// │             │             │╚═══════════╝│
// └─────────────┴─────────────┴─────────────┤
// +z                                   32, 32
//
//
//
// 0 - Middle floor
// 1 - Z axis edged floor
// 2 - X axis edged floor
// 3 - Inner corner floor
// 4 - Outer corner floor

enum class FloorSection(x_: Int, z_: Int) {
    MIDDLE(0, 0),
    Z_EDGE(1, 0),
    X_EDGE(0, 1),
    INNER(1, 1),
    OUTER(2, 2);

    val x = x_-1
    val z = z_-1

    val boundingBoxes: Array<AxisAlignedBB>
    init {
        val model = AxisAlignedBB(x.toDouble(), -1.0, z.toDouble(), x+0.5, 2.0, z+0.5)

        boundingBoxes = arrayOf(
                model,
                model.offset(0.5, 0.0, 0.0),
                model.offset(0.5, 0.0, 0.5),
                model.offset(0.0, 0.0, 0.5)
        )
    }
}

enum class RailSection(x_: Int, z_: Int) {
    MIDDLE(0, 0),
    Z_EDGE(1, 0),
    Z_END(2, 0),
    X_EDGE(0, 1),
    X_END(0, 2),
    INNER(1, 1),
    OUTER(2, 2);

    val x = x_-1
    val z = z_-1

    val boundingBoxes: Array<AxisAlignedBB>
    init {
        val model = AxisAlignedBB(x.toDouble(), -1.0, z.toDouble(), x+0.5, 2.0, z+0.5)

        boundingBoxes = arrayOf(
                model,
                model.offset(0.5, 0.0, 0.0),
                model.offset(0.5, 0.0, 0.5),
                model.offset(0.0, 0.0, 0.5)
        )
    }
}

class CatwalkState private constructor(
        val rail: Array<RailSection>,
        val floor: Array<FloorSection>,
        private val layers: TIntSet) {

    constructor(
             railNW:  RailSection,  railNE:  RailSection,  railSW:  RailSection,  railSE:  RailSection,
            floorNW: FloorSection, floorNE: FloorSection, floorSW: FloorSection, floorSE: FloorSection,
            vararg layers: Int
    ) : this(arrayOf(railNW, railNE, railSE, railSW), arrayOf(floorNW, floorNE, floorSE, floorSW), TIntHashSet(layers))

    fun hasLayer(layer: Int): Boolean {
        return layer in layers
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CatwalkState) return false

        if (!Arrays.equals(rail, other.rail)) return false
        if (!Arrays.equals(floor, other.floor)) return false
        if (layers != other.layers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(rail)
        result = 31 * result + Arrays.hashCode(floor)
        result = 31 * result + layers.hashCode()
        return result
    }

}

class CatwalkModel(val item: IBakedModel, val rails: IBakedModel, val floor: IBakedModel) : IBakedModel {
    val cache = mutableMapOf<CatwalkState, List<BakedQuad>>()

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        val cwState = (state as? IExtendedBlockState)?.getValue(BlockCatwalk.PROPERTY_CATWALK_STATE) ?:
                CatwalkState(
                         RailSection.OUTER,  RailSection.OUTER,  RailSection.OUTER,  RailSection.OUTER,
                        FloorSection.OUTER, FloorSection.OUTER, FloorSection.OUTER, FloorSection.OUTER, 0)
        return cache.getOrPut(cwState) {
            val builder = ImmutableList.Builder<BakedQuad>()
            val railQuads = rails.getQuads(state, side, rand)
            val floorQuads = floor.getQuads(state, side, rand)

            val filter = { quad: BakedQuad ->
                !quad.hasTintIndex() || cwState.hasLayer(quad.tintIndex)
            }

            (0..3).forEach {
                val offset = vec(
                        if(it in 1..2) 0.5 else 0.0,
                        -1, // the bounding box negative corner will be offset to (0, 0, 0) but the bounding box extends to -1y in the model
                        if(it > 1) 0.5 else 0.0
                )
                ModelSlicer.sliceInto(builder, railQuads, cwState.rail[it].boundingBoxes[it], offset, filter)
                ModelSlicer.sliceInto(builder, floorQuads, cwState.floor[it].boundingBoxes[it], offset, filter)
            }

            return@getOrPut builder.build()
        }
    }

    override fun isBuiltInRenderer() = false

    override fun isAmbientOcclusion() = true

    override fun isGui3d() = false

    override fun getOverrides() = ItemOverrideList.NONE
    override fun getParticleTexture() = item.particleTexture

}
