package catwalks.raytrace.node;

import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.RayTraceUtil.TraceablePrimitive;
import catwalks.raytrace.RayTraceUtil.SimpleTraceResult;
import catwalks.raytrace.primitives.TexCoords;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class NodeTraceable implements ITraceable<EntityPlayer, Integer> {

	int id;
	TraceablePrimitive<?> traceable;
	TexCoords uv;
	
	public NodeTraceable(int id, TraceablePrimitive<?> traceable, TexCoords uv) {
		this.id = id;
		this.traceable = traceable;
		this.uv = uv;
	}
	
	public int getId() {
		return id;
	}

	public TraceablePrimitive<?> getTraceable() {
		return traceable;
	}

	public TexCoords getUv() {
		return uv;
	}

	@Override
	public ITraceResult<Integer> trace(Vec3d start, Vec3d end, EntityPlayer param) {
		return new SimpleTraceResult<Integer>(traceable.trace(start, end), id);
	}

}
