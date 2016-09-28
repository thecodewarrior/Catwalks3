package catwalks.util.meta;

/**
 * Created by TheCodeWarrior
 */
public class BoolProp extends MetaValueProperty<Boolean> {
	
	protected int index;
	
	public BoolProp(int index) {
		this.index = index;
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		return false;
	}
	
	@Override
	public void set(MetaStorage storage, Boolean value) {
		storage.set(index, value);
		storage.notifyIfDirty();
	}
	
	@Override
	public Boolean get(MetaStorage storage) {
		return storage.get(index);
	}
}
