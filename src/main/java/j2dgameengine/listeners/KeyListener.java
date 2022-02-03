package j2dgameengine.listeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener
{
	private static KeyListener instance;
	private boolean[] keyPressed = new boolean[350];
	private boolean[] keyBeginPress = new boolean[350];

	private KeyListener() {}

	public static KeyListener getInstance()
	{
		if(instance == null) instance = new KeyListener();

		return instance;
	}

	public static void keyCallback(long window, int key, int scancode, int action, int mods)
	{
		if(action == GLFW_PRESS)
		{
			getInstance().keyPressed[key] = true;
			getInstance().keyBeginPress[key] = true;
		}
		else if(action == GLFW_RELEASE)
		{
			getInstance().keyPressed[key] = false;
			getInstance().keyBeginPress[key] = false;
		}
	}

	public static boolean isKeyPressed(int keyCode)
	{
		if(keyCode >= getInstance().keyPressed.length)
		{
			throw new IllegalArgumentException("Invalid key code '" + keyCode + "'");
		}

		return getInstance().keyPressed[keyCode];
	}

	public static boolean keyBeginPress(int keyCode)
	{
		boolean result = getInstance().keyBeginPress[keyCode];
		if(result)
		{
			getInstance().keyBeginPress[keyCode] = false;
		}

		return result;
	}
}
