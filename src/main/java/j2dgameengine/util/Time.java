package j2dgameengine.util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time
{
	public static float timeStarted = System.nanoTime();

	public static float getTime()
	{
		return (float) (glfwGetTime());
	}
}
