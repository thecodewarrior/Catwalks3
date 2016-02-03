package catwalks.item.crafting;

import catwalks.item.ItemDecoration;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDecorationRepair implements IRecipe {

	/**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn) {
        Item item = null;
        boolean multiple = false;
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(itemstack.getItem() instanceof ItemDecoration) {
            		if(item == null) {
            			item = itemstack.getItem();
            		} else if(itemstack.getItem() != item) {
            			return false;
            		} else {
            			multiple = true;
            		}
            	} else {
            		return false;
            	}
            }
        }

        return multiple;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        
    	ItemStack stack = null;
    	
    	for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(itemstack.getItem() instanceof ItemDecoration) {
            		if(stack == null) {
            			stack = itemstack.copy();
            		} else if(itemstack.getItem() == stack.getItem()) {
            			int needed = stack.getItemDamage();
            			int available = itemstack.getMaxDamage() - itemstack.getItemDamage();
            			int toTake = needed > available ? available : needed;
            			stack.setItemDamage(stack.getItemDamage() - toTake);
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
            	if(itemstack.getItem() instanceof ItemDecoration) {
            		if(stack == null) {
            			stack = itemstack.copy();
            		} else if(itemstack.getItem() == stack.getItem()) {
            			int needed = stack.getItemDamage();
            			int available = itemstack.getMaxDamage() - itemstack.getItemDamage();
            			int toTake = needed > available ? available : needed;
            			stack.setItemDamage(stack.getItemDamage() - toTake);
            			if(needed < available) {
            				itemstack = itemstack.copy();
            				itemstack.setItemDamage(itemstack.getItemDamage()+needed);
            				aitemstack[i] = itemstack;
            			}
            		}
            	}
            }
        }

        return aitemstack;
    }

}
