package catwalks.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by TheCodeWarrior
 */
public class NeighborCache<T> {
	
	final Map<BlockPos, T> cache = new HashMap<>();
	Function<BlockPos, T> generator;
	BlockPos basePos;
	
	public T get(int x, int y, int z) {
		return get(new BlockPos(x,y,z));
	}
	
	public T getAbsolute(BlockPos absolutePos) {
		return  getOrPut(absolutePos.subtract(basePos));
	}
	
	public T get(BlockPos relPos) {
		return getOrPut(relPos);
	}
	
	public T get(EnumFacing dir) {
		return get(BlockPos.ORIGIN.offset(dir));
	}
	
	public T get(EnumFacing dir1, EnumFacing dir2) {
		return get(BlockPos.ORIGIN.offset(dir1).offset(dir2));
	}
	
	public T get(EnumFacing dir1, EnumFacing dir2, EnumFacing dir3) {
		return get(BlockPos.ORIGIN.offset(dir1).offset(dir2).offset(dir3));
	}
	
	private T getOrPut(BlockPos relPos) {
		if(generator == null || basePos == null) {
			throw new IllegalStateException("You must initialize the cache before using it!");
		}
		if(!cache.containsKey(relPos))
			cache.put(relPos, generator.apply(relPos.add(basePos)));
		return cache.get(relPos);
	}
	
	public void clear() {
		cache.clear();
		generator = null;
		basePos = null;
	}
	
	public void init(BlockPos pos, Function<BlockPos, T> generator) {
		clear();
		this.basePos = pos;
		this.generator = generator;
	}
	
}
