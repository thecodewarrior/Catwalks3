package catwalks;

import java.util.EnumMap;
import java.util.Map;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.property.UPropertyBool;
import catwalks.block.property.UPropertyEnum;
import catwalks.movement.capability.ICWEntityData;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Const {
	
	{ /* DRYing constants */ }
	
	public static boolean developmentEnvironment = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final String MODID = CatwalksMod.MODID;
	
	public static final Vec3d VEC_CENTER = new Vec3d(0.5, 0.5, 0.5);
	public static final Vec3d VEC_ANTICENTER = new Vec3d(-0.5, -0.5, -0.5);
	
	public static final ResourceLocation ENTITY_DATA_CAPABILITY_LOC = new ResourceLocation(MODID, "entityData");
	
	@CapabilityInject(ICWEntityData.class)
	public static Capability<ICWEntityData> CW_ENTITY_DATA_CAPABILITY = null;
	
	{ /* not render-only properties */ }
	
	public static final PropertyBool IS_TOP    = PropertyBool.create("istop");
	public static final PropertyBool IS_BOTTOM = PropertyBool.create("isbottom");
	
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
	public static final PropertyBool TOP    = PropertyBool.create("top");
	public static final PropertyBool NORTH  = PropertyBool.create("north");
	public static final PropertyBool SOUTH  = PropertyBool.create("south");
	public static final PropertyBool EAST   = PropertyBool.create("east");
	public static final PropertyBool WEST   = PropertyBool.create("west");
	
	public static final Map<EnumFacing, PropertyBool> sideProperties = new EnumMap<EnumFacing, PropertyBool>(EnumFacing.class);
	
	public static final PropertyBool EAST_TOP = PropertyBool.create("easttop");
	public static final PropertyBool WEST_TOP = PropertyBool.create("westtop");
	
	public static final PropertyBool TAPE   = PropertyBool.create("tape");
	public static final PropertyBool LIGHTS = PropertyBool.create("lights");
	public static final PropertyBool SPEED  = PropertyBool.create("speed");
	
	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	
	public static final PropertyBool CONST_TRUE = PropertyBool.create("dummyConstTrue");
	public static final PropertyBool CONST_FALSE = PropertyBool.create("dummyConstFalse");
	
	{ /* render-only properties (unlisted properties) */ }
	
	public static final UPropertyBool UNLISTED_FLUFF_PROPERTY = new UPropertyBool("UNLISTED_FLUFF");
	
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
	
	static {
		sideProperties.put(EnumFacing.UP, TOP);
		sideProperties.put(EnumFacing.DOWN, BOTTOM);
		
		sideProperties.put(EnumFacing.NORTH, NORTH);
		sideProperties.put(EnumFacing.SOUTH, SOUTH);
		sideProperties.put(EnumFacing.EAST, EAST);
		sideProperties.put(EnumFacing.WEST, WEST);
	}
}
