package catwalks.util.nestedmap

import java.util.*

/**
 * Created by TheCodeWarrior
 */
internal open class HierarchyKey(protected var array: Array<out Any>?) {


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is HierarchyKey) return false

// Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(array, o.array)

    }

    override fun hashCode(): Int {
        return Arrays.hashCode(array)
    }

    class MutableHierarchyKey : HierarchyKey(null) {

        fun set(keys: Array<out Any>): MutableHierarchyKey {
            this.array = keys
            return this
        }
    }

    companion object {

        val ACCESS_KEY: ThreadLocal<MutableHierarchyKey> = object : ThreadLocal<MutableHierarchyKey>() {
            override fun initialValue(): MutableHierarchyKey {
                return MutableHierarchyKey()
            }
        }
    }
}
