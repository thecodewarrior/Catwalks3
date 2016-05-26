package catwalks.gui.inventory;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SlotWrapper extends Slot {
	
	protected Slot s;
	
	public SlotWrapper(Slot s) {
		super(s.inventory, s.getSlotIndex(), s.xDisplayPosition, s.yDisplayPosition);
		this.slotNumber = s.slotNumber;
		this.s = s;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return s.canTakeStack(playerIn);
	}
	
	@Override
    public boolean isItemValid(ItemStack stack) {
        return s.isItemValid(stack);
    }
	
	@Override
	public boolean canBeHovered() {
		return s.canBeHovered();
	}
		
	@Override
	public ItemStack decrStackSize(int amount) {
		return s.decrStackSize(amount);
	}
	
	@Override
	public ResourceLocation getBackgroundLocation() {
		return s.getBackgroundLocation();
	}
	
	@Override
	public TextureAtlasSprite getBackgroundSprite() {
		return s.getBackgroundSprite();
	}
	
	@Override
	public boolean getHasStack() {
		return s.getHasStack();
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return s.getItemStackLimit(stack);
	}
	
	@Override
	public int getSlotIndex() {
		return s.getSlotIndex();
	}
	
	@Override
	public int getSlotStackLimit() {
		return s.getSlotStackLimit();
	}
	
	@Override
	public String getSlotTexture() {
		return s.getSlotTexture();
	}
	
	@Override
	public ItemStack getStack() {
		return s.getStack();
	}
	
	@Override
	public boolean isHere(IInventory inv, int slotIn) {
		return s.isHere(inv, slotIn);
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
		s.onPickupFromSlot(playerIn, stack);
	}
	
	@Override
	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
		s.onSlotChange(p_75220_1_, p_75220_2_);
	}
	
	@Override
	public void onSlotChanged() {
		s.onSlotChanged();
	}
	
	@Override
	public void putStack(ItemStack stack) {
		s.putStack(stack);
	}
	
	@Override
	public void setBackgroundLocation(ResourceLocation texture) {
		s.setBackgroundLocation(texture);
	}
	
	@Override
	public void setBackgroundName(String name) {
		s.setBackgroundName(name);
	}
}
