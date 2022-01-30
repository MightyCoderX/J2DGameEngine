package j2dgameengine.components;

import org.joml.Vector2f;
import j2dgameengine.renderer.Texture;

public class Sprite
{
	private Texture texture;

	private Vector2f[] texCoords;

	public Sprite(Texture texture, Vector2f[] texCoords)
	{
		this.texture = texture;
		this.texCoords = texCoords;
	}

	public Sprite(Texture texture)
	{
		this(texture, new Vector2f[] {
			new Vector2f(1, 1),
			new Vector2f(1, 0),
			new Vector2f(0, 0),
			new Vector2f(0, 1)
		});
	}

	public Texture getTexture()
	{
		return texture;
	}

	public Vector2f[] getTexCoords()
	{
		return texCoords;
	}
}
