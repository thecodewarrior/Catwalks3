package catwalks.util.meta;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class BoolMapProp<T> extends MetaMapProperty<T, Boolean> {
	protected BoolArrayProp prop;
	protected T[] array;
	
	public BoolMapProp(int index, T[] array, int bits) {
		this.array = array;
		if(bits < array.length)
			throw new IllegalArgumentException("Can't fit array of length " + array.length + " in " + bits + " bits!");
		prop = new BoolArrayProp(index, bits);
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		return prop.addBits(index, bits);
	}
	
	@Override
	public void set(MetaStorage storage, T key, Boolean value) {
		int val = ArrayUtils.indexOf(array, key);
		if(val < 0)
			throw new NoSuchElementException("Error setting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		prop.set(storage, val, value);
	}
	
	@Override
	public Boolean get(MetaStorage storage, T key) {
		int val = ArrayUtils.indexOf(array, key);
		if(val < 0)
			throw new NoSuchElementException("Error getting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		return prop.get(storage, val);
	}
	
	@Override
	public String getValue(MetaStorage storage) {
		String trues = "";
		String falses = "";
		
		for(T value : array) {
			if(get(storage, value)) {
				trues += value + ", ";
			} else {
				falses += value + ", ";
			}
		}
		
		return String.format("{\n    true: [%s],\n    false: [%s]\n}", trues, falses);
	}
}
