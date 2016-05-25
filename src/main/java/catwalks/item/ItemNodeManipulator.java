package catwalks.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeRelocate;
import catwalks.proxy.ClientProxy;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public class ItemNodeManipulator extends ItemNodeBase {

	public ItemNodeManipulator() {
		super("nodeManipulator");
		setMaxStackSize(1);
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		ClientProxy.setSelectedNode(hit.data().node);
		ClientProxy.isRelocating = true;
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		return EnumActionResult.PASS;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking() && ClientProxy.isRelocating && ClientProxy.getSelectedNode() != null) {
			NetworkHandler.network.sendToServer(new PacketNodeRelocate(ClientProxy.getSelectedNode().getEntityId(), new Vec3d(pos.getX()+hitX, pos.getY()+hitY, pos.getZ()+hitZ)));
			ClientProxy.isRelocating = false;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
