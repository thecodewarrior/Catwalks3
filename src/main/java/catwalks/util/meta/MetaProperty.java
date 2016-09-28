package catwalks.util.meta;

/**
 * Created by TheCodeWarrior
 */
public abstract class MetaProperty {
	/**
	 * Returns true if the property supports bit extensions
	 */
	public abstract boolean addBits(int index, int bits);
	
	public abstract String getValue(MetaStorage storage);
}
