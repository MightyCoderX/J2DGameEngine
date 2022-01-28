package j2dgameengine;

import util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
	private int width, height;
	private String title;

	private static Window window;

	private long glfwWindow;

	private static Scene currentScene;

	public float r, g, b, a;

	private Window()
	{
		this.width = 720;
		this.height = 480;
		this.title = "Mario";
		r = g = b = a = 1;
	}

	public static void changeScene(int newScene)
	{
		switch (newScene)
		{
			case 0 ->
			{
				currentScene = new LevelEditorScene();
				currentScene.init();
			}
			case 1 ->
			{
				currentScene = new LevelScene();
				currentScene.init();
			}
			default ->
			{
				assert false : "Unknown scene '" + newScene + "'";
			}
		}
	}

	public static Window getInstance()
	{
		if(window == null) window = new Window();

		return window;
	}

	public void run()
	{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		//Free the memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void init()
	{
		// Setup error callback
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if(!glfwInit())
		{
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

		if(glfwWindow == NULL)
		{
			throw new IllegalStateException("Failed to create the GLFW window");
		}

		// Set mouse callbacks
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

		// Set keyboard callbacks
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		// Make the OpenGL context current
		glfwMakeContextCurrent(glfwWindow);
		//Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(glfwWindow);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		changeScene(0);
	}

	public void loop()
	{
		float beginTime = Time.getTime();
		float endTime;
		float deltaTime = -1.0f;

		while(!glfwWindowShouldClose(glfwWindow))
		{
			// Poll events
			glfwPollEvents();

			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);

			if(deltaTime >= 0) currentScene.update(deltaTime);

			glfwSwapBuffers(glfwWindow);

			endTime = Time.getTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;
		}
	}
}
