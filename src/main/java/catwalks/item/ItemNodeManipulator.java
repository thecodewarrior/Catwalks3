package catwalks.item;

import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeClick;
import catwalks.network.messages.PacketNodeInteract;
import catwalks.node.EntityNodeBase;
import catwalks.node.NodeUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemNodeManipulator extends ItemBase {

	public ItemNodeManipulator() {
		super("nodeManipulator");
		setMaxStackSize(1);
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(entityLiving instanceof EntityPlayer && entityLiving.worldObj.isRemote) {			
			ITraceResult<NodeHit> result = NodeUtil.rayTraceNodes();
			if(result == null || result.data() == null)
				return super.onEntitySwing(entityLiving, stack);
			
			NetworkHandler.network.sendToServer(new PacketNodeClick(result.data().node.getEntityId(), result.data().hit));
		}
		return super.onEntitySwing(entityLiving, stack);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(worldIn.isRemote && !playerIn.isSneaking()) {
			ITraceResult<NodeHit> result = NodeUtil.rayTraceNodes();
			if(result == null || result.data() == null)
				return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
			
			NetworkHandler.network.sendToServer(new PacketNodeInteract(result.data().node.getEntityId(), result.data().hit));
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking() && !worldIn.isRemote) {
			EntityNodeBase entity = new EntityNodeBase(worldIn, pos.getX()+hitX, pos.getY()+hitY, pos.getZ()+hitZ);
			
			switch (facing) {
			case NORTH:
				entity.rotationYaw = 180;
				break;
			case SOUTH:
				break;
			case EAST:
				entity.rotationYaw = -90;
				break;
			case WEST:
				entity.rotationYaw = 90;
				break;
			case UP:
				entity.rotationPitch = -90;
				break;
			case DOWN:
				entity.rotationPitch = 90;
				break;

			default:
				break;
			}
			
			worldIn.spawnEntityInWorld(entity);
			
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
