package j2dgameengine.listeners;

import j2dgameengine.Camera;
import j2dgameengine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener
{
	private static MouseListener instance;

	double scrollX, scrollY;
	double xPos, yPos, worldX, worldY;
	private final boolean[] mouseButtonPressed = new boolean[9];
	private boolean isDragging;

	private int mouseButtonsDown;

	private Vector2f gameViewportPos;
	private Vector2f gameViewportSize;

	private MouseListener()
	{
		this.scrollX = 0;
		this.scrollY = 0;
		this.xPos = 0;
		this.yPos = 0;
		this.isDragging = false;

		this.mouseButtonsDown = 0;

		this.gameViewportPos = new Vector2f();
		this.gameViewportSize = new Vector2f();
	}

	public static MouseListener getInstance()
	{
		if(instance == null) instance = new MouseListener();

		return instance;
	}

	public static void mousePosCallback(long window, double xPos, double yPos)
	{
		if(getInstance().mouseButtonsDown > 0)
		{
			getInstance().isDragging = true;
		}

		getInstance().xPos = xPos;
		getInstance().yPos = yPos;

		getWorldPos();
	}

	public static void mouseButtonCallback(long window, int button, int action, int mods)
	{
		if(button >= getInstance().mouseButtonPressed.length) return;

		if(action == GLFW_PRESS)
		{
			getInstance().mouseButtonsDown++;

			if(button < getInstance().mouseButtonPressed.length)
			{
				getInstance().mouseButtonPressed[button] = true;
			}
		}
		else if(action == GLFW_RELEASE)
		{
			getInstance().mouseButtonsDown--;

			if(button < getInstance().mouseButtonPressed.length)
			{
				getInstance().mouseButtonPressed[button] = false;
				getInstance().isDragging = false;
			}
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
	}

	public static float getX()
	{
		return (float) getInstance().xPos;
	}

	public static float getY()
	{
		return (float) getInstance().yPos;
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

	public static void setGameViewportPos(Vector2f gameViewportPos)
	{
		getInstance().gameViewportPos.set(gameViewportPos);
	}

	public static void setGameViewportSize(Vector2f gameViewportSize)
	{
		getInstance().gameViewportSize.set(gameViewportSize);
	}

	public static boolean mouseButtonDown(int button)
	{
		if(button >= getInstance().mouseButtonPressed.length) return false;
		return getInstance().mouseButtonPressed[button];
	}

	public static Vector2f getWorldPos()
	{
		float currentX = getX() - getInstance().gameViewportPos.x;
		currentX = (2.0f * (currentX / getInstance().gameViewportSize.x)) - 1.0f;
		float currentY = (getY() - getInstance().gameViewportPos.y);
		currentY = (2.0f * (1.0f - (currentY / getInstance().gameViewportSize.y))) - 1;

		Camera camera = Window.getScene().getCamera();
		Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

		Matrix4f inverseView = new Matrix4f(camera.getInverseView());
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
		tmp.mul(inverseView.mul(inverseProjection));

		return new Vector2f(tmp.x, tmp.y);
	}

	public static float getWorldX()
	{
		return getWorldPos().x;
	}

	public static float getWorldY()
	{
		return getWorldPos().y;
	}

	public static Vector2f getScreenPos()
	{
		float currentX = getX() - getInstance().gameViewportPos.x;
		float currentY = getY() - getInstance().gameViewportPos.y;
		currentX = (currentX / getInstance().gameViewportSize.x) * 1366.0f;
		currentY = (currentY / getInstance().gameViewportSize.y) * 768.0f;
		currentY = 768.0f - currentY;

		return new Vector2f(currentX, currentY);
	}

	public static float getScreenX()
	{
		return getScreenPos().x;
	}

	public static float getScreenY()
	{
		return getScreenPos().y;
	}
}
