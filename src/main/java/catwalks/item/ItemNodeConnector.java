package catwalks.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;

import catwalks.CatwalksMod;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeConnect;
import catwalks.proxy.ClientProxy;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public class ItemNodeConnector extends ItemNodeBase {

	public ItemNodeConnector() {
		super("nodeConnector");
		setMaxStackSize(1);
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		ClientProxy.setSelectedNode(hit.data().node);
		if(hit.data().node.getNode().outputs().size() != 0)
			ClientProxy.connectingIndex = 0;
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if( ClientProxy.getSelectedNode() != null &&
			ClientProxy.getSelectedNode() != hit.data().node &&
			ClientProxy.connectingIndex >= 0) {
			
			NetworkHandler.network.sendToServer(new PacketNodeConnect(
					ClientProxy.getSelectedNode().getEntityId(), ClientProxy.connectingIndex,
					hit.data().node.getEntityId(), 0));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	

}
