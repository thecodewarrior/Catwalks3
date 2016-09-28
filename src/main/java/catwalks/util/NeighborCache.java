package catwalks.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by TheCodeWarrior
 */
public class NeighborCache<T> {
	
	Map<BlockPos, T> cache = new HashMap<>();
	Function<BlockPos, T> generator;
	BlockPos basePos;
	
	public NeighborCache(BlockPos pos, Function<BlockPos, T> generator) {
		this.basePos = pos;
		this.generator = generator;
	}
	
	public T get(int x, int y, int z) {
		return get(new BlockPos(x,y,z));
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
		if(!cache.containsKey(relPos))
			cache.put(relPos, generator.apply(relPos.add(basePos)));
		return cache.get(relPos);
	}
	
}
