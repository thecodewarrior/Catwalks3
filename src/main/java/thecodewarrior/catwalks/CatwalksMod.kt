package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.utilities.LoggerBase
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import thecodewarrior.catwalks.proxy.CommonProxy

/**
 * TODO: Document file CatwalksMod
 *
 * Created by TheCodeWarrior
 */
@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION, name = CatwalksMod.MODNAME, dependencies = CatwalksMod.DEPENDENCIES, modLanguageAdapter = CatwalksMod.ADAPTER, acceptedMinecraftVersions = CatwalksMod.ALLOWED)
object CatwalksMod {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        Const
        PROXY.pre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.post(e)
    }

    const val MODID = "catwalks"
    const val MODNAME = "Catwalks"
    const val VERSION = "3.0.1"
    const val ALLOWED = "[1.12,)"
    const val CLIENT = "thecodewarrior.catwalks.proxy.ClientProxy"
    const val SERVER = "thecodewarrior.catwalks.proxy.CommonProxy"
    const val DEPENDENCIES = "required-after:forgelin;required-after:forge@[13.19.1.2195,)"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var PROXY: CommonProxy
}

object CatwalkLog : LoggerBase("Catwalks")
