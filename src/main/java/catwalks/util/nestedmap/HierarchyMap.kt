package catwalks.util.nestedmap

import java.util.HashMap

/**
 * Created by TheCodeWarrior
 */
class HierarchyMap<T>(val depth: Int) {

    private val map = HashMap<HierarchyKey, T>()

    fun put(value: T, vararg keys: Any) {
        if (keys.size != depth) {
            throw IllegalArgumentException(String.format("Provided key length %d doesn't match depth %d", keys.size, depth))
        }
        map.put(HierarchyKey(keys), value)
    }

    operator fun get(vararg keys: Any): T? {
        if (keys.size != depth) {
            throw IllegalArgumentException(String.format("Provided key length %d doesn't match depth %d", keys.size, depth))
        }
        return map[HierarchyKey.ACCESS_KEY.get().set(keys)]
    }
}
