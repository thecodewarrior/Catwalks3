package catwalks.part.data

import net.minecraft.util.EnumFacing
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class CatwalkRenderData : Comparable<CatwalkRenderData> {

    var sides = EnumMap<EnumFacing, CatwalkSideRenderData>(EnumFacing::class.java)

    var bottom: EnumFacing.Axis? = null
    var bottomNorth: Boolean = false
    var bottomSouth: Boolean = false
    var bottomEast: Boolean = false
    var bottomWest: Boolean = false
    var bottomNE: Boolean = false
    var bottomNW: Boolean = false
    var bottomSE: Boolean = false
    var bottomSW: Boolean = false
    var corner_ne: EnumCatwalkCornerType? = null
    var corner_nw: EnumCatwalkCornerType? = null
    var corner_se: EnumCatwalkCornerType? = null
    var corner_sw: EnumCatwalkCornerType? = null

    class CatwalkSideRenderData {
        var left: EnumCatwalkEndRenderType? = null
        var right: EnumCatwalkEndRenderType? = null

        enum class EnumCatwalkEndRenderType {
            END, MERGE, CONNECT, INNER_CORNER, OUTER_CORNER_180, OUTER_CORNER
        }
    }

    override fun compareTo(o: CatwalkRenderData): Int {
        return 0
    }

    enum class EnumCatwalkCornerType {
        OUTER, OUTER_180, INNER
    }
}
