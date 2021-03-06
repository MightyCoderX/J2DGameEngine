package j2dgameengine.components;

import org.joml.Vector2f;
import j2dgameengine.renderer.Texture;

public class Sprite
{
	private float width, height;

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

	public Sprite()
	{
		this(null);
	}

	public float getWidth()
	{
		return width;
	}

	public void setSize(float width, float height)
	{
		this.width = width;
		this.height = height;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public Vector2f[] getTexCoords()
	{
		return texCoords;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public void setTexCoords(Vector2f[] texCoords)
	{
		this.texCoords = texCoords;
	}

	public int getTexID()
	{
		return texture == null ? -1 : texture.getId();
	}
}
