package thecodewarrior.catwalks

import net.minecraftforge.common.property.IUnlistedProperty

/**
 * TODO: Document file UnlistedArbitraryProperty
 *
 * Created by TheCodeWarrior
 */
class UnlistedArbitraryProperty<T>(private val _name: String, private val _type: Class<T>) : IUnlistedProperty<T> {
    override fun valueToString(value: T) = value.toString()

    override fun isValid(value: T) = true

    override fun getName() = _name

    override fun getType() = _type
}
