package catwalks.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;

import catwalks.CatwalksMod;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeConnect;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public class ItemNodeConnector extends ItemNodeBase {

	public ItemNodeConnector() {
		super("nodeConnector");
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		CatwalksMod.proxy.setSelectedNode(hit.data().node);
		if(hit.data().node.getNode().outputs().size() != 0)
			CatwalksMod.proxy.setConnectingIndex(0);
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if( CatwalksMod.proxy.getSelectedNode() != null &&
			CatwalksMod.proxy.getSelectedNode() != hit.data().node &&
			CatwalksMod.proxy.getConnectingIndex() >= 0) {
			
			NetworkHandler.network.sendToServer(new PacketNodeConnect(
					CatwalksMod.proxy.getSelectedNode().getEntityId(), CatwalksMod.proxy.getConnectingIndex(),
					hit.data().node.getEntityId(), 0));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	

}
