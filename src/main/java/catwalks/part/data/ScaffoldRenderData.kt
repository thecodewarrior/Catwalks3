package catwalks.part.data

import net.minecraft.util.EnumFacing

/**
 * Created by TheCodeWarrior
 */
class ScaffoldRenderData(val sides: BooleanArray) : Comparable<ScaffoldRenderData> {
    override fun compareTo(other: ScaffoldRenderData): Int {
        return 0
    }
    companion object {
        val DEFAULT = ScaffoldRenderData(BooleanArray(EnumFacing.values().size) { true })
    }
}
