package catwalks;

import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.block.property.UPropertyBool;
import catwalks.block.property.UPropertyEnum;
import catwalks.block.property.UPropertyObject;
import catwalks.movement.capability.ICWEntityData;
import catwalks.part.data.CatwalkRenderData;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static catwalks.util.meta.MetaStorage.bits;

public class Const {
	
	{ /* Centeralizing constants */ }
	
	public static int MATERIAL_BITS = bits(128);
	
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
	
	{	/* blockstate properties*/	}
	
	public static final PropertyBool LIGHTS = PropertyBool.create("lights");
	public static final PropertyEnum<EnumCatwalkMaterialOld> MATERIAL_META_OLD = PropertyEnum.create("material", EnumCatwalkMaterialOld.class);
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL_META = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	
	public static final UPropertyEnum<EnumCatwalkMaterialOld> MATERIAL_OLD = UPropertyEnum.create("material", EnumCatwalkMaterialOld.class);
	public static final UPropertyEnum<EnumCatwalkMaterial> MATERIAL = UPropertyEnum.create("material", EnumCatwalkMaterial.class);
	
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
	
	public static final UPropertyEnum<EnumFacing> FACING = UPropertyEnum.create("facing", EnumFacing.class);
	
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

	public static final String COMMAND_OPTIONS = "options";
	
	public static final UPropertyObject<CatwalkRenderData> CATWALK_RENDER_DATA = new UPropertyObject<CatwalkRenderData>("renderdata", CatwalkRenderData.class);
	
	static {
		sideProperties.put(EnumFacing.UP, TOP);
		sideProperties.put(EnumFacing.DOWN, BOTTOM);
		
		sideProperties.put(EnumFacing.NORTH, NORTH);
		sideProperties.put(EnumFacing.SOUTH, SOUTH);
		sideProperties.put(EnumFacing.EAST, EAST);
		sideProperties.put(EnumFacing.WEST, WEST);
	}
	
	public static class NODE {
		private static int i = -1;
		public static final int PITCH  = i--;
		public static final int YAW  = i--;
		public static final int CONNECT_POINT = i--;
		public static final int OPEN_GUI = i--;
	};
	
	public static class GUI {
		public static int NODE_MANIPULATOR = -1;
	}
	
	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}
}
