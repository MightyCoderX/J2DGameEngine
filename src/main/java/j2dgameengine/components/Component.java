package j2dgameengine.components;

import imgui.ImGui;
import imgui.type.ImInt;
import j2dgameengine.GameObject;
import j2dgameengine.editor.JImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public abstract class Component
{
	private static int ID_COUNTER = 0;
	private int uid = -1;

	public transient GameObject gameObject = null;

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}

	public void start() {}

	public void editorUpdate(float dt) {}

	public void update(float dt) {}

	public void destroy() {}

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
					field.set(this, JImGui.dragInt(name, (int) value));
				}
				else if(type.equals(float.class))
				{
					field.set(this, JImGui.dragFloat(name, (float) value));
				}
				else if(type.equals(boolean.class))
				{
					boolean val = (boolean) value;

					if(ImGui.checkbox(name + ": ", val))
					{
						field.set(this, !val);
					}
				}
				else if(type.equals(Vector2f.class))
				{
					Vector2f val = (Vector2f) value;
					JImGui.drawVec2Control(name, val);
				}
				else if(type.equals(Vector3f.class))
				{
					Vector3f val = (Vector3f) value;
					float[] imVec = { val.x, val.y, val.z };

					if(ImGui.dragFloat3(name + ": ", imVec))
					{
						val.set(imVec);
					}
				}
				else if(type.equals(Vector4f.class))
				{
					Vector4f val = (Vector4f) value;
					float[] imVec = { val.x, val.y, val.z, val.w };

					if(ImGui.dragFloat4(name + ": ", imVec))
					{
						val.set(imVec);
					}
				}
				else if(type.isEnum())
				{
					String[] enumValues = getEnumValues(type);
					String enumType = ((Enum<?>) value).name();
					ImInt index = new ImInt(enumIndexOf(enumType, enumValues));
					if(ImGui.combo(field.getName(), index, enumValues, enumValues.length))
					{
						field.set(this, type.getEnumConstants()[index.get()]);
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

	private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType)
	{
		String[] enumValues = new String[enumType.getEnumConstants().length];
		int i = 0;
		for(T enumValue : enumType.getEnumConstants())
		{
			enumValues[i] = enumValue.name();
			i++;
		}

		return enumValues;
	}

	private int enumIndexOf(String enumValue, String[] enumValues)
	{
		return Arrays.asList(enumValues).indexOf(enumValue);
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
}
