package scenes;

import imgui.ImGui;
import imgui.ImVec2;
import j2dgameengine.*;
import j2dgameengine.components.*;
import j2dgameengine.renderer.DebugDraw;
import org.joml.Vector2f;
import j2dgameengine.util.AssetPool;
import org.joml.Vector3f;

public class LevelEditorScene extends Scene
{
	private Spritesheet sprites;

	private MouseControls mouseControls = new MouseControls();

	public LevelEditorScene() {	}

	@Override
	public void init()
	{
		loadResources();
		camera = new Camera(new Vector2f());
		sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

		DebugDraw.addLine2D(new Vector2f(0, 0), new Vector2f(900, 900), new Vector3f(0, 0, 1), 200);
		System.out.println("Added line from (0, 0) to (900, 900)");

		if(levelLoaded)
		{
			activeGameObject = gameObjects.get(0);
			return;
		}

		GameObject obj1 = new GameObject("Red", new Transform(400, 100, 256, 256), 2);

		SpriteRenderer spriteRendererObj1 = new SpriteRenderer();
		spriteRendererObj1.setColor(1, 0, 0, 1);

		obj1.addComponent(spriteRendererObj1);
		obj1.addComponent(new RigidBody());
		addGameObjectToScene(obj1);
		activeGameObject = obj1;

		GameObject obj2 = new GameObject("Green", new Transform(600, 100, 256, 256), -1);

		SpriteRenderer spriteRendererObj2 = new SpriteRenderer();

		Sprite sprite = new Sprite();
		sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));

		spriteRendererObj2.setSprite(sprite);

		obj2.addComponent(spriteRendererObj2);
		addGameObjectToScene(obj2);
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
				new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
								16, 16, 81, 0));

		AssetPool.getTexture("assets/images/blendImage2.png");
	}

	@Override
	public void update(float dt)
	{
		mouseControls.update(dt);

		for(GameObject go : gameObjects)
		{
			go.update(dt);
		}

		renderer.render();
	}

	@Override
	public void imGui()
	{
		ImGui.begin("Test window");

		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);

		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);

		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);

		float windowX2 = windowPos.x + windowSize.x;

		for(int i = 0; i < sprites.size(); i++)
		{
			Sprite sprite = sprites.getSprite(i);
			float spriteWidth = sprite.getWidth() * 4;
			float spriteHeight = sprite.getHeight() * 4;
			int id = sprite.getTexID();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if(ImGui.imageButton(id, spriteWidth, spriteHeight,
					texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y))
			{
				GameObject gameObject = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
				// Attach this to mouse cursor
				mouseControls.pickupObject(gameObject);
			}
			ImGui.popID();

			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			if(i + 1 < sprites.size() && nextButtonX2 < windowX2)
			{
				ImGui.sameLine();
			}
		}

		ImGui.end();
	}
}
