package catwalks.block.property

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import com.google.common.collect.Collections2
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import net.minecraft.util.IStringSerializable

class UPropertyEnum<T : Enum<T>> protected constructor(name: String, valueClass: Class<T>, allowedValues: Collection<T>) : UPropertyHelper<T>(name, valueClass) where T : IStringSerializable {
    private val allowedValues: ImmutableSet<T>
    private val nameToValue = Maps.newHashMap<String, T>()

    init {
        this.allowedValues = ImmutableSet.copyOf(allowedValues)

        for (t in allowedValues) {
            val s = (t as IStringSerializable).name

            if (this.nameToValue.containsKey(s)) {
                throw IllegalArgumentException("Multiple values have the same name \'" + s + "\'")
            }

            this.nameToValue.put(s, t)
        }
    }

    fun getAllowedValues(): Collection<T> {
        return this.allowedValues
    }

    /**
     * Get the name for the given value.
     */
    fun getName(value: T): String {
        return (value as IStringSerializable).name
    }

    override fun isValid(value: T): Boolean {
        return allowedValues.contains(value)
    }

    override fun valueToString(value: T): String {
        return value.getName()
    }

    companion object {

        fun <T : Enum<T>> create(name: String, clazz: Class<T>): UPropertyEnum<T> where T : IStringSerializable {
            /**
             * Create a new PropertyEnum with all Enum constants of the given class that match the given Predicate.
             */
            return create(name, clazz, Predicates.alwaysTrue<T>())
        }

        fun <T : Enum<T>> create(name: String, clazz: Class<T>, filter: Predicate<T>): UPropertyEnum<T> where T : IStringSerializable {
            /**
             * Create a new PropertyEnum with the specified values
             */
            return create(name, clazz, Collections2.filter(Lists.newArrayList(*clazz.enumConstants), filter))
        }

        fun <T : Enum<T>> create(name: String, clazz: Class<T>, vararg values: T): UPropertyEnum<T> where T : IStringSerializable {
            /**
             * Create a new PropertyEnum with the specified values
             */
            return create(name, clazz, Lists.newArrayList(*values))
        }

        fun <T : Enum<T>> create(name: String, clazz: Class<T>, values: Collection<T>): UPropertyEnum<T> where T : IStringSerializable {
            return UPropertyEnum(name, clazz, values)
        }
    }
}