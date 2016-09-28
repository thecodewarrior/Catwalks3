package catwalks.util.meta;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class BoolMapProp<T> extends MetaMapProperty<T, Boolean> {
	protected List<Integer> indices = new ArrayList<>(), bits = new ArrayList<>();
	
	protected T[] array;
	
	public BoolMapProp(int index, T[] array, int bits) {
		this.array = array;
		if(bits < array.length)
			throw new IllegalArgumentException("Can't fit array of length " + array.length + " in " + bits + " bits!");
		this.indices.add(index);
		this.bits.add(bits);
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		this.indices.add(index);
		this.bits.add(bits);
		return true;
	}
	
	@Override
	public void set(MetaStorage storage, T key, Boolean value) {
		int val = ArrayUtils.indexOf(array, key);
		if(val < 0)
			throw new NoSuchElementException("Error setting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		
		int beginningBitIndex = 0;
		for(int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			if(beginningBitIndex <= val && beginningBitIndex+len > val) {
				int subindex = val - beginningBitIndex;
				storage.set(index+subindex, value);
				storage.notifyIfDirty();
				return;
			}
			beginningBitIndex += len;
		}
		throw new NoSuchElementException("Couldn't find an allocated bit for array element " + val + "! Allocated bits can only fit " + (beginningBitIndex+1) + " values!");
	}
	
	@Override
	public Boolean get(MetaStorage storage, T key) {
		int val = ArrayUtils.indexOf(array, key);
		if(val < 0)
			throw new NoSuchElementException("Error getting array boolean property! " + key.toString() + " isn't in source array " + Arrays.toString(array) + ".");
		int beginningBitIndex = 0;
		for(int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			if(beginningBitIndex <= val && beginningBitIndex+len > val) {
				int subindex = val - beginningBitIndex;
				return storage.get(index+subindex);
			}
			beginningBitIndex += len;
		}
		throw new NoSuchElementException("Couldn't find an allocated bit for array element " + val + "! Allocated bits can only fit " + (beginningBitIndex+1) + " values!");
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
