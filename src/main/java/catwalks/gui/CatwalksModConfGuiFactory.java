package catwalks.gui;

import java.util.Set;

import net.minecraftforge.fml.client.IModGuiFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class CatwalksModConfGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return CatwalksModConfGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}
