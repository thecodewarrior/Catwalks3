package catwalks.node;

import net.minecraft.util.math.Vec3d;

public class NodeRayTraceResult {

	public int hit;
	public Vec3d hitVec;
	public EntityNodeBase node;
	
	public NodeRayTraceResult(EntityNodeBase node, int hit, Vec3d hitVec) {
		this.node = node;
		this.hit = hit;
		this.hitVec = hitVec;
	}
	
}
