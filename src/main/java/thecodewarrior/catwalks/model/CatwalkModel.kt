package thecodewarrior.catwalks.model

import com.google.common.collect.ImmutableList
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import gnu.trove.set.TIntSet
import gnu.trove.set.hash.TIntHashSet
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.client.model.pipeline.VertexTransformer
import net.minecraftforge.common.property.IExtendedBlockState
import thecodewarrior.catwalks.BlockCatwalk

// Append !catwalks:catwalk to a model location to use the catwalk model type
//
// Single lines: Section separation
// Double lines: Catwalk railing
// Double line T junction: freestanding rail end
// Periods: "Open" edge, or an edge connecting to another catwalk of the same type
// Asterisks: "Closed" edge, or a freestanding edge
// Numbers: The ID of each quadrant type (not all quadrants are adjacent to all others of the same type)
//
// The main model, which is used for the item as well as for the connected model. Located at the location preceding the !
//
// 0, 0
// ┌─────────────┐
// │╔═══════════╗│
// │║ 0       0 ║│
// │║           ║│
// │║           ║│
// │║ 0       0 ║│
// │╚═══════════╝│
// └─────────────┘
//          16, 16
//
// The connected model map, located at the location preceding the ! with a `.cmm` before the `.json`, or before the last
// period if it contains one
//
// -16, -16
// ├─────────────┬─────────────┬─────────────┐ +x
// │. . . . . . .│╦* * * * * *╦│* * * * * * *│
// │. 1       1 .│║ 2       2 ║│* 4       4 *│
// │.           .│║           ║│*           *│
// │.           .│║           ║│*           *│
// │. 1       1 .│║ 3       3 ║│* 4       4 *│
// │. . . . . . .│║. . . . . .║│* * * * * * *│
// ├─────────────┼─────────────┼─────────────┤
// │╠════════════│╝. . . . . .╚│════════════╣│
// │* 5       6 .│. 7       7 .│. 6       5 *│
// │*           .│.           .│.           *│
// │*           .│.           .│.           *│
// │* 5       6 .│. 7       7 .│. 6       5 *│
// │╠════════════│╗. . . . . .╔│════════════╣│
// ├─────────────┼─────────────┼─────────────┤
// │╩. . . . . .╩│║. . . . . .║│╣* * * * * *╠│
// │* 8       8 *│║ 3       3 ║│. 9       9 .│
// │*           *│║           ║│.           .│
// │*           *│║           ║│.           .│
// │* 8       8 *│║ 2       2 ║│. 9       9 .│
// │╦. . . . . .╦│╩* * * * * *╩│╣* * * * * *╠│
// └─────────────┴─────────────┴─────────────┤
// +z                                   32, 32
//
// Type names:
// 0 - CLOSED
// 1 - MIDDLE
// 2 - X_END
// 3 - X_CONNECT
// 4 - OPEN_EDGE
// 5 - Z_END
// 6 - Z_CONNECT
// 7 - INNER
// 8 - Z_GAP
// 9 - X_GAP
//

// quadrant parameter is an interlaced `x,z` array of coordinates for the quadrants
// coords are in half blocks, so each stitchable piece is one "unit" in the array
// quadrants are wound clockwise from the top left
// if there are only two values the array will be fabricated based on a contiguous square, as most sections are
enum class SectionType(vararg quadrants: Int) {
    CLOSED(0,0),
    MIDDLE(-2,-2),
    Z_END(0,-2, 1,-2, 1,3, 0,3),
    Z_CONNECT(0,-1, 1,-1, 1,2, 0,2),
    OPEN_EDGE(2,-2),
    X_END(-2,0, 3,0, 3,1, -2,1),
    X_CONNECT(-1,0, 2,0, 2,1, -1,1),
    INNER(0,0),
    X_GAP(-2,2),
    Z_GAP(2,2);

    val bounds: Array<AxisAlignedBB>

    init {
        val q = if(quadrants.size != 2) quadrants else intArrayOf(
                quadrants[0]  , quadrants[1]  ,
                quadrants[0]+1, quadrants[1]  ,
                quadrants[0]+1, quadrants[1]+1,
                quadrants[0]  , quadrants[1]+1
        )

        bounds = (q.indices step 2).map {
            AxisAlignedBB(
                    q[it]/2.0      , -1.0, q[it+1]/2.0,
                    q[it]/2.0 + 0.5,  3.0, q[it+1]/2.0 + 0.5
            ).expand(0.000001, 0.000001, 0.000001)
        }.toTypedArray()
    }

    companion object {
        val quadrantOffsetX = floatArrayOf(0f, -0.5f, -0.5f,  0f  )
        val quadrantOffsetZ = floatArrayOf(0f,  0f,   -0.5f, -0.5f)
    }
}

data class CatwalkState private constructor(private val types: List<SectionType>, private val layers: TIntSet) {
    constructor(types: Array<SectionType>, vararg layers: Int) : this(types.toList(), TIntHashSet(layers))

    fun getType(quadrant: Int): SectionType {
        return types[quadrant]
    }

    fun hasLayer(layer: Int): Boolean {
        return layer in layers
    }
}

class CatwalkModel(val main: IBakedModel, val cmm: IBakedModel) : IBakedModel {
    val cache = mutableMapOf<CatwalkState, List<BakedQuad>>()

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        val builder = ImmutableList.Builder<BakedQuad>()
        val cwState = (state as? IExtendedBlockState)?.getValue(BlockCatwalk.PROPERTY_CATWALK_STATE) ?:
                CatwalkState(arrayOf(SectionType.CLOSED, SectionType.CLOSED, SectionType.CLOSED, SectionType.CLOSED), 0)
        val mainQuads = main.getQuads(state, side, rand)
        val cmmQuads = cmm.getQuads(state, side, rand)
        filterQuads(builder,
                if(cwState.getType(0) == SectionType.CLOSED) mainQuads else cmmQuads,
                0, cwState)
        filterQuads(builder,
                if(cwState.getType(1) == SectionType.CLOSED) mainQuads else cmmQuads,
                1, cwState)
        filterQuads(builder,
                if(cwState.getType(2) == SectionType.CLOSED) mainQuads else cmmQuads,
                2, cwState)
        filterQuads(builder,
                if(cwState.getType(3) == SectionType.CLOSED) mainQuads else cmmQuads,
                3, cwState)
        return builder.build()
    }

    fun filterQuads(output: ImmutableList.Builder<BakedQuad>, quads: List<BakedQuad>, quadrant: Int, state: CatwalkState) {
        val aabb = state.getType(quadrant).bounds[quadrant]
        quads.forEach { quad ->
            if(quad.hasTintIndex() && !state.hasLayer(quad.tintIndex)) return@forEach

            val posIndex = quad.format.elements.indexOfFirst { it.isPositionElement }
            quad.vertexData[posIndex]

            var positions = Array<Vec3d>(4) { Vec3d.ZERO }
            var i = 0

            quad.pipe(object : VertexTransformer(UnpackedBakedQuad.Builder(quad.format)) {
                override fun put(element: Int, vararg data: Float) {
                    val usage = parent.vertexFormat.getElement(element).usage
                    if(usage == VertexFormatElement.EnumUsage.POSITION && data.size >= 3)
                        positions[i++] = vec(data[0], data[1], data[2])
                    super.put(element, *data)
                }
            })

            val normal = ((positions[1]-positions[0]) cross (positions[2]-positions[0])).normalize()
            val pos = (positions.fold(Vec3d.ZERO) { fold, it -> fold + it } / 4) - (normal * 0.001)

            if(pos !in aabb) return@forEach

            val builder = UnpackedBakedQuad.Builder(quad.format)

            val transformer = object : VertexTransformer(builder) {
                override fun put(element: Int, vararg data: Float) {
                    var data = data
                    val usage = parent.vertexFormat.getElement(element).usage
                    if(usage == VertexFormatElement.EnumUsage.POSITION && data.size >= 3) {
                        data = data.clone()
                        data[0] = data[0] - (aabb.minX.toFloat() + SectionType.quadrantOffsetX[quadrant])
                        data[2] = data[2] - (aabb.minZ.toFloat() + SectionType.quadrantOffsetZ[quadrant])
                    }
                    if(usage == VertexFormatElement.EnumUsage.NORMAL) {
                        data = data.clone()
                        data[0] = normal.x.toFloat()
                        data[1] = normal.y.toFloat()
                        data[2] = normal.z.toFloat()
                    }
                    super.put(element, *data)
                }
            }

            quad.pipe(transformer)

            val unpacked = builder.build()

            output.add(BakedQuad( // pack the quad, for memory reasons
                    unpacked.vertexData, unpacked.tintIndex, unpacked.face, unpacked.sprite,
                    unpacked.shouldApplyDiffuseLighting(), unpacked.format
            ))
        }
    }

    override fun isBuiltInRenderer() = false

    override fun isAmbientOcclusion() = true

    override fun isGui3d() = false

    override fun getOverrides() = ItemOverrideList.NONE
    override fun getParticleTexture() = main.particleTexture

}
