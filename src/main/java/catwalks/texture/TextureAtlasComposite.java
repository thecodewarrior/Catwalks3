package catwalks.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasComposite extends TextureAtlasSprite {

	CompositeTexture texture;
	
	public TextureAtlasComposite(CompositeTexture texture) {
		super(texture.name.toString());
		this.texture = texture;
	}
	
	public ResourceLocation fileLoc(ResourceLocation loc) {
		return new ResourceLocation(loc.getResourceDomain(), "textures/" + loc.getResourcePath() + ".png");
	}
	
	public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        ResourceLocation filelocation = fileLoc(location);
        
        try {
            // check to see if the resource can be loaded (someone added an
            // override)
            manager.getResource(filelocation);
//            LogHelper.info("Dense Ores: Detected override for " + name);
            return false;
        } catch (IOException e) {
            // file not found: let's generate one
            return true;
        }
    }
	
	public boolean load(IResourceManager manager, ResourceLocation location) {
		BufferedImage sofar = null, stitching = null;
      AnimationMetadataSection animation = null;
      
		try {
			for (ResourceLocation loc : texture.layers) {
				IResource iresource = manager.getResource(fileLoc(loc));
				stitching = ImageIO.read(iresource.getInputStream());
				
				if(sofar == null) {
					sofar = stitching;
					animation = (AnimationMetadataSection) iresource.getMetadata("animation");
					continue;
				}
				
				composite(sofar, stitching);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		
		
		int mp = Minecraft.getMinecraft().gameSettings.mipmapLevels;
		BufferedImage[] finalTexture = new BufferedImage[1 + mp];
		finalTexture[0] = sofar;
		
		try {
			this.loadSprite(finalTexture, animation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return false;
    }
	
	public void composite(BufferedImage bg, BufferedImage fg) {
		if(bg.getWidth() != fg.getWidth()) {
			throw new IllegalArgumentException(String.format(  "Width not equal! bg: %d, fg: %d for texture %s", bg.getWidth(),  fg.getWidth(),  texture.name.toString() ));
		}
		if(bg.getHeight() != fg.getHeight()) {
			throw new IllegalArgumentException(String.format( "Height not equal! bg: %d, fg: %d for texture %s", bg.getHeight(), fg.getHeight(), texture.name.toString() ));
		}
				
		Graphics2D g = bg.createGraphics();
		g.drawImage(fg, 0, 0, null);
		g.dispose();
		
	}
}
