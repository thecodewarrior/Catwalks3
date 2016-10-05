package catwalks;

import catwalks.block.EnumDecoration;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public enum EnumCatwalkMaterial implements IStringSerializable {
	CUSTOM(BlockRenderLayer.CUTOUT),
	STEEL(BlockRenderLayer.CUTOUT),
	IESTEEL(BlockRenderLayer.CUTOUT),
	WOOD(BlockRenderLayer.CUTOUT);
	
	public final BlockRenderLayer LAYER;
	public final List<EnumDecoration> DECORATIONS;
	
	EnumCatwalkMaterial(BlockRenderLayer layer, EnumDecoration... decorations) {
		LAYER = layer;
		DECORATIONS = Arrays.asList(decorations);
	}
	
	public int getID(EnumDecoration decor) {
		return DECORATIONS.indexOf(decor);
	}
	
	public EnumDecoration getDecor(int id) {
		return DECORATIONS.get(id);
	}
	
	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
}
