package catwalks.util.meta;

/**
 * Created by TheCodeWarrior
 */
public abstract class MetaMapProperty<K, V> extends MetaProperty {
	public abstract void set(MetaStorage storage, K key, V value);
	public abstract V get(MetaStorage storage, K key);
	
}
