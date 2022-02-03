package j2dgameengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import j2dgameengine.components.Component;
import j2dgameengine.components.ComponentTypeAdapter;
import j2dgameengine.components.SpriteRenderer;
import j2dgameengine.components.Transform;
import j2dgameengine.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
	private static int ID_COUNTER = 0;
	private int uid = -1;

	public String name;
	private List<Component> components;
	public transient Transform transform;

	private boolean doSerialization = true;
	private boolean isDead = false;

	public GameObject(String name)
	{
		this.name = name;
		this.components = new ArrayList<>();

		this.uid = ID_COUNTER++;
	}

	public void addComponent(Component c)
	{
		c.generateId();
		components.add(c);
		c.gameObject = this;
	}

	public <T extends Component> T getComponent(Class<T> componentClass)
	{
		for(Component c : components)
		{
			if(!componentClass.isAssignableFrom(c.getClass())) continue;

			return componentClass.cast(c);
		}

		return null;
	}

	public <T extends Component> void removeComponent(Class<T> componentClass)
	{
		for(int i = 0; i < components.size(); i++)
		{
			Component c = components.get(i);
			if(!componentClass.isAssignableFrom(c.getClass())) continue;

			components.remove(i);
			return;
		}
	}

	public List<Component> getComponents()
	{
		return components;
	}

	public void editorUpdate(float dt)
	{
		for(int i = 0; i < components.size(); i++)
		{
			components.get(i).editorUpdate(dt);
		}
	}

	public void update(float dt)
	{
		for(int i = 0; i < components.size(); i++)
		{
			components.get(i).update(dt);
		}
	}

	public void start()
	{
		for(int i = 0; i < components.size(); i++)
		{
			components.get(i).start();
		}
	}

	public void imGui()
	{
		for(Component c : components)
		{
			if(ImGui.collapsingHeader(c.getClass().getSimpleName()))
			{
				c.imGui();
			}
		}
	}

	public void destroy()
	{
		this.isDead = true;
		for(int i = 0; i < components.size(); i++)
		{
			components.get(i).destroy();
		}
	}

	public GameObject copy()
	{
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Component.class, new ComponentTypeAdapter())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();

		String objAsJson = gson.toJson(this);

		GameObject obj = gson.fromJson(objAsJson, GameObject.class);
		obj.generateUid();
		for(Component c : obj.getComponents())
		{
			c.generateId();
		}

		SpriteRenderer spriteRenderer = obj.getComponent(SpriteRenderer.class);

		if(spriteRenderer != null && spriteRenderer.getTexture() != null)
		{
			spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilePath()));
		}

		return obj;
	}

	public void generateUid()
	{
		this.uid = ID_COUNTER++;
	}

	public int getUid()
	{
		return uid;
	}

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}

	public void setNoSerialize()
	{
		this.doSerialization = false;
	}

	public boolean doSerialization()
	{
		return doSerialization;
	}

	public boolean isDead()
	{
		return isDead;
	}
}
