package catwalks.item;

import java.util.List;

import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import catwalks.CatwalksMod;
import catwalks.register.ItemRegister;

public class ItemBase extends Item{
	public String name;
	
	public ItemBase(String name) {
		setUnlocalizedName(name);
		this.name = name;
		GameRegistry.registerItem(this, name);
		ItemRegister.renderRegsiterItems.add(this);
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
	
	public String[] getCustomRenderVariants() { return null; }
}
