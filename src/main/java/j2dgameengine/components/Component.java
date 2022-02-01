package j2dgameengine.components;

import imgui.ImGui;
import j2dgameengine.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component
{
	private static int ID_COUNTER = 0;
	private int uid = -1;

	public transient GameObject gameObject = null;

	public void start() {}

	public void update(float dt) {}

	public void imGui()
	{
		try
		{
			Field[] fields = getClass().getDeclaredFields();

			for (Field field : fields)
			{
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				if(isTransient) continue;

				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				if(isPrivate)
				{
					field.setAccessible(true);
				}

				Class type = field.getType();
				String name = field.getName();
				Object value = field.get(this);

				if(type.equals(int.class))
				{
					int[] imInt = {(int) value};

					if(ImGui.dragInt(name + ": ", imInt))
					{
						field.set(this, imInt[0]);
					}
				}
				else if(type.equals(float.class))
				{
					float[] imFloat = {(float) value};

					if(ImGui.dragFloat(name + ": ", imFloat))
					{
						field.set(this, imFloat[0]);
					}
				}
				else if(type.equals(boolean.class))
				{
					boolean val = (boolean) value;

					if(ImGui.checkbox(name + ": ", val))
					{
						field.set(this, !val);
					}
				}
				else if(type.equals(Vector3f.class))
				{
					Vector3f val = (Vector3f) value;
					float[] imVec3f = { val.x, val.y, val.z };

					if(ImGui.dragFloat3(name + ": ", imVec3f))
					{
						val.set(imVec3f);
					}
				}
				else if(type.equals(Vector4f.class))
				{
					Vector4f val = (Vector4f) value;
					float[] imVec4f = { val.x, val.y, val.z, val.w };

					if(ImGui.dragFloat4(name + ": ", imVec4f))
					{
						val.set(imVec4f);
					}
				}

				if(isPrivate)
				{
					field.setAccessible(false);
				}
			}
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}

	public void generateId()
	{
		if(uid > -1) return;

		uid = ID_COUNTER++;
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
