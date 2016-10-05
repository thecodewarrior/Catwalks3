package catwalks.block;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;

import java.util.*;

public enum EnumCatwalkMaterialOld implements IStringSerializable {
	CUSTOM,
	STEEL,
	IESTEEL,
	WOOD;
	
	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
}