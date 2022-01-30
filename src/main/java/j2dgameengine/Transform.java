package j2dgameengine;

import org.joml.Vector2f;

import java.util.Objects;

public class Transform
{
	public Vector2f position;
	public Vector2f scale;

	public Transform(Vector2f position, Vector2f scale)
	{
		this.position = position;
		this.scale = scale;
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

		return t.position.equals(this.position) && t.scale.equals(this.scale);
	}
}
