package j2dgameengine.util;

import j2dgameengine.components.Spritesheet;
import j2dgameengine.renderer.Shader;
import j2dgameengine.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool
{
	private static final Map<String, Shader> shaders = new HashMap<>();
	private static final Map<String, Texture> textures = new HashMap<>();
	private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

	public static Shader getShader(String assetName)
	{
		File file = new File(assetName);

		if(shaders.containsKey(file.getAbsolutePath()))
		{
			return shaders.get(file.getAbsolutePath());
		}
		else
		{
			Shader shader = new Shader(assetName);
			shader.compileAndLink();
			shaders.put(file.getAbsolutePath(), shader);
			return shader;
		}
	}

	public static Texture getTexture(String assetName)
	{
		File file = new File(assetName);

		if(textures.containsKey(file.getAbsolutePath()))
		{
			return textures.get(file.getAbsolutePath());
		}
		else
		{
			Texture texture = new Texture(assetName);
			textures.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

	public static void addSpritesheet(String assetName, Spritesheet spritesheet)
	{
		File file = new File(assetName);

		if(!spritesheets.containsKey(file.getAbsolutePath()))
		{
			spritesheets.put(file.getAbsolutePath(), spritesheet);
		}
	}

	public static Spritesheet getSpritesheet(String assetName)
	{
		File file = new File(assetName);

		if(!spritesheets.containsKey(file.getAbsolutePath()))
		{
			assert false : "Error: tried to access spritesheet '" + assetName + "' and it has not been added to asset pool";
		}

		return spritesheets.getOrDefault(file.getAbsolutePath(), null);
	}
}
