package catwalks.item;

import catwalks.CatwalksMod;
import catwalks.register.ItemRegister;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public abstract class ItemMultiPartBase extends ItemMultiPart {
	public String name;
	
	public ItemMultiPartBase(String name) {
		setUnlocalizedName(name);
		this.name = name;
		this.setRegistryName(name);
		GameRegistry.register(this);
//		ItemRegister.renderRegsiterItems.add(this);
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
