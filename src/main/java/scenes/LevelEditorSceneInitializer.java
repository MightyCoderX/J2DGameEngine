package scenes;

import imgui.ImGui;
import imgui.ImVec2;
import j2dgameengine.*;
import j2dgameengine.components.*;
import j2dgameengine.listeners.MouseListener;
import org.joml.Vector2f;
import j2dgameengine.util.AssetPool;

public class LevelEditorSceneInitializer extends SceneInitializer
{
	private Spritesheet sprites;

	private GameObject levelEditorStuff;

	public LevelEditorSceneInitializer() {	}

	@Override
	public void init(Scene scene)
	{
		sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
		Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

		levelEditorStuff = scene.createGameObject("LevelEditor");
		levelEditorStuff.setNoSerialize();
		levelEditorStuff.addComponent(new MouseControls());
		levelEditorStuff.addComponent(new GridLines());
		levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
		levelEditorStuff.addComponent(new GizmoSystem(gizmos));
		scene.addGameObjectToScene(levelEditorStuff);
	}

	@Override
	public void loadResources(Scene scene)
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
				new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
						16, 16, 81, 0));

		AssetPool.addSpritesheet("assets/images/gizmos.png",
				new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
						24, 48, 3, 0));

		for(GameObject go : scene.getGameObjects())
		{
			if(go.getComponent(SpriteRenderer.class) == null) continue;
			SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
			if(spriteRenderer.getTexture() == null) continue;

			spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilePath()));
		}
	}

	@Override
	public void imGui()
	{
		ImGui.begin("Level Editor Stuff");
		levelEditorStuff.imGui();
		ImGui.end();

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
			float spriteWidth = sprite.getWidth() * 2;
			float spriteHeight = sprite.getHeight() * 2;
			int id = sprite.getTexID();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if(ImGui.imageButton(id, spriteWidth, spriteHeight,
					texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
			{
				GameObject gameObject = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
				// Attach this to mouse cursor
				levelEditorStuff.getComponent(MouseControls.class).pickupObject(gameObject);
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
