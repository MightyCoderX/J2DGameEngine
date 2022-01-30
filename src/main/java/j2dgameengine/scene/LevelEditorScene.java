package j2dgameengine.scene;

import imgui.ImGui;
import j2dgameengine.components.Sprite;
import j2dgameengine.components.SpriteRenderer;
import j2dgameengine.components.Spritesheet;
import j2dgameengine.Camera;
import j2dgameengine.GameObject;
import j2dgameengine.Transform;
import org.joml.Vector2f;
import j2dgameengine.util.AssetPool;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene
{
	private GameObject obj1;
	private Spritesheet sprites;

	public LevelEditorScene() {	}

	@Override
	public void init()
	{
		loadResources();

		sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

		camera = new Camera(new Vector2f());

		obj1 = new GameObject("Red", new Transform(200, 100, 256, 256), 2);
		obj1.addComponent(new SpriteRenderer(1, 0, 0, 1));
		addGameObjectToScene(obj1);
		activeGameObject = obj1;

		GameObject obj2 = new GameObject("Green", new Transform(400, 100, 256, 256), -1);
		obj2.addComponent(new SpriteRenderer(new Sprite(
				AssetPool.getTexture("assets/images/blendImage2.png")
		)));
		addGameObjectToScene(obj2);
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.addSpritesheet("assets/images/spritesheet.png",
				new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
								16, 16, 26, 0));
	}

	@Override
	public void update(float dt)
	{
//		System.out.println((1.0f / dt) + " fps " + dt);

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
		ImGui.text("Some random text");
		ImGui.end();
	}
}
