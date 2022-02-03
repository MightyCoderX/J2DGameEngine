package j2dgameengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import j2dgameengine.components.Component;
import j2dgameengine.components.ComponentTypeAdapter;
import j2dgameengine.components.Transform;
import j2dgameengine.physics2d.Physics2D;
import j2dgameengine.renderer.Renderer;
import org.joml.Vector2f;
import scenes.SceneInitializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene
{
	private Renderer renderer;
	private Camera camera;
	private List<GameObject> gameObjects;
	private Physics2D physics2D;

	private boolean isRunning;

	private final String levelFilePath = "level.json";

	private Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Component.class, new ComponentTypeAdapter())
			.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
			.create();

	private SceneInitializer sceneInitializer;

	public Scene(SceneInitializer sceneInitializer)
	{
		this.sceneInitializer = sceneInitializer;

		this.physics2D = new Physics2D();
		this.renderer = new Renderer();
		this.gameObjects = new ArrayList<>();
		this.isRunning = false;
	}

	public void init()
	{
		this.camera = new Camera(new Vector2f());
		this.sceneInitializer.loadResources(this);
		this.sceneInitializer.init(this);
	}

	public void start()
	{
		for(int i = 0; i < gameObjects.size(); i++)
		{
			GameObject go = gameObjects.get(i);
			go.start();
			renderer.add(go);
			physics2D.add(go);
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
			physics2D.add(go);
		}
	}

	public void editorUpdate(float dt)
	{
		this.camera.adjustProjection();

		for(int i = 0; i < gameObjects.size(); i++)
		{
			GameObject go = gameObjects.get(i);
			go.editorUpdate(dt);

			if(go.isDead())
			{
				gameObjects.remove(i);
				this.renderer.destroyGameObject(go);
				this.physics2D.destroyGameObject(go);
				i--;
			}
		}
	}

	public void update(float dt)
	{
		this.camera.adjustProjection();
		this.physics2D.update(dt);

		for(int i = 0; i < gameObjects.size(); i++)
		{
			GameObject go = gameObjects.get(i);

			if(go.isDead())
			{
				gameObjects.remove(i);
				this.renderer.destroyGameObject(go);
				this.physics2D.destroyGameObject(go);
				i--;
			}

			go.update(dt);
		}
	}

	public void render()
	{
		this.renderer.render();
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void imGui()
	{
		this.sceneInitializer.imGui();
	}

	public GameObject createGameObject(String name)
	{
		GameObject gameObject = new GameObject(name);
		gameObject.addComponent(new Transform());
		gameObject.transform = gameObject.getComponent(Transform.class);

		return gameObject;
	}

	public void save()
	{
		try
		{
			FileWriter writer = new FileWriter(levelFilePath);
			List<GameObject> toSerialize = gameObjects.stream()
					.filter(GameObject::doSerialization)
					.toList();
			writer.write(gson.toJson(toSerialize));
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
			if(!Files.exists(Paths.get(levelFilePath))) return;
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
		if(objects.length <= 0) return;
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
	}

	public void destroy()
	{
		for(GameObject go : gameObjects)
		{
			go.destroy();
		}
	}

	public GameObject getGameObject(int id)
	{
		Optional<GameObject> result = gameObjects.stream()
				.filter(gameObject -> gameObject.getUid() == id).findFirst();

		return result.orElse(null);
	}

	public List<GameObject> getGameObjects()
	{
		return gameObjects;
	}
}
