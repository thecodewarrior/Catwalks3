package catwalks.proxy;

import catwalks.item.ItemDecoration;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;

public class CommonProxy {
	public void preInit() {}
	
	public void reloadConfigs() {}
	
	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
	
	public EntityPlayer getPlayerLooking(Vec3d start, Vec3d end) {
		EntityPlayer player = null;
		List<EntityPlayerMP> players = FMLServerHandler.instance().getServer().getPlayerList().getPlayerList();
		float fudge = 0.1f;
		
		for (final EntityPlayerMP p : players) { // for each player
			Vec3d lookStart = RayTracer.getStartVec(p);
			Vec3d lookEnd   = RayTracer.getEndVec(p);
			double lookDistance = RayTracer.getBlockReachDistance(p);
			
			double dStart  = lookStart.distanceTo(start);
			double dEnd    = lookEnd  .distanceTo(start);
			
			double dStart_ = lookStart.distanceTo(end);
			double dEnd_   = lookEnd  .distanceTo(end);
			
			double dSumStart = dStart + dEnd;
			double dSumEnd = dStart_ + dEnd_;
			
			if(dSumStart <= lookDistance+fudge && dSumStart >= lookDistance-fudge &&
				dSumEnd <= lookDistance+fudge && dSumEnd >= lookDistance-fudge) {
				player = p; break;
			}
		}
		return player;
	}
	
	@SubscribeEvent
	public void itemPickup(EntityItemPickupEvent event) {
		ItemStack stack = event.getItem().getEntityItem();
		if(event.getEntityPlayer() == null) {
			return;
		}
		InventoryPlayer inv = event.getEntityPlayer().inventory;
		boolean changed = false;

		if(stack != null && stack.getItem() instanceof ItemDecoration) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack slotStack = inv.getStackInSlot(i);
				if(slotStack != null && slotStack.getItem() == stack.getItem() && ( slotStack.getItemDamage() != 0 || event.getEntityPlayer().capabilities.isCreativeMode)) {
					int toTake = slotStack.getItemDamage();
					int available = stack.getMaxDamage()-stack.getItemDamage();
					if(event.getEntityPlayer().capabilities.isCreativeMode)
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
				event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
			}
		}
		
	}
}
