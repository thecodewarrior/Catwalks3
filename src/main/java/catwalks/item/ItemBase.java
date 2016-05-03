package catwalks.item;

import java.util.List;

import catwalks.CatwalksMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemBase extends Item{
	public String name;
	
	public ItemBase(String name) {
		setUnlocalizedName(name);
		this.name = name;
		GameRegistry.registerItem(this, name);
		setCreativeTab(CatwalksMod.tab);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int i = 0;
		String unloc = getUnlocalizedName() + ".info.";
		
		while(true) {
			
			if(!I18n.hasKey(unloc+i))
				break;
			
			tooltip.add(I18n.format(unloc+i));
			i += 1;
		}
		
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
}
