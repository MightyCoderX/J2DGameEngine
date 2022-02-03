package j2dgameengine.physics2d.components;

import j2dgameengine.components.Component;
import org.joml.Vector2f;

public abstract class Collider extends Component
{
	protected Vector2f offset = new Vector2f();

	public Vector2f getOffset()
	{
		return offset;
	}
}
