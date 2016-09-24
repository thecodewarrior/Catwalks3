package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.item.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public class ItemRegister {
	
	public static ItemDecoration lights, tape, speed;
	public static ItemCatwalkTool tool;
	public static ItemBase grate;
	public static ItemLadderGrabber ladderGrabber;
	public static ItemScaffold scaffold;
	public static ItemCatwalk catwalk;
	
	public static void register() {
		lights = new ItemDecoration("lights");
		tape   = new ItemDecoration("tape");
		speed  = new ItemDecoration("speed");
		tool   = new ItemCatwalkTool();
		grate  = new ItemBase("steelgrate");
		ladderGrabber = new ItemLadderGrabber();
		scaffold = new ItemScaffold("scaffoldpart");
		catwalk = new ItemCatwalk("catwalkpart");
	}
	
	public static List<ItemBase> renderRegsiterItems = new ArrayList<>();
}
