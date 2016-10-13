package catwalks.block.property

/**
 * Created by TheCodeWarrior
 */
class UPropertyObject<T : Comparable<T>>(name: String, clazz: Class<T>) : UPropertyHelper<T>(name, clazz) {

    override fun isValid(value: T): Boolean {
        return true
    }

    override fun valueToString(value: T): String {
        return "complexData"
    }
}
