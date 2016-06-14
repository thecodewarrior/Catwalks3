package catwalks.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDecorationSplit implements IRecipe {

	Item targetItem;
	
	public Item getTargetItem() {
		return targetItem;
	}
	
	public RecipeDecorationSplit(Item item) {
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

        return count == 1;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null) {
        		if(itemstack.getItem() == targetItem) {
        			ItemStack stack = itemstack.copy();
        			int uses = stack.getMaxDamage() - stack.getItemDamage();
        			uses = uses/2;
        			
        			stack.setItemDamage(stack.getMaxDamage() - uses);
        			return stack;
        		}
            }
        }

        return null;
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
            		if(itemstack.getItem() == targetItem) {
            			stack = itemstack.copy();
            			int uses = stack.getMaxDamage() - stack.getItemDamage();
            			
            			int usesleft = uses - (uses/2);
            			
            			stack.setItemDamage(stack.getMaxDamage()-usesleft);
            			aitemstack[i] = stack;
            		}
            	} else {
            		aitemstack[i] = itemstack.copy(); // don't want other people to lose items because of glitches. any excess items get kept in the grid
            	}
            }
        }


        return aitemstack;
    }

}
