package catwalks.proxy;

import catwalks.item.ItemDecoration;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

public class CommonProxy {
	public void preInit() {}
	
	public void reloadConfigs() {}
	
	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
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
