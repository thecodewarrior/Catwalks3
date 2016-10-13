package catwalks.util.meta

/**
 * Created by TheCodeWarrior
 */
abstract class MetaValueProperty<T> : MetaProperty() {

    abstract operator fun set(storage: MetaStorage, value: T)
    abstract operator fun get(storage: MetaStorage): T

    override fun getValue(storage: MetaStorage): String {
        return get(storage).toString()
    }
}
