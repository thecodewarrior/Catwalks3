package catwalks;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.property.UPropertyBool;
import catwalks.block.property.UPropertyEnum;
import catwalks.movement.capability.ICWEntityData;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Const {
	
	{ /* DRYing constants */ }
	
	public static Random RAND = new Random();
	
	public static EnumFacing[] HORIZONTALS_FROM_NORTH = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST };
	
	public static boolean developmentEnvironment = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final String MODID = CatwalksMod.MODID;
	
	public static final Vec3d VEC_CENTER = new Vec3d(0.5, 0.5, 0.5);
	public static final Vec3d VEC_ANTICENTER = new Vec3d(-0.5, -0.5, -0.5);
	
	public static final ResourceLocation ENTITY_DATA_CAPABILITY_LOC = new ResourceLocation(MODID, "entityData");
	
	@CapabilityInject(ICWEntityData.class)
	public static Capability<ICWEntityData> CW_ENTITY_DATA_CAPABILITY = null;
	
	{ /* not render-only properties */ }
	
	{	/* normal blockstate properties*/	}
	
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	public static final PropertyBool LIGHTS = PropertyBool.create("lights");
	
	
	public static final UPropertyBool IS_TOP    = UPropertyBool.create("istop");
	public static final UPropertyBool IS_BOTTOM = UPropertyBool.create("isbottom");
	
	public static final UPropertyBool BOTTOM = UPropertyBool.create("bottom");
	public static final UPropertyBool TOP    = UPropertyBool.create("top");
	public static final UPropertyBool NORTH  = UPropertyBool.create("north");
	public static final UPropertyBool SOUTH  = UPropertyBool.create("south");
	public static final UPropertyBool EAST   = UPropertyBool.create("east");
	public static final UPropertyBool WEST   = UPropertyBool.create("west");
	
	public static final Map<EnumFacing, UPropertyBool> sideProperties = new EnumMap<EnumFacing, UPropertyBool>(EnumFacing.class);
	
	public static final UPropertyBool EAST_TOP = UPropertyBool.create("easttop");
	public static final UPropertyBool WEST_TOP = UPropertyBool.create("westtop");
	
	public static final UPropertyBool TAPE   = UPropertyBool.create("tape");
	public static final UPropertyBool SPEED  = UPropertyBool.create("speed");
	
	public static final UPropertyEnum<EnumFacing> FACING = UPropertyEnum.create("facing", EnumFacing.class, (v) -> v.getAxis() != Axis.Y);
	
	public static final UPropertyBool CONST_TRUE = UPropertyBool.create("dummyConstTrue");
	public static final UPropertyBool CONST_FALSE = UPropertyBool.create("dummyConstFalse");
	
	{ /* render-only properties (unlisted properties) */ }
	
	public static final UPropertyBool UNLISTED_FLUFF_PROPERTY = UPropertyBool.create("UNLISTED_FLUFF");
	
	public static final UPropertyBool NORTH_LADDER_EXT  = UPropertyBool.create("north_ladder_ext");
	public static final UPropertyBool SOUTH_LADDER_EXT  = UPropertyBool.create("south_ladder_ext");
	public static final UPropertyBool EAST_LADDER_EXT   = UPropertyBool.create("east_ladder_ext");
	public static final UPropertyBool WEST_LADDER_EXT   = UPropertyBool.create("west_ladder_ext");
	
	public static final UPropertyBool NORTH_LADDER_EXT_TOP  = UPropertyBool.create("north_ladder_ext_top");
	public static final UPropertyBool SOUTH_LADDER_EXT_TOP  = UPropertyBool.create("south_ladder_ext_top");
	public static final UPropertyBool EAST_LADDER_EXT_TOP   = UPropertyBool.create("east_ladder_ext_top");
	public static final UPropertyBool WEST_LADDER_EXT_TOP   = UPropertyBool.create("west_ladder_ext_top");
	
	public static final UPropertyBool NE_LADDER_EXT  = UPropertyBool.create("northeast_ladder_ext");
	public static final UPropertyBool NW_LADDER_EXT  = UPropertyBool.create("northwest_ladder_ext");
	public static final UPropertyBool SE_LADDER_EXT  = UPropertyBool.create("southeast_ladder_ext");
	public static final UPropertyBool SW_LADDER_EXT  = UPropertyBool.create("southwest_ladder_ext");
	
	static {
		sideProperties.put(EnumFacing.UP, TOP);
		sideProperties.put(EnumFacing.DOWN, BOTTOM);
		
		sideProperties.put(EnumFacing.NORTH, NORTH);
		sideProperties.put(EnumFacing.SOUTH, SOUTH);
		sideProperties.put(EnumFacing.EAST, EAST);
		sideProperties.put(EnumFacing.WEST, WEST);
	}
	
	public static class NODE {
		public static final int PITCH_PLUS  = -1;
		public static final int PITCH_MINUS = -2;
		public static final int YAW_PLUS  = -3;
		public static final int YAW_MINUS = -4;
		public static final int CONNECT_POINT = -5;
	};
}
