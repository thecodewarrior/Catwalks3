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

    var ladderSpeed = 2f
    var catwalkSpeed = 1
    var showScaffoldInsideFaces = false

    var CUSTOM_ENABLED = BooleanArray(8)

    lateinit var file: File
    lateinit var config: Configuration

    val CATEGORY_GENERAL = "general"
    val CATEGORY_DEV = "developer options"

    fun loadConfigsFromFile(configFile: File) {
        file = configFile
        config = Configuration(configFile)
        config.load()

        loadConfigs(config)
    }

    fun loadConfigs(conf: Configuration) {
        var prop: Property

        prop = conf.get(CATEGORY_GENERAL, "Catwalk Speed Potion Level", 1).setRequiresMcRestart(false)
        prop.comment = "The speed boost on catwalks will apply a speed boost equivalent to Speed N"
        catwalkSpeed = prop.int

        prop = conf.get(CATEGORY_GENERAL, "Ladder Speed Multiplier", 1.5).setRequiresMcRestart(false)
        prop.comment = "Caged ladders will be N times as fast as normal ladders"
        ladderSpeed = prop.double.toFloat()

        prop = conf.get(CATEGORY_GENERAL, "Show Scaffold Inside Faces", false).setRequiresMcRestart(false)
        prop.comment = "Whether the faces of scaffolds should show if they are next to another scaffold"
        showScaffoldInsideFaces = prop.boolean

        prop = conf.get(CATEGORY_GENERAL, "Development mode", Const.developmentEnvironment).setRequiresMcRestart(false)
        prop.comment = "Enables development mode, some features require a restart"
        Const.developmentEnvironment = prop.boolean

        if (conf.hasChanged() == true) {
            conf.save()
            CatwalksMod.proxy.reloadConfigs()
        }
    }


}
