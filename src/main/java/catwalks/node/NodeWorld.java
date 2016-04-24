package catwalks.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import catwalks.util.Vec2i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NodeWorld {
	public World world;
	
	protected Map<UUID, Node> nodeMap = new HashMap<>();
	protected Set<Node> nodeList = new HashSet<>();
	protected Map<Vec2i, List<Node>> chunks = new HashMap<>();
	
	public Node getByUUID(UUID uuid) {
		return nodeMap.get(uuid);
	}
	
//	public List<Node> getInAABB(AxisAlignedBB aabb) {
//		
//	}
	
	public void removeChunk(Vec2i chunk) {
		if(!chunks.containsKey(chunk))
			return;
		List<Node> nodes = chunks.get(chunk);
		
		Collection<Node> mapIter = nodeMap.values();
		
		for (Node node : nodes) {
			mapIter.remove(node);
			nodeList.remove(node);
		}
	}
	
	public Node createFromNBT(NBTTagCompound tag) {
		byte id = tag.getByte("ID");
		
		switch(id) {
		
		}
	}
}
