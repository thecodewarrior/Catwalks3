package catwalks.util.meta

/**
 * Created by TheCodeWarrior
 */
class FloatProp(index: Int, bits: Int) : MetaValueProperty<Float>() {

    protected var intProp: IntProp
    protected var coefficient: Float = 0.toFloat()

    init {
        intProp = IntProp(index, bits)
        coefficient = Math.pow(2.0, (bits / 2).toDouble()).toFloat()
    }

    override fun addBits(index: Int, bits: Int): Boolean {
        intProp.addBits(index, bits)
        return true
    }

    override fun set(storage: MetaStorage, value: Float) {
        val intVal = (value * coefficient).toInt()

        intProp.set(storage, intVal)
        storage.notifyIfDirty()
    }

    override fun get(storage: MetaStorage): Float {
        val intVal = intProp.get(storage)
        return intVal / coefficient
    }
}
