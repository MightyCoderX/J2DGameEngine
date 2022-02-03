package j2dgameengine;

import com.google.gson.*;
import j2dgameengine.components.Component;
import j2dgameengine.components.Transform;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
	@Override
	public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();

		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.get("components").getAsJsonArray();

		GameObject go = new GameObject(name);

		for(JsonElement e : components)
		{
			Component c = context.deserialize(e, Component.class);
			go.addComponent(c);
		}

		go.transform = go.getComponent(Transform.class);

		return go;
	}
}
