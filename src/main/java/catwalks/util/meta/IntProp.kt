package catwalks.util.meta

import java.util.*

/**
 * Created by TheCodeWarrior
 */
class IntProp(index: Int, bits: Int) : MetaValueProperty<Int>() {

    protected var indices: MutableList<Int> = ArrayList()
    protected var bits: MutableList<Int> = ArrayList()

    init {
        addBits(index, bits)
    }

    override fun addBits(index: Int, bits: Int): Boolean {
        this.indices.add(index)
        this.bits.add(bits)
        return true
    }

    override fun set(storage: MetaStorage, value: Int) {
        var currentBit = 0
        for (i in indices.indices) {
            val index = indices[i]
            val len = bits[i]
            for (j in 0..len - 1) {
                storage.set(j + index, (value shr currentBit and 1) == 1)
                currentBit++
            }
        }
        storage.notifyIfDirty()
    }

    override fun get(storage: MetaStorage): Int {
        var value = 0
        var currentBit = 0

        for (i in indices.indices) {
            val index = indices[i]
            val len = bits[i]
            for (j in 0..len - 1) {
                value += if (storage.get(j + index)) 1 shl currentBit else 0
                currentBit++
            }
        }

        return value
    }
}
