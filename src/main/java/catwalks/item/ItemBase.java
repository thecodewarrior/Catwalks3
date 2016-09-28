package catwalks.item;

import catwalks.CatwalksMod;
import catwalks.register.ItemRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.text.MessageFormat;
import java.util.List;

public class ItemBase extends Item{
	public String name;
	
	public ItemBase(String name) {
		setUnlocalizedName(name);
		this.name = name;
		GameRegistry.registerItem(this, name);
		ItemRegister.renderRegsiterItems.add(this);
		setCreativeTab(CatwalksMod.tab);
	}
	
	public Object[] getInformationArguments(ItemStack stack, EntityPlayer player) {
		return new Object[0];
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int i = 0;
		String unloc = getUnlocalizedName() + ".info.";
		Object[] arguments = getInformationArguments(stack, playerIn);
		
		while(true) {
			
			if(!I18n.hasKey(unloc+i))
				break;
			
			String translated = I18n.format(unloc+i);
			if(translated.length() == 0)
				break;
			
			tooltip.add(MessageFormat.format(translated, arguments));
			i += 1;
		}
		
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	public String[] getCustomRenderVariants() { return null; }
}
