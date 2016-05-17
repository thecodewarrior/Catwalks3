package catwalks.raytrace.node;

import catwalks.node.EntityNodeBase;

public class NodeHit {

	public int hit;
	public EntityNodeBase node;
	
	public NodeHit(EntityNodeBase node, int hit) {
		this.node = node;
		this.hit = hit;
	}
	
}
