package catwalks;

import java.util.EnumMap;
import java.util.Map;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.property.UPropertyBool;
import catwalks.block.property.UPropertyEnum;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Const {
	
	public static final UPropertyBool CONST_TRUE = new UPropertyBool("dummyConstTrue");
	public static final UPropertyBool CONST_FALSE = new UPropertyBool("dummyConstFalse");
	
	public static final UPropertyBool IS_TOP = new UPropertyBool("istop");
	
	public static final UPropertyBool BOTTOM = new UPropertyBool("bottom");
	public static final UPropertyBool TOP    = new UPropertyBool("top");
	public static final UPropertyBool NORTH  = new UPropertyBool("north");
	public static final UPropertyBool SOUTH  = new UPropertyBool("south");
	public static final UPropertyBool EAST   = new UPropertyBool("east");
	public static final UPropertyBool WEST   = new UPropertyBool("west");
	
	public static final Map<EnumFacing, UPropertyBool> sideProperties = new EnumMap<EnumFacing, UPropertyBool>(EnumFacing.class);
	
	public static final UPropertyBool EAST_TOP = new UPropertyBool("easttop");
	public static final UPropertyBool WEST_TOP = new UPropertyBool("westtop");
	
	public static final UPropertyBool TAPE   = new UPropertyBool("tape");
	public static final PropertyBool LIGHTS = PropertyBool.create("lights");
	public static final UPropertyBool SPEED  = new UPropertyBool("speed");
	
	public static final UPropertyBool NORTH_LADDER_EXT  = new UPropertyBool("north_ladder_ext");
	public static final UPropertyBool SOUTH_LADDER_EXT  = new UPropertyBool("south_ladder_ext");
	public static final UPropertyBool EAST_LADDER_EXT   = new UPropertyBool("east_ladder_ext");
	public static final UPropertyBool WEST_LADDER_EXT   = new UPropertyBool("west_ladder_ext");
	
	public static final UPropertyBool NORTH_LADDER_EXT_TOP  = new UPropertyBool("north_ladder_ext_top");
	public static final UPropertyBool SOUTH_LADDER_EXT_TOP  = new UPropertyBool("south_ladder_ext_top");
	public static final UPropertyBool EAST_LADDER_EXT_TOP   = new UPropertyBool("east_ladder_ext_top");
	public static final UPropertyBool WEST_LADDER_EXT_TOP   = new UPropertyBool("west_ladder_ext_top");
	
	public static final UPropertyBool NE_LADDER_EXT  = new UPropertyBool("northeast_ladder_ext");
	public static final UPropertyBool NW_LADDER_EXT  = new UPropertyBool("northwest_ladder_ext");
	public static final UPropertyBool SE_LADDER_EXT  = new UPropertyBool("southeast_ladder_ext");
	public static final UPropertyBool SW_LADDER_EXT  = new UPropertyBool("southwest_ladder_ext");
	
	public static final UPropertyEnum<EnumFacing> FACING = UPropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	public static final String MODID = CatwalksMod.MODID;
	
	public static final Vec3 VEC_CENTER = new Vec3(0.5, 0.5, 0.5);
	public static final Vec3 VEC_ANTICENTER = new Vec3(-0.5, -0.5, -0.5);
	
	static {
		sideProperties.put(EnumFacing.UP, TOP);
		sideProperties.put(EnumFacing.DOWN, BOTTOM);
		
		sideProperties.put(EnumFacing.NORTH, NORTH);
		sideProperties.put(EnumFacing.SOUTH, SOUTH);
		sideProperties.put(EnumFacing.EAST, EAST);
		sideProperties.put(EnumFacing.WEST, WEST);
	}

	public static boolean developmentEnvironment = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
}
