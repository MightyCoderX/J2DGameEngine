package j2dgameengine.scene;

import imgui.ImGui;
import j2dgameengine.Camera;
import j2dgameengine.GameObject;
import j2dgameengine.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
	protected Renderer renderer;
	protected Camera camera;
	protected List<GameObject> gameObjects;
	protected GameObject activeGameObject;
	private boolean isRunning = false;

	public Scene()
	{
		renderer = new Renderer();
		gameObjects = new ArrayList<>();
	}

	public void init()
	{

	}

	public void start()
	{
		for(GameObject go : gameObjects)
		{
			go.start();
			renderer.add(go);
		}
		isRunning = true;
	}

	public void addGameObjectToScene(GameObject go)
	{
		gameObjects.add(go);
		if(isRunning)
		{
			go.start();
			renderer.add(go);
		}
	}

	public abstract void update(float dt);

	public Camera getCamera()
	{
		return camera;
	}

	public void sceneImGui()
	{
		if(activeGameObject == null) return;
		ImGui.begin("Inspector");
		activeGameObject.imGui();
		ImGui.end();

		imGui();
	}

	public void imGui()
	{

	}
}
