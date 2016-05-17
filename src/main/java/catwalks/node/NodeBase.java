package catwalks.node;

public class NodeBase {

	protected EntityNodeBase entity;
	
	public NodeBase(EntityNodeBase entity) {
		this.entity = entity;
	}
	
	public void onTick() {}
	public void onFirstTick() {}
	public void onLoad() {}
	
}
