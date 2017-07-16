package thecodewarrior.catwalks.proxy

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * TODO: Document file CommonProxy
 *
 * Created by TheCodeWarrior
 */
open class CommonProxy {
    init { MinecraftForge.EVENT_BUS.register(this) }

    open fun pre(e: FMLPreInitializationEvent) {}
    open fun init(e: FMLInitializationEvent) {}
    open fun post(e: FMLPostInitializationEvent) {}
}
