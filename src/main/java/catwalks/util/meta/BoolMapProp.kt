package catwalks.util.meta

import org.apache.commons.lang3.ArrayUtils
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class BoolMapProp<T>(index: Int, protected var array: Array<T>, bits: Int) : MetaMapProperty<T, Boolean>() {
    protected var prop: BoolArrayProp

    init {
        if (bits < array.size)
            throw IllegalArgumentException("Can't fit array of length " + array.size + " in " + bits + " bits!")
        prop = BoolArrayProp(index, bits)
    }

    override fun addBits(index: Int, bits: Int): Boolean {
        return prop.addBits(index, bits)
    }

    override fun set(storage: MetaStorage, key: T, value: Boolean) {
        val v = ArrayUtils.indexOf(array, key)
        if (v < 0)
            throw NoSuchElementException("Error setting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".")
        prop.set(storage, v, value)
    }

    override fun get(storage: MetaStorage, key: T): Boolean {
        val v = ArrayUtils.indexOf(array, key)
        if (v < 0)
            throw NoSuchElementException("Error getting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".")
        return prop.get(storage, v)
    }

    override fun getValue(storage: MetaStorage): String {
        var trues = ""
        var falses = ""

        for (value in array) {
            if (get(storage, value)) {
                trues += "$value, "
            } else {
                falses += "$value, "
            }
        }

        return String.format("{\n    true: [%s],\n    false: [%s]\n}", trues, falses)
    }
}
