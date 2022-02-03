package j2dgameengine;

import j2dgameengine.components.Sprite;
import j2dgameengine.components.SpriteRenderer;
import j2dgameengine.components.Transform;

public class Prefabs
{
	public static GameObject generateSpriteObject(Sprite sprite, float width, float height)
	{
		GameObject spriteObject = Window.getScene().createGameObject("sprite_object_gen");
		spriteObject.transform.scale.x = width;
		spriteObject.transform.scale.y = height;

		SpriteRenderer renderer = new SpriteRenderer();
		renderer.setSprite(sprite);
		spriteObject.addComponent(renderer);

		return spriteObject;
	}

}
