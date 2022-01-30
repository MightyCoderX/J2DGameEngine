package j2dgameengine.components;

import imgui.ImGui;
import j2dgameengine.Component;
import j2dgameengine.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import j2dgameengine.renderer.Texture;

public class SpriteRenderer extends Component
{
	private Sprite sprite;
	private Vector4f color;

	private Transform lastTransform;
	boolean isDirty = false;

	public SpriteRenderer(Vector4f color)
	{
		this.color = color;
		this.sprite = new Sprite(null);
		this.isDirty = true;
	}

	public SpriteRenderer(float r, float g, float b, float a)
	{
		this(new Vector4f(r, g, b, a));
	}

	public SpriteRenderer(Sprite sprite)
	{
		this.sprite = sprite;
		this.color = new Vector4f(1, 1, 1, 1);
		this.isDirty = true;
	}

	@Override
	public void start()
	{
		lastTransform = gameObject.transform.copy();
	}

	@Override
	public void update(float dt)
	{
		if(!lastTransform.equals(gameObject.transform))
		{
			gameObject.transform.copy(lastTransform);
			isDirty = true;
		}
	}

	@Override
	public void imGui()
	{
		float[] imColor = {color.x, color.y, color.z, color.w};
		if(ImGui.colorPicker4("Color picker", imColor))
		{
			color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
			isDirty = true;
		}
	}

	public Vector4f getColor()
	{
		return color;
	}

	public Texture getTexture()
	{
		return sprite.getTexture();
	}

	public Vector2f[] getTexCoords()
	{
		return sprite.getTexCoords();
	}

	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
		this.isDirty = true;
	}

	public void setColor(Vector4f color)
	{
		if(this.color.equals(color)) return;

		this.color.set(color);
		this.isDirty = true;
	}

	public boolean isDirty()
	{
		return isDirty;
	}

	public void setClean()
	{
		this.isDirty = false;
	}
}
