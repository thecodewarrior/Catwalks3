package catwalks.block;

import net.minecraft.util.IStringSerializable;

public enum EnumCatwalkMaterial implements IStringSerializable {
	STEEL, RUSTY, WOOD, CUSTOM;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
}