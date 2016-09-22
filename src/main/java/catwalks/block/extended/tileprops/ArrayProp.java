package catwalks.block.extended.tileprops;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class ArrayProp<T> {
	
	protected int index, bits;
	protected T[] array;
	
	public ArrayProp(int index, T[] array) {
		this.index = index;
		this.array = array;
		
		int value = array.length;
		int count = 0;
		while (value > 0) {
			count++;
			value = value >> 1;
		}
		
		this.bits = count;
	}
	
	public ArrayProp(int index, T[] array, int fitSize) {
		this.index = index;
		this.array = array;
		
		int value = Math.max(array.length, fitSize);
		int count = 0;
		while (value > 0) {
			count++;
			value = value >> 1;
		}
		
		this.bits = count;
	}
	
	public int getBits() { return bits; }
	
	public void set(TileExtended tile, T value) {
		int i = ArrayUtils.indexOf(array, value);
		if(i < 0)
			throw new NoSuchElementException("Error setting array property! " + value.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		tile.setNumber(index, bits, i);
	}
	
	public T get(TileExtended tile) {
		int i = tile.getNumber(index, bits);
		if(i < 0 || i >= array.length)
			throw new IndexOutOfBoundsException("Error getting array property! " + i + " isn't a valid index in source array " + Arrays.toString(array));
		return array[i];
	}
	
	
}
