package thecodewarrior.catwalks.proxy

import net.minecraft.block.Block
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thecodewarrior.catwalks.BlockCatwalk

/**
 * TODO: Document file CommonProxy
 *
 * Created by TheCodeWarrior
 */
open class CommonProxy {
    init { MinecraftForge.EVENT_BUS.register(this) }

    @SubscribeEvent
    fun registerBlocks(e: RegistryEvent.Register<Block>) {
        val r = e.registry

        r.register(BlockCatwalk())
    }
    open fun pre(e: FMLPreInitializationEvent) {}
    open fun init(e: FMLInitializationEvent) {}
    open fun post(e: FMLPostInitializationEvent) {}
}
