package catwalks.util.meta;

/**
 * Created by TheCodeWarrior
 */
public class FloatProp extends MetaValueProperty<Float> {
	
	protected IntProp intProp;
	protected float coefficient;
	
	public FloatProp(int index, int bits) {
		intProp = new IntProp(index, bits);
		coefficient = (float)Math.pow(2, bits/2);
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		intProp.addBits(index, bits);
		return true;
	}
	
	@Override
	public void set(MetaStorage storage, Float value) {
		int intVal = (int)( value * coefficient );
		
		intProp.set(storage, intVal);
		storage.notifyIfDirty();
	}
	
	@Override
	public Float get(MetaStorage storage) {
		int intVal = intProp.get(storage);
		return intVal / coefficient;
	}
}
