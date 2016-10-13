package catwalks.util.meta

import java.util.*

/**
 * Created by TheCodeWarrior
 */
class MetaStorage(protected var allocator: MetaStorage.Allocator) {

    var meta = BitSet()
    protected var dirtyListener: IDirtyable? = null

    constructor(allocator: Allocator, dirtyListener: IDirtyable) : this(allocator) {
        this.dirtyListener = dirtyListener
    }

    protected var dirty = false
    protected fun markDirty() {
        dirty = true
    }

    fun notifyIfDirty() {
        if (dirty && dirtyListener != null)
            dirtyListener!!.markDirty()
        dirty = false
    }

    operator fun get(bit: Int): Boolean {
        return meta.get(bit)
    }

    operator fun set(bit: Int, value: Boolean) {
        if (meta.get(bit) != value)
            markDirty()
        meta.set(bit, value)
    }

    fun toByteArray(): ByteArray {
        return meta.toByteArray()
    }

    fun fromByteArray(array: ByteArray) {
        meta = BitSet.valueOf(array)
    }

    fun toLongArray(): LongArray {
        return meta.toLongArray()
    }

    fun fromLongArray(array: LongArray) {
        meta = BitSet.valueOf(array)
    }

    override fun toString(): String {
        val builder = StringBuilder()

        val len = allocator.length
        for (i in 0..len - 1) {
            if (meta.get(i))
                builder.append("1")
            else
                builder.append("0")
        }

        builder.append("\n")

        builder.append(allocator.toString(this))

        return builder.toString()
    }

    class Allocator {
        var length = 0
            protected set
        protected var props: MutableMap<String, MetaProperty> = HashMap()

        fun bufferBits(bits: Int) {
            length += bits
        }

        fun allocateBool(name: String): BoolProp {
            val p = BoolProp(length)
            length++
            props.put(name, p)
            return p
        }

        fun allocateInt(name: String, bits: Int): IntProp {
            val p = IntProp(length, bits)
            length += bits
            props.put(name, p)
            return p
        }

        fun allocateFloat(name: String, bits: Int): FloatProp {
            val p = FloatProp(length, bits)
            length += bits
            props.put(name, p)
            return p
        }

        fun <T> allocateArray(name: String, values: Array<T>, bits: Int): ArrayProp<T> {
            val p = ArrayProp(length, values, bits)
            length += bits
            props.put(name, p)
            return p
        }

        fun <T> allocateBoolMap(name: String, values: Array<T>, bits: Int): BoolMapProp<T> {
            val p = BoolMapProp(length, values, bits)
            length += bits
            props.put(name, p)
            return p
        }

        fun allocateBoolArray(name: String, bits: Int): BoolArrayProp {
            val p = BoolArrayProp(length, bits)
            length += bits
            props.put(name, p)
            return p
        }

        fun addBits(prop: MetaProperty, bits: Int) {
            if (prop.addBits(length, bits))
                length += bits
            else
                throw IllegalArgumentException("Prop class " + prop.javaClass + " can not have bits added to it!")
        }

        fun toString(storage: MetaStorage): String {
            val builder = StringBuilder()

            for ((key, value) in props) {
                builder.append(key).append(" : ").append(value.getValue(storage)).append("\n")
            }

            return builder.toString()
        }
    }
}
