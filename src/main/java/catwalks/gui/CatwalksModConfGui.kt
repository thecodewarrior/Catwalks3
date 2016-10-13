package catwalks.gui

import catwalks.Conf
import catwalks.Const
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.fml.client.config.GuiConfig
import net.minecraftforge.fml.client.config.IConfigElement
import java.util.*

class CatwalksModConfGui(parent: GuiScreen) : GuiConfig(parent, configElements, Const.MODID, false, false, getTitle(parent)) {

    companion object {
        private val configElements: List<IConfigElement>
            get() {
                val configElements = ArrayList<IConfigElement>()

                configElements.addAll(ConfigElement(Conf.config.getCategory(Conf.CATEGORY_GENERAL)).childElements)
                configElements.add(ConfigElement(Conf.config.getCategory(Conf.CATEGORY_DEV)))
                return configElements
            }

        private fun getTitle(parent: GuiScreen): String {
            return "Catwalks 3"
        }
    }


}
