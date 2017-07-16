package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.kotlin.enumMapOf
import com.teamwizardry.librarianlib.features.saving.Save
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import thecodewarrior.catwalks.model.CatwalkState
import thecodewarrior.catwalks.model.FloorSection
import thecodewarrior.catwalks.model.RailSection

/**
 * TODO: Document file TileCatwalk
 *
 * Created by TheCodeWarrior
 */
@TileRegister("catwalk")
class TileCatwalk : TileMod() {
    @Save var sides = enumMapOf<EnumFacing, Boolean>()
    @Save var material = CatwalkMaterial.CLASSIC

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

        fun calculate(): CatwalkState {
            return CatwalkState(
                    getRailState(EnumFacing.WEST, EnumFacing.NORTH),
                    getRailState(EnumFacing.EAST, EnumFacing.NORTH),
                    getRailState(EnumFacing.WEST, EnumFacing.SOUTH),
                    getRailState(EnumFacing.EAST, EnumFacing.SOUTH),

                    if(!has(EnumFacing.DOWN)) null else getFloorState(EnumFacing.WEST, EnumFacing.NORTH),
                    if(!has(EnumFacing.DOWN)) null else getFloorState(EnumFacing.EAST, EnumFacing.NORTH),
                    if(!has(EnumFacing.DOWN)) null else getFloorState(EnumFacing.WEST, EnumFacing.SOUTH),
                    if(!has(EnumFacing.DOWN)) null else getFloorState(EnumFacing.EAST, EnumFacing.SOUTH)
            )
        }

        fun getRailState(xAxis: EnumFacing, zAxis: EnumFacing): RailSection {
            val posX = pos.offset(xAxis)
            val posZ = pos.offset(zAxis)
            val corner = pos.offset(xAxis).offset(zAxis)

            if(has(xAxis) && has(zAxis))
                return RailSection.OUTER


            if(has(xAxis)) {
                if(exists(posZ) && !has(zAxis.opposite, posZ)) {
                    if(has(xAxis, posZ)) {
                        return RailSection.Z_EDGE
                    } else if(exists(corner) && has(zAxis.opposite, corner) && !has(xAxis.opposite, corner)) {
                        return RailSection.Z_EDGE
                    }
                }
                return RailSection.Z_END
            }
            if(has(zAxis)) {
                if(exists(posX) && !has(xAxis.opposite, posX)) {
                    if (has(zAxis, posX)) {
                        return RailSection.X_EDGE
                    } else if(exists(corner) && has(xAxis.opposite, corner) && !has(zAxis.opposite, corner)) {
                        return RailSection.X_EDGE
                    }
                }
                return RailSection.X_END
            }

            if(!has(xAxis) && !has(zAxis)) {
                if(exists(posX) && !has(xAxis.opposite, posX) && has(zAxis, posX)
                        && exists(posZ) && !has(zAxis.opposite, posZ) && has(xAxis, posZ)
                        ) {
                    return RailSection.INNER
                }
            }
            return RailSection.MIDDLE
        }

        fun getFloorState(xAxis: EnumFacing, zAxis: EnumFacing): FloorSection {
            val posX = pos.offset(xAxis)
            val posZ = pos.offset(zAxis)
            val corner = pos.offset(xAxis).offset(zAxis)

            if(has(xAxis) && has(zAxis))
                return FloorSection.OUTER

            if(!has(xAxis) && !has(zAxis)
                    && exists(posX) && has(EnumFacing.DOWN, posX) && !has(xAxis.opposite, posX)
                    && exists(posZ) && has(EnumFacing.DOWN, posZ) && !has(zAxis.opposite, posZ)
                    ) {
                if(exists(corner) && has(EnumFacing.DOWN, corner) && !has(xAxis.opposite, corner) && !has(zAxis.opposite, corner))
                    return FloorSection.MIDDLE
                return FloorSection.INNER
            }

            if(!has(zAxis)) {
                if(exists(posZ) && has(EnumFacing.DOWN, posZ) && !has(zAxis.opposite, posZ))
                    return FloorSection.Z_EDGE
            }
            if(!has(xAxis)) {
                if(exists(posX) && has(EnumFacing.DOWN, posX) && !has(xAxis.opposite, posX))
                    return FloorSection.X_EDGE
            }

            return FloorSection.OUTER
        }

    }
}
