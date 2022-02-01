package j2dgameengine.components;

import org.joml.Vector2f;
import j2dgameengine.renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet
{
	private Texture texture;
	private List<Sprite> sprites;

	public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int spriteCount, int spacing)
	{
		this.texture = texture;
		this.sprites = new ArrayList<>();

		int currentX = 0;
		int currentY = texture.getHeight() - spriteHeight;

		for(int i = 0; i < spriteCount; i++)
		{
			float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
			float topY = (currentY + spriteHeight) / (float)texture.getHeight();
			float leftX = currentX / (float)texture.getWidth();
			float bottomY = currentY / (float)texture.getHeight();

			Vector2f[] texCoords = {
				new Vector2f(rightX, topY),
				new Vector2f(rightX, bottomY),
				new Vector2f(leftX, bottomY),
				new Vector2f(leftX, topY)
			};

			Sprite sprite = new Sprite();
			sprite.setTexture(texture);
			sprite.setTexCoords(texCoords);
			sprite.setSize(spriteWidth, spriteHeight);
			sprites.add(sprite);

			currentX += spriteWidth + spacing;
			if(currentX >= texture.getWidth())
			{
				currentX = 0;
				currentY -= spriteHeight + spacing;
			}
		}
	}

	public Sprite getSprite(int index)
	{
		return sprites.get(index);
	}

	public int size()
	{
		return sprites.size();
	}
}
