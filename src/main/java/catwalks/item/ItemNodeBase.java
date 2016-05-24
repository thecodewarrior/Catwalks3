package catwalks.item;

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
import catwalks.network.messages.PacketNodeInteract;
import catwalks.node.NodeUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public abstract class ItemNodeBase extends ItemBase {

	public ItemNodeBase(String name) {
		super(name);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(entityLiving instanceof EntityPlayer && entityLiving.worldObj.isRemote) {			
			ITraceResult<NodeHit> result = NodeUtil.nodeHit;
			if(result == null || result.data() == null)
				return super.onEntitySwing(entityLiving, stack);
			
			boolean ret = leftClickNodeClient(result, stack, (EntityPlayer)entityLiving);
			if(!ret) {
				NetworkHandler.network.sendToServer(new PacketNodeClick(result.data().node.getEntityId(), result.data().hit));
				if(CatwalksMod.proxy.getSelectedNode() != result.data().node) {
					CatwalksMod.proxy.setSelectedNode(result.data().node);
					CatwalksMod.proxy.setConnectingIndex(-1);
				}
			}
			return ret;
		}
		return super.onEntitySwing(entityLiving, stack);
	}
	
	public abstract boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player);
	public abstract EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player);
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(worldIn.isRemote) {
			ITraceResult<NodeHit> result = NodeUtil.nodeHit;
			if(result == null || result.data() == null)
				return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
			
			NetworkHandler.network.sendToServer(new PacketNodeInteract(result.data().node.getEntityId(), result.data().hit, result.data().data));
			
			ItemStack passStack = itemStackIn.copy();
			EnumActionResult res = rightClickNodeClient(result, passStack, playerIn);
			return new ActionResult<ItemStack>(res, passStack);
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}
	
}
