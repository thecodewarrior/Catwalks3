package catwalks.item;

import catwalks.node.EntityNodeBase;
import catwalks.node.NodeUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ItemNodeManipulator extends ItemBase {

	public ItemNodeManipulator() {
		super("nodeManipulator");
		setMaxStackSize(1);
	}
	
//	@SubscribeEvent
//	public void onKeyInput(InputEvent.MouseInputEvent event) {
//	    if (Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
//	    	
//	    }
//	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(!( entityLiving instanceof EntityPlayer ))
			return super.onEntitySwing(entityLiving, stack);
		EntityPlayer playerIn = (EntityPlayer) entityLiving;
		
		ITraceResult<NodeHit> result = NodeUtil.rayTraceNodes(playerIn.worldObj, playerIn, RayTracer.getStartVec(playerIn), playerIn.getLook(1), 10);
		if(result == null || result.data() == null)
			return super.onEntitySwing(entityLiving, stack);
		
		result.data().node.onLeftClick(playerIn, result.data().hit);
		
		return super.onEntitySwing(entityLiving, stack);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(!playerIn.isSneaking()) {
			ITraceResult<NodeHit> result = NodeUtil.rayTraceNodes(worldIn, playerIn, RayTracer.getStartVec(playerIn), playerIn.getLook(1), 10);
			if(result == null || result.data() == null)
				return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
			
			result.data().node.onRightClick(playerIn, result.data().hit);
			worldIn.spawnParticle(EnumParticleTypes.CRIT_MAGIC, result.hitPoint().xCoord, result.hitPoint().yCoord, result.hitPoint().zCoord, 0, 0, 0, new int[0]);
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking() && !worldIn.isRemote) {
			EntityNodeBase entity = new EntityNodeBase(worldIn, pos.getX()+hitX, pos.getY()+hitY, pos.getZ()+hitZ);
			
			worldIn.spawnEntityInWorld(entity);
			
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
