package catwalks.util

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.util.function.Function

/**
 * Created by TheCodeWarrior
 */
class NeighborCache<T> {

    internal val cache: MutableMap<BlockPos, T> = mutableMapOf()
    internal var generator: Function<BlockPos, T>? = null
    internal var basePos: BlockPos? = null

    operator fun get(x: Int, y: Int, z: Int): T {
        return get(BlockPos(x, y, z))
    }

    fun getAbsolute(absolutePos: BlockPos): T {
        basePos?.let {
            return getOrPut(absolutePos.subtract(it))
        }
        throw IllegalStateException("Cache not initialized!")
    }

    operator fun get(relPos: BlockPos): T {
        return getOrPut(relPos)
    }

    operator fun get(dir: EnumFacing): T {
        return get(BlockPos.ORIGIN.offset(dir))
    }

    operator fun get(dir1: EnumFacing, dir2: EnumFacing): T {
        return get(BlockPos.ORIGIN.offset(dir1).offset(dir2))
    }

    operator fun get(dir1: EnumFacing, dir2: EnumFacing, dir3: EnumFacing): T {
        return get(BlockPos.ORIGIN.offset(dir1).offset(dir2).offset(dir3))
    }

    private fun getOrPut(relPos: BlockPos): T {
        val gen = generator
        val pos = basePos
        if (gen == null || pos == null) {
            throw IllegalStateException("You must initialize the cache before using it!")
        }
        return cache.getOrPut(relPos) {
            gen.apply(relPos.add(pos))
        }
    }

    fun clear() {
        cache.clear()
        generator = null
        basePos = null
    }

    fun init(pos: BlockPos, generator: (BlockPos) -> T) {
        init(pos, Function(generator))
    }

    fun init(pos: BlockPos, generator: Function<BlockPos, T>) {
        clear()
        this.basePos = pos
        this.generator = generator
    }

}
