package j2dgameengine;

import j2dgameengine.components.Sprite;
import j2dgameengine.components.SpriteRenderer;

public class Prefabs
{
	public static GameObject generateSpriteObject(Sprite sprite, float width, float height)
	{
		GameObject spriteObject = new GameObject("sprite_object_gen", new Transform(0, 0, width, height));
		SpriteRenderer renderer = new SpriteRenderer();
		renderer.setSprite(sprite);
		spriteObject.addComponent(renderer);

		return spriteObject;
	}

}
