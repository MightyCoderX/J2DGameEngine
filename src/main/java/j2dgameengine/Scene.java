package j2dgameengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import j2dgameengine.*;
import j2dgameengine.components.Component;
import j2dgameengine.components.ComponentTypeAdapter;
import j2dgameengine.renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
	protected Renderer renderer;
	protected Camera camera;
	protected List<GameObject> gameObjects;
	protected GameObject activeGameObject;
	private boolean isRunning = false;
	private final String levelFilePath = "level.json";
	private Gson gson;
	protected boolean levelLoaded = false;

	public Scene()
	{
		renderer = new Renderer();
		gameObjects = new ArrayList<>();
		gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(Component.class, new ComponentTypeAdapter())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();
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

	public void saveExit()
	{
		try
		{
			FileWriter writer = new FileWriter(levelFilePath);
			writer.write(gson.toJson(gameObjects));
			writer.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void load()
	{
		String inFile = "";
		try
		{
			inFile = new String(Files.readAllBytes(Paths.get(levelFilePath)));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		if(inFile.isBlank()) return;

		int maxGoId = -1;
		int maxCompId = -1;

		GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
		for(GameObject object : objects)
		{
			addGameObjectToScene(object);

			for(Component c : object.getComponents())
			{
				if(c.getUid() > maxCompId)
				{
					maxCompId = c.getUid();
				}
			}

			if(object.getUid() > maxGoId)
			{
				maxGoId = object.getUid();
			}
		}

		maxGoId++;
		maxCompId++;
		GameObject.init(maxGoId);
		Component.init(maxCompId);

		levelLoaded = true;
	}
}
