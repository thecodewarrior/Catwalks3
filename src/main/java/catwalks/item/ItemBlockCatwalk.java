package catwalks.item;

import catwalks.block.EnumCatwalkMaterial;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
