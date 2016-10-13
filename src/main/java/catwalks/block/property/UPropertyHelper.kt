package catwalks.block.property

import com.google.common.base.Objects
import net.minecraftforge.common.property.IUnlistedProperty

abstract class UPropertyHelper<T : Comparable<T>> protected constructor(private val name: String, val valueClass: Class<T>) : IUnlistedProperty<T> {

    override fun getName(): String {
        return this.name
    }

    override fun toString(): String {
        return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).toString()
    }

    override fun equals(p_equals_1_: Any?): Boolean {
        if (this === p_equals_1_) {
            return true
        } else if (p_equals_1_ != null && this.javaClass == p_equals_1_.javaClass) {
            val propertyhelper = p_equals_1_ as UPropertyHelper<*>?
            return this.valueClass == propertyhelper!!.valueClass && this.name == propertyhelper.name
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return 31 * this.valueClass.hashCode() + this.name.hashCode()
    }

    override fun getType(): Class<T> {
        return valueClass
    }
}
