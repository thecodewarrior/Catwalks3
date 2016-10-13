package catwalks.util.meta

/**
 * Created by TheCodeWarrior
 */
abstract class MetaMapProperty<K, V> : MetaProperty() {
    abstract operator fun set(storage: MetaStorage, key: K, value: V)
    abstract operator fun get(storage: MetaStorage, key: K): V

}
