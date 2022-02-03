package j2dgameengine;

import j2dgameengine.listeners.KeyListener;
import j2dgameengine.listeners.MouseListener;
import j2dgameengine.observers.EventSystem;
import j2dgameengine.observers.Observer;
import j2dgameengine.observers.events.Event;
import j2dgameengine.observers.events.EventType;
import j2dgameengine.renderer.*;
import j2dgameengine.util.AssetPool;
import scenes.LevelEditorSceneInitializer;
import j2dgameengine.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import scenes.SceneInitializer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer
{
	private int width, height;
	private String title;

	private static Window window;
	private ImGuiLayer imGuiLayer;
	private Framebuffer framebuffer;
	private PickingTexture pickingTexture;
	private boolean runtimePlaying;

	private long glfwWindow;

	private static Scene currentScene;


	private Window()
	{
		this.width = 720;
		this.height = 360;
		this.title = "J2DGameEngine";
		this.runtimePlaying = false;

		EventSystem.addObserver(this);
	}

	public static void changeScene(SceneInitializer sceneInitializer)
	{
		if(currentScene != null)
		{
			currentScene.destroy();
		}

		getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
		currentScene = new Scene(sceneInitializer);
		currentScene.load();
		currentScene.init();
		currentScene.start();
	}

	public static Scene getScene()
	{
		return currentScene;
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

		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->
		{
			System.out.println("Window resized " + newWidth + " " + newHeight);
			setWidth(newWidth);
			setHeight(newHeight);
			currentScene.getCamera().adjustProjection();
		});

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

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		this.framebuffer = new Framebuffer(1366, 768);
		this.pickingTexture = new PickingTexture(1366, 768);
		glViewport(0, 0, 1366, 768);

		imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
		imGuiLayer.init();

		changeScene(new LevelEditorSceneInitializer());
	}

	public void loop()
	{
		float beginTime = Time.getTime();
		float endTime;
		float dt = -1.0f;

		Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
		Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

		while(!glfwWindowShouldClose(glfwWindow))
		{
			// Poll events
			glfwPollEvents();

			// Render pass 1. Render to picking texture
			glDisable(GL_BLEND);
			pickingTexture.enableWriting();
			glViewport(0, 0, 1366, 768);
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			Renderer.bindShader(pickingShader);
			currentScene.render();

			pickingTexture.disableWriting();
			glEnable(GL_BLEND);

			// Render pass 2. Render actual game
			DebugDraw.beginFrame();

			framebuffer.bind();
			glClearColor(1, 1, 1, 1);
			glClear(GL_COLOR_BUFFER_BIT);

			if(dt >= 0)
			{
				DebugDraw.draw();
				Renderer.bindShader(defaultShader);
				if(runtimePlaying)
				{
					currentScene.update(dt);
				}
				else
				{
					currentScene.editorUpdate(dt);
				}
				currentScene.render();
			}
			framebuffer.unbind();

			imGuiLayer.update(dt, currentScene);
			glfwSwapBuffers(glfwWindow);

			MouseListener.endFrame();

			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}

	public static void setWidth(int width)
	{
		getInstance().width = width;
	}

	public static void setHeight(int height)
	{
		getInstance().height = height;
	}

	private static int[] getWindowSize()
	{
		int[] w = new int[1];
		int[] h = new int[1];
		glfwGetWindowSize(getInstance().glfwWindow, w, h);

		return new int[] { w[0], h[0] };
	}

	public static int getWidth()
	{
		return getWindowSize()[0];
	}

	public static int getHeight()
	{
		return getWindowSize()[1];
	}

	public static Framebuffer getFramebuffer()
	{
		return getInstance().framebuffer;
	}

	public static float getTargetAspectRatio()
	{
		return 16.0f / 9.0f;
	}

	public static ImGuiLayer getImGuiLayer()
	{
		return getInstance().imGuiLayer;
	}

	@Override
	public void onNotify(GameObject gameObject, Event event)
	{
		switch (event.type)
		{
			case GAME_ENGINE_START_PLAY ->
			{
				this.runtimePlaying = true;
				currentScene.save();
				Window.changeScene(new LevelEditorSceneInitializer());
			}
			case GAME_ENGINE_STOP_PLAY ->
			{
				this.runtimePlaying = false;
				Window.changeScene(new LevelEditorSceneInitializer());
			}
			case LOAD_LEVEL -> Window.changeScene(new LevelEditorSceneInitializer());
			case SAVE_LEVEL -> currentScene.save();
		}
	}
}
