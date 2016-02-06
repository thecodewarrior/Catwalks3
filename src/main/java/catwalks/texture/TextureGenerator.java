package catwalks.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import catwalks.CatwalksMod;
import catwalks.util.Logs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TextureGenerator {

	public static TextureGenerator instance = new TextureGenerator();
	
	public static Map<ResourceLocation, CompositeTexture> textures = new HashMap<>();
	
	public static void addTexture(CompositeTexture texture) {
		textures.put(texture.name, texture);
	}
	
	@SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
		
		TextureMap map = event.map;
		
		for(CompositeTexture tex : textures.values()) {
			
			map.getTextureExtry(tex.name.toString());
			TextureAtlasSprite texture = map.getTextureExtry(tex.name.toString());
			
			if(texture == null) {
				texture = new TextureAtlasComposite(tex);
				map.setTextureEntry(tex.name.toString(), texture);
			}
		}

    }
	
	@SubscribeEvent
    public void dumpAtlas(TextureStitchEvent.Post event) {
		if(!CatwalksMod.developmentEnvironment)
			return;
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        Logs.log("Dumping atlas...");
        Logs.log(String.format("Atlas is %d wide by %d tall%n", width, height));

        int pixels = width * height;
        
        IntBuffer buffer = BufferUtils.createIntBuffer(pixels);
        int[] pixelValues = new int[pixels];

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);

        buffer.get(pixelValues);

        BufferedImage bufferedimage = new BufferedImage(width, height, 2);

        for (int k = 0; k < height; ++k)
        {
            for (int l = 0; l < width; ++l)
            {
                bufferedimage.setRGB(l, k, pixelValues[k * width + l]);
            }
        }

        File mcFolder = Minecraft.getMinecraft().mcDataDir;
        File result = new File(mcFolder, "atlas.png");

        try {
            ImageIO.write(bufferedimage, "png", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
