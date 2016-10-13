package catwalks.item

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class EntityItemDecoration : EntityItem {

    constructor(worldIn: World?, x: Double, y: Double, z: Double, stack: ItemStack?) : super(worldIn, x, y, z, stack) {
        // TODO Auto-generated constructor stub
    }

    constructor(worldIn: World?, x: Double, y: Double, z: Double) : super(worldIn, x, y, z) {
        // TODO Auto-generated constructor stub
    }

    constructor(worldIn: World?) : super(worldIn) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Called to update the entity's position/logic.
     */
    override fun onUpdate() {
        super.onUpdate()

        val flag = this.prevPosX.toInt() != this.posX.toInt() || this.prevPosY.toInt() != this.posY.toInt() || this.prevPosZ.toInt() != this.posZ.toInt()

        if (flag || this.ticksExisted % 25 == 0) {
            if (!this.worldObj.isRemote) {
                this.combineDecorationsNearby()
            }
        }
    }

    fun combineDecorationsNearby() {
        for (entityitem in this.worldObj.getEntitiesWithinAABB<EntityItem>(EntityItem::class.java!!, this.entityBoundingBox.expand(0.5, 0.0, 0.5))) {
            this.combineDecorations(entityitem)
        }
    }

    fun combineDecorations(other: EntityItem): Boolean {
        if (other === this) {
            return false
        } else if (other.isEntityAlive && this.isEntityAlive) {
            val itemstack = this.entityItem
            val itemstack1 = other.entityItem

            if (itemstack1.item !== itemstack.item) {
                return false
            } else if (itemstack1.itemDamage - (itemstack.maxDamage - itemstack.itemDamage) < 0) {
                return false
            } else {
                itemstack1.itemDamage = itemstack1.itemDamage - (itemstack.maxDamage - itemstack.itemDamage)
                other.setEntityItemStack(itemstack1)
                this.setDead()
                return true
            }
        } else {
            return false
        }
    }

}
