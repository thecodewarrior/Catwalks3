package catwalks.block.property;

import com.google.common.base.Objects;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class UPropertyHelper<T extends Comparable<T>> implements IUnlistedProperty<T>
{
    private final Class<T> valueClass;
    private final String name;

    protected UPropertyHelper(String name, Class<T> valueClass)
    {
        this.valueClass = valueClass;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public Class<T> getValueClass()
    {
        return this.valueClass;
    }

    public String toString()
    {
        return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).toString();
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            UPropertyHelper propertyhelper = (UPropertyHelper)p_equals_1_;
            return this.valueClass.equals(propertyhelper.valueClass) && this.name.equals(propertyhelper.name);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return 31 * this.valueClass.hashCode() + this.name.hashCode();
    }
    
	@Override
	public Class<T> getType() {
		return valueClass;
	}
}
