package catwalks.util.meta

/**
 * Created by TheCodeWarrior
 */
class BoolProp(protected var index: Int) : MetaValueProperty<Boolean>() {

    override fun addBits(index: Int, bits: Int): Boolean {
        return false
    }

    override fun set(storage: MetaStorage, value: Boolean) {
        storage.set(index, value)
        storage.notifyIfDirty()
    }

    override fun get(storage: MetaStorage): Boolean {
        return storage.get(index)
    }
}
