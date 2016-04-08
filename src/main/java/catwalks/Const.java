package catwalks;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.property.UPropertyBool;
import catwalks.block.property.UPropertyEnum;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class Const {
	
	public static final UPropertyBool BOTTOM = new UPropertyBool("bottom");
	public static final UPropertyBool TOP    = new UPropertyBool("top");
	public static final UPropertyBool NORTH  = new UPropertyBool("north");
	public static final UPropertyBool SOUTH  = new UPropertyBool("south");
	public static final UPropertyBool EAST   = new UPropertyBool("east");
	public static final UPropertyBool WEST   = new UPropertyBool("west");
	
	public static final UPropertyBool EAST_TOP = new UPropertyBool("easttop");
	public static final UPropertyBool WEST_TOP = new UPropertyBool("westtop");
	
	public static final UPropertyBool TAPE   = new UPropertyBool("tape");
	public static final PropertyBool LIGHTS = PropertyBool.create("lights");
	public static final UPropertyBool SPEED  = new UPropertyBool("speed");
	
	public static final UPropertyEnum<EnumFacing> FACING = UPropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	public static final String MODID = CatwalksMod.MODID;
	
}
