package j2dgameengine;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
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
}
