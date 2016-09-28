package catwalks.util.meta;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class ArrayProp<T> extends MetaValueProperty<T> {
	
	protected IntProp intProp;
	protected T[] array;
	
	public ArrayProp(int index, T[] array, int bits) {
		this.array = array;
		this.intProp = new IntProp(index, bits);
	}
	
	public boolean addBits(int index, int bits) {
		this.intProp.addBits(index, bits);
		return true;
	}
	
	public void set(MetaStorage storage, T value) {
		int i = ArrayUtils.indexOf(array, value);
		if(i < 0)
			throw new NoSuchElementException("Error setting array property! " + value.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		this.intProp.set(storage, i);
		storage.notifyIfDirty();
	}
	
	public T get(MetaStorage storage) {
		int i = this.intProp.get(storage);
		if(i < 0 || i >= array.length)
			throw new IndexOutOfBoundsException("Error getting array property! " + i + " isn't a valid index in source array " + Arrays.toString(array));
		return array[i];
	}
	
	
}
