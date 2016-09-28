package catwalks.util.meta;

/**
 * Created by TheCodeWarrior
 */
public abstract class MetaValueProperty<T> extends MetaProperty {
	
	public abstract void set(MetaStorage storage, T value);
	public abstract T get(MetaStorage storage);
	
	@Override
	public String getValue(MetaStorage storage) {
		return get(storage).toString();
	}
}
