package catwalks.proxy

import catwalks.item.ItemDecoration
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.eventhandler.Event.Result
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.server.FMLServerHandler

open class CommonProxy {
    open fun preInit() {
    }

    open fun reloadConfigs() {
    }

    open val server: MinecraftServer
        get() = FMLServerHandler.instance().server

    @SubscribeEvent
    fun itemPickup(event: EntityItemPickupEvent) {
        val stack = event.item.entityItem
        if (event.entityPlayer == null) {
            return
        }
        val inv = event.entityPlayer.inventory
        var changed = false

        if (stack != null && stack.item is ItemDecoration) {
            for (i in 0..inv.sizeInventory - 1) {
                val slotStack = inv.getStackInSlot(i)
                if (slotStack != null && slotStack.item === stack.item && (slotStack.itemDamage != 0 || event.entityPlayer.capabilities.isCreativeMode)) {
                    var toTake = slotStack.itemDamage
                    val available = stack.maxDamage - stack.itemDamage
                    if (event.entityPlayer.capabilities.isCreativeMode)
                        toTake = available
                    val toput = Math.min(toTake, available)

                    slotStack.itemDamage = slotStack.itemDamage - toput
                    stack.itemDamage = stack.itemDamage + toput
                    changed = true
                }
            }
            if (stack.itemDamage == stack.maxDamage)
                stack.stackSize--

            if (changed) {
                event.result = Result.ALLOW
                event.entityPlayer.inventoryContainer.detectAndSendChanges()
            }
        }

    }
}
