package catwalks.util.meta

import org.apache.commons.lang3.ArrayUtils
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class ArrayProp<T>(index: Int, protected var array: Array<T>, bits: Int) : MetaValueProperty<T>() {

    protected var intProp: IntProp

    init {
        this.intProp = IntProp(index, bits)
    }

    override fun addBits(index: Int, bits: Int): Boolean {
        this.intProp.addBits(index, bits)
        return true
    }

    override fun set(storage: MetaStorage, value: T) {
        val i = ArrayUtils.indexOf(array, value)
        if (i < 0)
            throw NoSuchElementException("Error setting array property! " + value.toString() + " isn't in source array " + Arrays.toString(array) + ".")
        this.intProp.set(storage, i)
        storage.notifyIfDirty()
    }

    override fun get(storage: MetaStorage): T {
        val i = this.intProp.get(storage)
        if (i < 0 || i >= array.size)
            throw IndexOutOfBoundsException("Error getting array property! " + i + " isn't a valid index in source array " + Arrays.toString(array))
        return array[i]
    }


}
