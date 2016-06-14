package catwalks.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDecorationRepair implements IRecipe {

	Item targetItem;
	
	public Item getTargetItem() {
		return targetItem;
	}
	
	public RecipeDecorationRepair(Item item) {
		targetItem = item;
	}
	
	/**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int count = 0;
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(itemstack.getItem() != targetItem)
            		return false;
            	count++;
            }
        }
        
        return count > 1;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        
    	ItemStack stack = null;
    	
    	for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(itemstack.getItem() == targetItem) {
            		if(stack == null) {
            			stack = itemstack.copy();
            		} else {
            			int usesNeeded = stack.getItemDamage();
            			int usesAvailable = itemstack.getMaxDamage() - itemstack.getItemDamage();
            			int usesToTake = usesNeeded > usesAvailable ? usesAvailable : usesNeeded;
            			stack.setItemDamage(stack.getItemDamage() - usesToTake);
            		}
            	}
            }
        }

        return stack;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return 4;
    }

    public ItemStack getRecipeOutput() {
        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        ItemStack stack = null;
    	
    	for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(itemstack.getItem() == targetItem) {
            		if(stack == null) {
            			stack = itemstack.copy();
            		} else {
            			int usesNeeded = stack.getItemDamage();
            			int usesAvailable = itemstack.getMaxDamage() - itemstack.getItemDamage();
            			int usesToTake = usesNeeded > usesAvailable ? usesAvailable : usesNeeded;
            			
            			stack.setItemDamage(stack.getItemDamage() - usesToTake);
            			
            			if(usesNeeded < usesAvailable) {
            				itemstack = itemstack.copy();
            				itemstack.setItemDamage(itemstack.getItemDamage()+usesNeeded);
            				aitemstack[i] = itemstack;
            			}
            		}
            	}
            }
        }

        return aitemstack;
    }

}
