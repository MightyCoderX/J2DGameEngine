package j2dgameengine.components;

import j2dgameengine.editor.JImGui;
import org.joml.Vector2f;

public class Transform extends Component
{
	public Vector2f position;
	public Vector2f scale;
	public float rotation;
	public int zIndex;

	public Transform(Vector2f position, Vector2f scale)
	{
		this.position = position;
		this.scale = scale;
		this.rotation = 0.0f;
		this.zIndex = 0;
	}

	public Transform(float x, float y, float scaleX, float scaleY)
	{
		this(new Vector2f(x, y), new Vector2f(scaleX, scaleY));
	}

	public Transform(Vector2f position)
	{
		this(position, new Vector2f());
	}

	public Transform(float x, float y)
	{
		this(new Vector2f(x, y), new Vector2f());
	}

	public Transform()
	{
		this(new Vector2f(), new Vector2f());
	}

	@Override
	public void imGui()
	{
		gameObject.name = JImGui.inputText("Name: ", gameObject.name);
		JImGui.drawVec2Control("Position", position);
		JImGui.drawVec2Control("Scale", scale, 0.25f);
		rotation = JImGui.dragFloat("Rotation", rotation);
		zIndex = JImGui.dragInt("Z-Index", zIndex);
	}

	public Transform copy()
	{
		return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
	}

	public void copy(Transform to)
	{
		to.position.set(this.position);
		to.scale.set(this.scale);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if(!(o instanceof Transform t)) return false;

		return t.position.equals(this.position) && t.scale.equals(this.scale)
				&& t.rotation == this.rotation && t.zIndex == this.zIndex;
	}
}
