package catwalks.node;

public class NodeBase {

	protected EntityNodeBase entity;
	
	public NodeBase(EntityNodeBase entity) {
		this.entity = entity;
	}
	
	public void serverTick() {}
	public void clientTick() {}
	public void onFirstTick() {}
	public void onLoad() {}
	
}
