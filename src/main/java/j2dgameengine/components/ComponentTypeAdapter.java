package j2dgameengine.components;

import com.google.gson.*;
import j2dgameengine.components.Component;

import java.lang.reflect.Type;

public class ComponentTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component>
{
	@Override
	public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
		result.add("properties", context.serialize(src, src.getClass()));

		return result;
	}

	@Override
	public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();

		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");

		try
		{
			return context.deserialize(element, Class.forName(type));
		}
		catch (ClassNotFoundException ex)
		{
			throw new JsonParseException("Unknown element type " + type, ex);
		}
	}


}
