package catwalks.part.data

import net.minecraft.util.EnumFacing

/**
 * Created by TheCodeWarrior
 */
class ScaffoldRenderData : Comparable<ScaffoldRenderData> {
    override fun compareTo(other: ScaffoldRenderData): Int {
        return 0
    }

    val sides = BooleanArray(EnumFacing.values().size)
}
