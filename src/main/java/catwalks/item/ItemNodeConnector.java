package catwalks.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import catwalks.CatwalksMod;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeClick;
import catwalks.network.messages.PacketNodeConnect;
import catwalks.network.messages.PacketNodeInteract;
import catwalks.node.NodeUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public class ItemNodeConnector extends ItemNodeBase {

	public ItemNodeConnector() {
		super("nodeConnector");
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		CatwalksMod.proxy.setSelectedNode(hit.data().node);
		CatwalksMod.proxy.setConnectingIndex(0);
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if(CatwalksMod.proxy.getSelectedNode() != hit.data().node && CatwalksMod.proxy.getConnectingIndex() >= 0) {
			NetworkHandler.network.sendToServer(new PacketNodeConnect(
					CatwalksMod.proxy.getSelectedNode().getEntityId(), CatwalksMod.proxy.getConnectingIndex(),
					hit.data().node.getEntityId(), 0));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	

}
