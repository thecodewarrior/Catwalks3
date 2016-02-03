package catwalks.item.crafting;

import catwalks.item.ItemDecoration;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDecorationSplit implements IRecipe {

	/**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn) {
        Item item = null;
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(item == null) {
            		if(itemstack.getItem() instanceof ItemDecoration) {
            			item = itemstack.getItem();
            		} else {
            			return false;
            		}
            	} else {
            		return false;
            	}
            }
        }

        return item != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        
    	ItemStack stack = null;
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
            	if(stack == null) {
            		if(itemstack.getItem() instanceof ItemDecoration) {
            			stack = itemstack.copy();
            			int outputdamage = (stack.getMaxDamage()-stack.getItemDamage())/2;
            			stack.setItemDamage(stack.getMaxDamage()-outputdamage);
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
            	if(stack == null) {
            		if(itemstack.getItem() instanceof ItemDecoration) {
            			stack = itemstack.copy();
            			int outputdamage = (stack.getMaxDamage()-stack.getItemDamage())/2;
            			stack.setItemDamage(stack.getItemDamage()+outputdamage);
            			aitemstack[i] = stack;
            		}
            	} else {
            		aitemstack[i] = itemstack.copy(); // don't want other people to lose items because of glitches.
            	}
            }
        }


        return aitemstack;
    }

}
