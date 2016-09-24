package catwalks.block.extended.tileprops;

/**
 * Created by TheCodeWarrior
 */
public class ExtendedTileProperties {
	protected int index = 0;
	
	public void bufferBits(int bits) {
		index += bits;
	}
	
	public BoolProp allocateBool() {
		BoolProp p = new BoolProp(index);
		index++;
		return p;
	}
	
	public IntProp allocateInt(int bits) {
		IntProp p = new IntProp(index, bits);
		index += bits;
		return p;
	}
	
	public FloatProp allocateFloat(int bits) {
		FloatProp p = new FloatProp(index, bits);
		index += bits;
		return p;
	}
	
	public <T> ArrayProp<T> allocateArray(T[] values) {
		ArrayProp<T> p = new ArrayProp(index, values);
		index += p.getBits();
		return p;
	}
	
	public <T> ArrayProp<T> allocateArray(T[] values, int ensureSize) {
		ArrayProp<T> p = new ArrayProp(index, values, ensureSize);
		index += p.getBits();
		return p;
	}
	
	public <T> BoolArrayProp<T> allocateBoolArray(T[] values) {
		BoolArrayProp<T> p = new BoolArrayProp(index, values);
		index += p.getBits();
		return p;
	}
	
	public <T> BoolArrayProp<T> allocateBoolArray(T[] values, int ensureSize) {
		BoolArrayProp<T> p = new BoolArrayProp(index, values, ensureSize);
		index += p.getBits();
		return p;
	}
}
