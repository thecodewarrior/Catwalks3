package thecodewarrior.catwalks.model

import com.google.common.collect.ImmutableList
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.client.model.pipeline.VertexTransformer

/**
 * TODO: Document file ModelSlicer
 *
 * Created by TheCodeWarrior
 */
object ModelSlicer {
    fun slice(quads: List<BakedQuad>, bb: AxisAlignedBB, offset: Vec3d, filter: (BakedQuad) -> Boolean = { true }): List<BakedQuad> {
        val builder = ImmutableList.Builder<BakedQuad>()

        sliceInto(builder, quads, bb, offset, filter)

        return builder.build()
    }

    fun sliceInto(builder: ImmutableList.Builder<BakedQuad>, quads: List<BakedQuad>, bb: AxisAlignedBB, offset: Vec3d, filter: (BakedQuad) -> Boolean = { true }) {
        val tiny = 0.000001
        val expandedBB = bb.expand(tiny, tiny, tiny)
        quads.forEach {
            if(!filter(it)) return@forEach
            val sliced = sliceQuad(it, expandedBB, offset)

            if(sliced != null) builder.add(sliced)
        }
    }

    private fun sliceQuad(quad: BakedQuad, bb: AxisAlignedBB, offset: Vec3d): BakedQuad? {
        val positions = Array<Vec3d>(4) { Vec3d.ZERO }
        var i = 0

        val builder = UnpackedBakedQuad.Builder(quad.format)

        quad.pipe(object : VertexTransformer(builder) {
            override fun put(element: Int, vararg data_: Float) {
                var data = data_
                val usage = parent.vertexFormat.getElement(element).usage
                if(usage == VertexFormatElement.EnumUsage.POSITION && data.size >= 3) {
                    positions[i++] = vec(data[0], data[1], data[2])
                    data = data.clone()
                    data[0] = data[0] - (bb.minX - offset.x).toFloat()
                    data[1] = data[1] - (bb.minY - offset.y).toFloat()
                    data[2] = data[2] - (bb.minZ - offset.z).toFloat()
                }
                super.put(element, *data)
            }
        })

        val normal = ((positions[1]-positions[0]) cross (positions[2]-positions[0])).normalize()

        val pos = (positions.fold(Vec3d.ZERO) { fold, it -> fold + it } / 4) - (normal * 0.001)

        if(pos !in bb) return null

        val unpacked = builder.build()

        return BakedQuad( // pack the quad, for memory reasons
                unpacked.vertexData, unpacked.tintIndex, unpacked.face, unpacked.sprite,
                unpacked.shouldApplyDiffuseLighting(), unpacked.format
        )
    }
}
