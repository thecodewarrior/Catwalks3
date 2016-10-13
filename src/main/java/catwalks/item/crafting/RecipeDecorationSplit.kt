package catwalks.item.crafting

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

class RecipeDecorationSplit(item: Item) : IRecipe {

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

        return count == 1
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {


        for (i in 0..inv.sizeInventory - 1) {
            val itemstack = inv.getStackInSlot(i)

            if (itemstack != null) {
                if (itemstack.item === targetItem) {
                    val stack = itemstack.copy()
                    var uses = stack.maxDamage - stack.itemDamage
                    uses = uses / 2

                    stack.itemDamage = stack.maxDamage - uses
                    return stack
                }
            }
        }

        return null
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
            val itemstack = inv.getStackInSlot(i)

            if (itemstack != null) {
                if (stack == null) {
                    if (itemstack.item === targetItem) {
                        stack = itemstack.copy()
                        val uses = stack!!.maxDamage - stack.itemDamage

                        val usesleft = uses - uses / 2

                        stack.itemDamage = stack.maxDamage - usesleft
                        aitemstack[i] = stack
                    }
                } else {
                    aitemstack[i] = itemstack.copy() // don't want other people to lose items because of glitches. any excess items get kept in the grid
                }
            }
        }


        return aitemstack
    }

}
