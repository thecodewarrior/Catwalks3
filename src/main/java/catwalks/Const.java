package catwalks;

import catwalks.block.property.UPropertyEnum;
import catwalks.block.property.UPropertyObject;
import catwalks.part.data.CatwalkRenderData;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

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
//
//	@CapabilityInject(ICWEntityData.class)
//	public static Capability<ICWEntityData> CW_ENTITY_DATA_CAPABILITY = null;
	
	{	/* blockstate properties*/	}
	
	public static final UPropertyEnum<EnumFacing> FACING = UPropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	
	{ /* render-only properties (unlisted properties) */ }
	
	public static final String COMMAND_OPTIONS = "options";
	
	public static final UPropertyObject<CatwalkRenderData> CATWALK_RENDER_DATA = new UPropertyObject<CatwalkRenderData>("renderdata", CatwalkRenderData.class);
	
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
