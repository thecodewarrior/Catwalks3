package catwalks.raytrace.node;

import catwalks.node.EntityNodeBase;

public class NodeHit {

	public int hit, data;
	public EntityNodeBase node;

	public NodeHit(EntityNodeBase node, int hit) {
		this(node, hit, 0);
	}
	
	public NodeHit(EntityNodeBase node, int hit, int data) {
		this.node = node;
		this.hit = hit;
		this.data = data;
	}
	
}
