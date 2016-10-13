package catwalks.item.crafting

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

class RecipeDecorationRepair(item: Item) : IRecipe {

    var targetItem: Item
        internal set

    init {
        targetItem = item
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    override fun matches(inv: InventoryCrafting, worldIn: World): Boolean {
        var count = 0

        for (i in 0..inv.sizeInventory - 1) {
            val itemstack = inv.getStackInSlot(i)

            if (itemstack != null) {
                if (itemstack.item !== targetItem)
                    return false
                count++
            }
        }

        return count > 1
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {

        var stack: ItemStack? = null

        for (i in 0..inv.sizeInventory - 1) {
            val itemstack = inv.getStackInSlot(i)

            if (itemstack != null) {
                if (itemstack.item === targetItem) {
                    if (stack == null) {
                        stack = itemstack.copy()
                    } else {
                        val usesNeeded = stack.itemDamage
                        val usesAvailable = itemstack.maxDamage - itemstack.itemDamage
                        val usesToTake = if (usesNeeded > usesAvailable) usesAvailable else usesNeeded
                        stack.itemDamage = stack.itemDamage - usesToTake
                    }
                }
            }
        }

        return stack
    }

    /**
     * Returns the size of the recipe area
     */
    override fun getRecipeSize(): Int {
        return 4
    }

    override fun getRecipeOutput(): ItemStack? {
        return null
    }

    override fun getRemainingItems(inv: InventoryCrafting): Array<ItemStack?> {
        val aitemstack = arrayOfNulls<ItemStack>(inv.sizeInventory)

        var stack: ItemStack? = null

        for (i in 0..inv.sizeInventory - 1) {
            var itemstack = inv.getStackInSlot(i)

            if (itemstack != null) {
                if (itemstack.item === targetItem) {
                    if (stack == null) {
                        stack = itemstack.copy()
                    } else {
                        val usesNeeded = stack.itemDamage
                        val usesAvailable = itemstack.maxDamage - itemstack.itemDamage
                        val usesToTake = if (usesNeeded > usesAvailable) usesAvailable else usesNeeded

                        stack.itemDamage = stack.itemDamage - usesToTake

                        if (usesNeeded < usesAvailable) {
                            itemstack = itemstack.copy()
                            itemstack!!.itemDamage = itemstack.itemDamage + usesNeeded
                            aitemstack[i] = itemstack
                        }
                    }
                }
            }
        }

        return aitemstack
    }

}
