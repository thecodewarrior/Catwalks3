package catwalks.proxy;

import java.util.List;

import catwalks.item.ItemDecoration;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {
	public void preInit() {}
	public void reloadConfigs() {}
	
	public EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		EntityPlayer player = null;
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		for (final EntityPlayerMP p : players) { // for each player
			Vec3 lookStart = RayTracer.getStartVec(p);
			Vec3 lookEnd   = RayTracer.getEndVec(p);
			double lookDistance = RayTracer.getBlockReachDistance(p);
			
			double dStart  = lookStart.distanceTo(start);
			double dEnd    = lookEnd  .distanceTo(start);
			
			double dStart_ = lookStart.distanceTo(end);
			double dEnd_   = lookEnd  .distanceTo(end);
			
			
			if(dStart + dEnd == lookDistance && dStart_ + dEnd_ == lookDistance) {
				player = p; break;
			}
		}
		return player;
	}
	
	@SubscribeEvent
	public void itemPickup(EntityItemPickupEvent event) {
		ItemStack stack = event.item.getEntityItem();
		if(event.entityPlayer == null) {
			return;
		}
		InventoryPlayer inv = event.entityPlayer.inventory;
		boolean changed = false;

		if(stack != null && stack.getItem() instanceof ItemDecoration) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack slotStack = inv.getStackInSlot(i);
				if(slotStack != null && slotStack.getItem() == stack.getItem() && ( slotStack.getItemDamage() != 0 || event.entityPlayer.capabilities.isCreativeMode)) {
					int toTake = slotStack.getItemDamage();
					int available = stack.getMaxDamage()-stack.getItemDamage();
					if(event.entityPlayer.capabilities.isCreativeMode)
						toTake = available;
					int toput = Math.min(toTake, available);
					
					slotStack.setItemDamage(slotStack.getItemDamage()-toput);
					stack.setItemDamage(stack.getItemDamage()+toput);
					changed = true;
				}
			}
			if(stack.getItemDamage() == stack.getMaxDamage())
				stack.stackSize--;
			
			if(changed) {
				event.setResult(Result.ALLOW);
				event.entityPlayer.inventoryContainer.detectAndSendChanges();
			}
		}
		
	}
}
