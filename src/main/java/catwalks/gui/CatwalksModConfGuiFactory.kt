package catwalks.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class CatwalksModConfGuiFactory : IModGuiFactory {
    override fun initialize(minecraftInstance: Minecraft) {
    }

    override fun mainConfigGuiClass(): Class<out GuiScreen> {
        return CatwalksModConfGui::class.java
    }

    override fun runtimeGuiCategories(): Set<IModGuiFactory.RuntimeOptionCategoryElement>? {
        return null
    }

    override fun getHandlerFor(element: IModGuiFactory.RuntimeOptionCategoryElement): IModGuiFactory.RuntimeOptionGuiHandler? {
        return null
    }
}