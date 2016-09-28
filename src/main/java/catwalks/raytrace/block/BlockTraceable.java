package catwalks.raytrace.block;

import catwalks.block.BlockCatwalkBase.BlockTraceParam;
import catwalks.block.BlockCatwalkBase.BlockTraceResult;
import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.*;
import catwalks.util.GeneralUtil;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.function.Predicate;

public class BlockTraceable implements ITraceable<BlockTraceParam, BlockTraceResult> {
	
	private static final Vec3d   toCenter = new Vec3d(-0.5, 0, -0.5);
	private static final Vec3d fromCenter = new Vec3d(0.5, 0, 0.5);
	
	
	
	public TraceablePrimitive<?> shown;
	public TraceablePrimitive<?> hidden;
	public IProperty<Boolean> normalEnable;
	public IUnlistedProperty<Boolean> unlistedEnable;
	public Predicate<BlockTraceParam> predEnable;
	public EnumFacing side;
	public BlockPos offset;
	public boolean ignoreWrench;
	public boolean alwaysShow;
	
	public BlockTraceable(TraceablePrimitive<?> shown, TraceablePrimitive<?> hidden, IProperty<Boolean> normalEnable,
			IUnlistedProperty<Boolean> unlistedEnable, Predicate<BlockTraceParam> predEnable, EnumFacing side, BlockPos offset,
			boolean ignoreWrench, boolean alwaysShow) {
		super();
		this.shown = shown;
		this.hidden = hidden;
		this.normalEnable = normalEnable;
		this.unlistedEnable = unlistedEnable;
		this.predEnable = predEnable;
		this.side = side;
		this.offset = offset;
		this.ignoreWrench = ignoreWrench;
		this.alwaysShow = alwaysShow;
	}

	public BlockTraceable clone() {
		return new BlockTraceable(shown.clone(), hidden.clone(), normalEnable, unlistedEnable, predEnable, side, offset, ignoreWrench, alwaysShow);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public ITraceResult<BlockTraceResult> trace(Vec3d start, Vec3d end, BlockTraceParam param) {
		
		boolean prop = false;
		if(normalEnable != null)
			prop = param.state.getValue(normalEnable);
		if(unlistedEnable != null)
			prop = param.state.getValue(unlistedEnable);
		if(predEnable != null)
			prop = predEnable.test(param);
		prop = prop || alwaysShow;
		
		if(!prop && !( param.wrench || ignoreWrench )	)
			return (ITraceResult<BlockTraceResult>) RayTraceUtil.MISS_RESULT;
		
		ITraceResult<?> result = null;
		if(prop) {
			result = shown.trace(start, end);
		} else {
			result = hidden.trace(start, end);
		}
		
		return new SimpleRenderableTraceResult<BlockTraceResult>(result, new BlockTraceResult(offset == null ? BlockPos.ORIGIN : offset, side), ((IRenderableFace) result).getVertices());
	}

}
