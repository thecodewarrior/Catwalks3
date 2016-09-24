package catwalks.block.extended.tileprops;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class BoolArrayProp<T> {
	protected int index;
	protected T[] array;
	protected int bits;
	
	public BoolArrayProp(int index, T[] array) {
		this.index = index;
		this.array = array;
		this.bits = array.length;
	}
	
	public BoolArrayProp(int index, T[] array, int bits) {
		this.index = index;
		this.array = array;
		this.bits = Math.max(array.length, bits);
	}
	
	public int getBits() {
		return bits;
	}
	
	public void set(TileExtended tile, T key, boolean value) {
		int i = ArrayUtils.indexOf(array, key);
		if(i < 0)
			throw new NoSuchElementException("Error setting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		tile.setBoolean(index, value);
	}
	
	public boolean get(TileExtended tile) {
		return tile.getBoolean(index);
	}
}
