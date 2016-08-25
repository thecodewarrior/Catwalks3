package catwalks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import net.minecraft.client.gui.GuiScreen;

import catwalks.Conf;
import catwalks.Const;

public class CatwalksModConfGui extends GuiConfig
{
    public CatwalksModConfGui(GuiScreen parent)
    {
        super(parent, getConfigElements(), Const.MODID, false, false, getTitle(parent));
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> configElements = new ArrayList<IConfigElement>();

        configElements.addAll(new ConfigElement(Conf.config.getCategory(Conf.CATEGORY_GENERAL)).getChildElements());
        configElements.add(new ConfigElement(Conf.config.getCategory(Conf.CATEGORY_DEV)));
        return configElements;
    }

    private static String getTitle(GuiScreen parent)
    {
        return "Catwalks 3";
    }
}