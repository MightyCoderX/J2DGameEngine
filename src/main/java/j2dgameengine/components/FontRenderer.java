package j2dgameengine.components;

import j2dgameengine.Component;

public class FontRenderer extends Component
{
	@Override
	public void start()
	{
		if(gameObject.getComponent(SpriteRenderer.class) != null)
		{
			System.out.println("Found SpriteRenderer");
		}
	}

	@Override
	public void update(float dt)
	{

	}
}