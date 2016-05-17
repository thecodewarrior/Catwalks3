package catwalks.raytrace.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import catwalks.block.BlockCatwalkBase.BlockTraceParam;
import catwalks.raytrace.RayTraceUtil.ITraceablePrimitive;
import catwalks.util.GeneralUtil;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockTraceFactory {

	private static final Vec3d   toCenter = new Vec3d(-0.5, 0, -0.5);
	private static final Vec3d fromCenter = new Vec3d(0.5, 0, 0.5);
	
	
	private ITraceablePrimitive<?> shown;
	private ITraceablePrimitive<?> hidden;
	private IProperty<Boolean> normalEnable;
	private IUnlistedProperty<Boolean> unlistedEnable;
	private Predicate<BlockTraceParam> predEnable;
	private EnumFacing side;
	private BlockPos offset;
	private boolean ignoreWrench;
	private boolean alwaysShow;
	
	private List<BlockTracable> list = new ArrayList<>();
	
	public void commit() {
		BlockTracable tracable = new BlockTracable(shown.clone(), hidden.clone(), normalEnable, unlistedEnable, predEnable, side, offset, ignoreWrench, alwaysShow);
		list.add(tracable);
	}
	
	public void rotate(int rotation) {
		shown.translate(toCenter);
		shown.rotate(rotation);
		shown.translate(fromCenter);
		
		hidden.translate(toCenter);
		hidden.rotate(rotation);
		hidden.translate(fromCenter);
		
		side = GeneralUtil.rotateFacing(rotation, side);
	}
	
	public void translate(double x, double y, double z) {
		Vec3d amount = new Vec3d(x, y, z);
		shown.translate(amount);
		hidden.translate(amount);
	}
	
	public void rotateAll(int rotation) {
		List<BlockTracable> newList = new ArrayList<>();
		for (BlockTracable trace : list) {
			BlockTracable clone = trace.clone();
			clone.rotate(rotation);
			newList.add(clone);
		}
		list = newList;
	}
	
	public List<BlockTracable> build() {
		return ImmutableList.copyOf(list);
	}
	
	public void empty() {
		list = new ArrayList<>();
	}
	
	{ /* setters */ }
	
	public void setShown(ITraceablePrimitive<?> shown) {
		this.shown = shown;
	}
	public void setHidden(ITraceablePrimitive<?> hidden) {
		this.hidden = hidden;
	}
	public void setEnable(IProperty<Boolean> normalEnable) {
		this.normalEnable = normalEnable;
		this.unlistedEnable = null;
		this.predEnable = null;
	}
	public void setEnable(IUnlistedProperty<Boolean> unlistedEnable) {
		this.unlistedEnable = unlistedEnable;
		this.normalEnable = null;
		this.predEnable = null;
	}
	public void setEnable(Predicate<BlockTraceParam> predEnable) {
		this.predEnable = predEnable;
		this.unlistedEnable = null;
		this.normalEnable = null;
	}
	public void setSide(EnumFacing side) {
		this.side = side;
	}
	public void setOffset(int x, int y, int z) {
		this.offset = new BlockPos(x, y, z);
	}
	public void resetOffset() {
		this.offset = BlockPos.ORIGIN;
	}
	public void setIgnoreWrench(boolean value) {
		this.ignoreWrench = value;
	}
	public void setAlwaysShow(boolean value) {
		this.alwaysShow = value;
	}
}
