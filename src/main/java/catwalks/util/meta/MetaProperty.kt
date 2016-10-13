package catwalks.util.meta

/**
 * Created by TheCodeWarrior
 */
abstract class MetaProperty {
    /**
     * Returns true if the property supports bit extensions
     */
    abstract fun addBits(index: Int, bits: Int): Boolean

    abstract fun getValue(storage: MetaStorage): String
}
