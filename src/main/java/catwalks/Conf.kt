package catwalks

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import java.io.File
import java.util.concurrent.ThreadLocalRandom

object Conf {

    @SubscribeEvent
    fun onConfigChangedEvent(event: OnConfigChangedEvent) {
        if (Const.MODID == event.modID) {
            loadConfigs(config)
        }
    }

    var shouldHaveLaddeyGrabbey = ThreadLocalRandom.current().nextDouble() <= 5 / 100.0 // 5% chance

    var showScaffoldInsideFaces = false

    var ENABLED = arrayOf<String>()

    lateinit var file: File
    lateinit var config: Configuration

    val CATEGORY_GENERAL = "general"
    val CATEGORY_MODPACK = "modpack"
    val CATEGORY_DEV = "developer options"

    fun loadConfigsFromFile(configFile: File) {
        file = configFile
        config = Configuration(configFile)
        config.load()

        loadConfigs(config)
    }

    fun loadConfigs(conf: Configuration) {
        var prop: Property

        prop = conf.get(CATEGORY_GENERAL, "Show Scaffold Inside Faces", false).setRequiresMcRestart(false)
        prop.comment = "Whether the faces of scaffolds should show if they are next to another scaffold"
        showScaffoldInsideFaces = prop.boolean

        prop = conf.get(CATEGORY_DEV, "Development mode", Const.developmentEnvironment).setRequiresMcRestart(false)
        prop.comment = "Enables development mode, some features require a restart"
        Const.developmentEnvironment = prop.boolean

        prop = conf.get(CATEGORY_MODPACK, "Materials force enabled", ENABLED).setRequiresMcRestart(true)
        prop.comment = "Sets which of the 8 custom materials are enabled"
        ENABLED = prop.stringList

        if (conf.hasChanged() == true) {
            conf.save()
            CatwalksMod.proxy.reloadConfigs()
        }
    }


}
