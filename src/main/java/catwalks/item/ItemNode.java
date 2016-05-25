package catwalks.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import catwalks.node.NodeUtil.EnumNodes;

public class ItemNode extends ItemBase {

	public ItemNode() {
		super("node");
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (EnumNodes type : EnumNodes.values()) {
			subItems.add(new ItemStack(this, 1, type.ordinal()));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = "InvalidID";
		if(stack.getItemDamage() < EnumNodes.values().length)
			name = EnumNodes.values()[stack.getItemDamage()].toString();
		return super.getUnlocalizedName(stack) + "." + name;
	}
	
	public String[] getCustomRenderVariants() {
		String[] strings = new String[EnumNodes.values().length];
		
		for (int i = 0; i < strings.length; i++) {
			strings[i] = EnumNodes.values()[i].toString();
		}
		
		return strings;
	}
}
