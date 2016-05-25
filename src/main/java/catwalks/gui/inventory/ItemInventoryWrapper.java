package catwalks.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemInventoryWrapper implements IInventory {

	private static final String KEY_INV = "inventory";
	private static final String KEY_SLOT = "slot";
	
	protected final IInventoryContainerItem inventoryItem;
	protected final ItemStack stack;
	protected NBTTagCompound tag;
	protected ItemStack[] inventory;
	protected boolean dirty = false;
	
	public ItemInventoryWrapper(ItemStack itemstack) {
		stack = itemstack;
		inventoryItem = (IInventoryContainerItem) stack.getItem();
		inventory = new ItemStack[getSizeInventory()];

		loadInventory();
		markDirty();
	}
	
	@Override
	public String getName() {
		return stack.hasDisplayName() ? stack.getDisplayName() : inventoryItem.getGuiUnlocalizedName();
	}

	@Override
	public boolean hasCustomName() {
		return stack.hasDisplayName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}

	@Override
	public int getSizeInventory() {
		return inventoryItem.getInventorySize();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack s = inventory[index];
		if (s == null) {
			return null;
		}
		ItemStack r = s.splitStack(count);
		if (s.stackSize <= 0) {
			inventory[index] = null;
			r.stackSize += s.stackSize;
		}
		return r;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inventory[index];
		inventory[index] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventoryItem.getStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
	{ /* implementation stuff */ }
	
	@Override
	public void markDirty() {
		
		saveStacks();
		this.dirty = true;
	}
	
	{ /* saving/loading */ }
	
	protected void loadInventory() {
		if(stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if(!stack.getTagCompound().hasKey(KEY_INV)) {
			stack.getTagCompound().setTag(KEY_INV, new NBTTagCompound());
		}
		tag = stack.getTagCompound().getCompoundTag(KEY_INV);

		loadStacks();
	}
	
	protected void loadStacks() {

		for (int i = 0; i < inventory.length; i++) {
			if(tag.hasKey(KEY_SLOT + i)) {
				inventory[i] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(KEY_SLOT + i));
			} else {
				inventory[i] = null;
			}
		}
	}

	protected void saveStacks() {

		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] == null) {
				tag.removeTag(KEY_SLOT + i);
			} else {
				tag.setTag(KEY_SLOT + i, inventory[i].writeToNBT(new NBTTagCompound()));
			}
		}
		stack.setTagInfo(KEY_INV, tag);
	}

}
