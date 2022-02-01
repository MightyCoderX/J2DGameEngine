package j2dgameengine;

import j2dgameengine.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
	private static int ID_COUNTER = 0;
	private int uid = -1;

	private String name;
	public Transform transform;
	private List<Component> components;
	private int zIndex;

	public GameObject(String name, Transform transform, int zIndex)
	{
		this.name = name;
		this.transform = transform;
		this.components = new ArrayList<>();
		this.zIndex = zIndex;

		this.uid = ID_COUNTER++;
	}

	public GameObject(String name, Transform transform)
	{
		this(name, transform, 0);
	}

	public GameObject(String name)
	{
		this(name, new Transform(), 0);
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

	public void update(float dt)
	{
		for(int i = 0; i < components.size(); i++)
		{
			components.get(i).update(dt);
		}
	}

	public void start()
	{
		for(Component c : components)
		{
			c.start();
		}
	}

	public void imGui()
	{
		for(Component c : components)
		{
			c.imGui();
		}
	}

	public int zIndex()
	{
		return zIndex;
	}

	public int getUid()
	{
		return uid;
	}

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}
}
