package catwalks.item;

import catwalks.CatwalksMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item{
	public String name;
	
	public ItemBase(String name) {
		setUnlocalizedName(name);
		this.name = name;
		GameRegistry.registerItem(this, name);
		setCreativeTab(CatwalksMod.tab);
	}
}
