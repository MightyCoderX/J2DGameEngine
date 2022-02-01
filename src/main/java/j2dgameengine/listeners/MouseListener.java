package j2dgameengine.listeners;

import j2dgameengine.Window;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener
{
	private static MouseListener instance;

	double scrollX, scrollY;
	double xPos, yPos, lastX, lastY;
	private final boolean[] mouseButtonPressed = new boolean[9];
	private boolean isDragging;

	private MouseListener()
	{
		this.scrollX = 0;
		this.scrollY = 0;
		this.xPos = 0;
		this.yPos = 0;
		this.lastX = 0;
		this.lastY = 0;
		this.isDragging = false;
	}

	public static MouseListener getInstance()
	{
		if(instance == null) instance = new MouseListener();

		return instance;
	}

	public static void mousePosCallback(long window, double xPos, double yPos)
	{
		getInstance().lastX = getInstance().xPos;
		getInstance().lastY = getInstance().yPos;
		getInstance().xPos = xPos;
		getInstance().yPos = yPos;

		for(int i = 0; i < getInstance().mouseButtonPressed.length; i++)
		{
			if(getInstance().mouseButtonPressed[i])
			{
				getInstance().isDragging = true;
				break;
			}
		}
	}

	public static void mouseButtonCallback(long window, int button, int action, int mods)
	{
		if(button >= getInstance().mouseButtonPressed.length) return;

		if(action == GLFW_PRESS)
		{
			getInstance().mouseButtonPressed[button] = true;
		}
		else if(action == GLFW_RELEASE)
		{
			getInstance().mouseButtonPressed[button] = false;
			getInstance().isDragging = false;
		}
	}

	public static void mouseScrollCallback(long window, double xOffset, double yOffset)
	{
		getInstance().scrollX = xOffset;
		getInstance().scrollY = yOffset;
	}

	public static void endFrame()
	{
		getInstance().scrollX = 0;
		getInstance().scrollY = 0;
		getInstance().lastX = getInstance().xPos;
		getInstance().lastY = getInstance().yPos;
	}

	public static float getX()
	{
		return (float) getInstance().xPos;
	}

	public static float getY()
	{
		return (float) getInstance().yPos;
	}

	public static float getWorldX()
	{
		float currentX = getX();
		currentX = (currentX / (float) Window.getWidth()) * 2 - 1;
		Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
		tmp.mul(Window.getCurrentScene().getCamera().getInverseProjection())
				.mul(Window.getCurrentScene().getCamera().getInverseView());

		return tmp.x;
	}

	public static float getWorldY()
	{
		float currentY = Window.getHeight() - getY();
		currentY = (currentY / (float) Window.getHeight()) * 2 - 1;
		Vector4f tmp = new Vector4f(0, currentY, 0, 1);
		tmp.mul(Window.getCurrentScene().getCamera().getInverseProjection())
				.mul(Window.getCurrentScene().getCamera().getInverseView());

		return tmp.y;
	}

	public static double getDx()
	{
		return getInstance().lastX - getInstance().xPos;
	}

	public static double getDy()
	{
		return getInstance().lastY - getInstance().yPos;
	}

	public static double getScrollX()
	{
		return getInstance().scrollX;
	}

	public static double getScrollY()
	{
		return getInstance().scrollY;
	}

	public static boolean isDragging()
	{
		return getInstance().isDragging;
	}

	public static boolean mouseButtonDown(int button)
	{
		if(button >= getInstance().mouseButtonPressed.length) return false;
		return getInstance().mouseButtonPressed[button];
	}
}
