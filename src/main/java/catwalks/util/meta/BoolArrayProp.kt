package catwalks.util.meta

import java.util.*

/**
 * Created by TheCodeWarrior
 */
class BoolArrayProp(index: Int, bits: Int) : MetaMapProperty<Int, Boolean>() {
    protected var indices: MutableList<Int> = ArrayList()
    protected var bits: MutableList<Int> = ArrayList()

    init {
        this.indices.add(index)
        this.bits.add(bits)
    }

    override fun addBits(index: Int, bits: Int): Boolean {
        this.indices.add(index)
        this.bits.add(bits)
        return true
    }

    override fun set(storage: MetaStorage, v: Int, value: Boolean) {

        if (v < 0)
            throw IndexOutOfBoundsException("Can't have negative array index!")

        var beginningBitIndex = 0
        for (i in indices.indices) {
            val index = indices[i]
            val len = bits[i]
            if (beginningBitIndex <= v && beginningBitIndex + len > v) {
                val subindex = v - beginningBitIndex
                storage.set(index + subindex, value)
                storage.notifyIfDirty()
                return
            }
            beginningBitIndex += len
        }
        throw NoSuchElementException("Couldn't find an allocated bit for array element " + v + "! Allocated bits can only fit " + (beginningBitIndex + 1) + " values!")
    }

    override fun get(storage: MetaStorage, v: Int): Boolean {
        if (v < 0)
            throw IndexOutOfBoundsException("Can't have negative array index!")
        var beginningBitIndex = 0
        for (i in indices.indices) {
            val index = indices[i]
            val len = bits[i]
            if (beginningBitIndex <= v && beginningBitIndex + len > v) {
                val subindex = v - beginningBitIndex
                return storage.get(index + subindex)
            }
            beginningBitIndex += len
        }
        throw NoSuchElementException("Couldn't find an allocated bit for array element " + v + "! Allocated bits can only fit " + (beginningBitIndex + 1) + " values!")
    }

    override fun getValue(storage: MetaStorage): String {
        var str = "["
        for (i in indices.indices) {
            val index = indices[i]
            val len = bits[i]
            for (j in 0..len - 1) {
                str += if (storage.get(index + j)) "1" else "0"
            }
        }
        str += "]"

        return str
    }
}
