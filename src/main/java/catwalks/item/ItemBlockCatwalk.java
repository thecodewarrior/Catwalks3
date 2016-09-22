package catwalks.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import catwalks.block.EnumCatwalkMaterial;

public class ItemBlockCatwalk extends ItemBlock {
	public ItemBlockCatwalk(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	return super.getUnlocalizedName(stack) + "." + EnumCatwalkMaterial.values()[stack.getItemDamage()].getName().toLowerCase();
    }
    
}
