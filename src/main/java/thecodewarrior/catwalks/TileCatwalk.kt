package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.kotlin.enumMapOf
import com.teamwizardry.librarianlib.features.saving.Save
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import thecodewarrior.catwalks.model.CatwalkState
import thecodewarrior.catwalks.model.SectionType

/**
 * TODO: Document file TileCatwalk
 *
 * Created by TheCodeWarrior
 */
@TileRegister("catwalk")
class TileCatwalk : TileMod() {
    @Save var sides = enumMapOf<EnumFacing, Boolean>()
//    @Save var material
//    @Save var decorations
    init {
        EnumFacing.values().forEach { sides[it] = true }
    }

    fun has(side: EnumFacing): Boolean = sides[side] ?: false

    fun getCatwalkState(): CatwalkState {
        return CatwalkStateCalculator().calculate()
    }

    private inner class CatwalkStateCalculator {
        val tileCache = TLongObjectHashMap<TileCatwalk?>()

        fun getCached(pos: BlockPos): TileCatwalk? {
            val l = pos.toLong()
            if(tileCache.containsKey(l)) return tileCache[l]
            val tile = this@TileCatwalk.world.getTileEntity(pos) as? TileCatwalk
            tileCache.put(l, tile)
            return tile
        }

        fun exists(pos: BlockPos): Boolean {
            return getCached(pos) != null
        }

        fun has(side: EnumFacing, pos: BlockPos = this@TileCatwalk.pos): Boolean {
            return getCached(pos)?.has(side) ?: false
        }

        fun calculate() = CatwalkState(arrayOf(
                getCornerState(EnumFacing.WEST, EnumFacing.NORTH),
                getCornerState(EnumFacing.EAST, EnumFacing.NORTH),
                getCornerState(EnumFacing.EAST, EnumFacing.SOUTH),
                getCornerState(EnumFacing.WEST, EnumFacing.SOUTH)
        ))

        private fun getCornerState(xAxis: EnumFacing, zAxis: EnumFacing): SectionType {
            if(has(xAxis) && has(zAxis)) return SectionType.CLOSED
            if(has(xAxis)) {
                val calc = calculateRail(xAxis, zAxis)
                if(calc == SectionType.X_END) return SectionType.Z_END
                if(calc == SectionType.X_CONNECT) return SectionType.Z_CONNECT
                if(calc == SectionType.X_GAP) return SectionType.Z_GAP
                return calc
            }
            if(has(zAxis)) {
                val calc = calculateRail(zAxis, xAxis)
                return calc
            }
            // if both sides are open, then we have to check surroundings
            if(!has(xAxis.opposite, pos.offset(xAxis)) && !has(zAxis.opposite, pos.offset(zAxis))) {
                if(has(xAxis, pos.offset(zAxis)) && has(zAxis, pos.offset(xAxis)))
                    return SectionType.INNER
            }
            if(exists(pos.offset(xAxis)) && exists(pos.offset(zAxis)) &&
                    !has(xAxis.opposite, pos.offset(xAxis)) && !has(zAxis.opposite, pos.offset(zAxis)) &&
                    has(xAxis, pos.offset(zAxis)) != has(zAxis, pos.offset(xAxis))
            )
                return SectionType.INNER
            if(!has(xAxis.opposite, pos.offset(xAxis)) && !has(zAxis.opposite, pos.offset(zAxis)) &&
                    !has(zAxis, pos.offset(xAxis)) && !has(xAxis, pos.offset(zAxis)) &&
                    !has(zAxis.opposite, pos.offset(xAxis).offset(zAxis)) && !has(xAxis.opposite, pos.offset(xAxis).offset(zAxis)))
                return SectionType.MIDDLE
            val calcX = calculateOpenCorner(xAxis, zAxis)
            var calcZ = calculateOpenCorner(zAxis, xAxis)
            if(calcZ == SectionType.X_GAP) calcZ = SectionType.Z_GAP

            if(calcX != null && calcZ != null) {
                // if both X and Z are fighting for some reason, just do an open edge. It's safer that way.
                return SectionType.OPEN_EDGE
            }
            return calcX ?: calcZ ?: SectionType.OPEN_EDGE
        }

        private fun calculateRail(rail: EnumFacing, end: EnumFacing): SectionType {
            val touching = pos.offset(end)
            val corner = touching.offset(rail)
            val side = pos.offset(rail)
            if(exists(touching) && !has(end.opposite, touching))
                return SectionType.X_CONNECT
            if(!has(end.opposite, touching) && !has(rail, touching) && !has(rail.opposite, corner) && has(end.opposite, corner))
                return SectionType.X_CONNECT

            return SectionType.X_END
        }

        private fun calculateOpenCorner(rail: EnumFacing, end: EnumFacing): SectionType? {
            val touching = pos.offset(end)
            val corner = touching.offset(rail)
            val side = pos.offset(rail)
            if(!has(end.opposite, touching) && has(rail, touching))
                return SectionType.X_GAP
            if(!has(end.opposite, touching) && !has(rail, touching) && !has(rail.opposite, corner) && has(end.opposite, corner))
                return SectionType.X_GAP

            return null
        }

    }
}
