package j2dgameengine.editor;

import imgui.ImGui;
import j2dgameengine.GameObject;
import j2dgameengine.Scene;
import j2dgameengine.components.NonPickable;
import j2dgameengine.listeners.MouseListener;
import j2dgameengine.physics2d.components.Box2DCollider;
import j2dgameengine.physics2d.components.CircleCollider;
import j2dgameengine.physics2d.components.RigidBody2D;
import j2dgameengine.renderer.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow
{
	private GameObject activeGameObject;
	private PickingTexture pickingTexture;

	private float debounce = 0.2f;

	public PropertiesWindow(PickingTexture pickingTexture)
	{
		this.pickingTexture = pickingTexture;
	}

	public void update(float dt, Scene currentScene)
	{
		if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce <= 0.0f)
		{
			int x = (int)MouseListener.getScreenX();
			int y = (int)MouseListener.getScreenY();

			int gameObjectId = pickingTexture.readPixel(x, y);
			GameObject pickedObject = currentScene.getGameObject(gameObjectId);

			if(pickedObject == null && !MouseListener.isDragging())
			{
				activeGameObject = null;
			}

			if(pickedObject != null && pickedObject.getComponent(NonPickable.class) == null)
			{
				activeGameObject = pickedObject;
			}

			debounce = 0.2f;
		}
		debounce -= dt;
	}

	public void imGui()
	{
		if(activeGameObject != null)
		{
			ImGui.begin("Properties");

			if(ImGui.beginPopupContextWindow("ComponentAdder"))
			{
				if(ImGui.menuItem("Add RigidBody"))
				{
					if(activeGameObject.getComponent(RigidBody2D.class) == null)
					{
						activeGameObject.addComponent(new RigidBody2D());
					}
				}

				if(ImGui.menuItem("Add BoxCollider"))
				{
					if(activeGameObject.getComponent(Box2DCollider.class) == null &&
						activeGameObject.getComponent(CircleCollider.class) == null)
					{
						activeGameObject.addComponent(new Box2DCollider());
					}
				}

				if(ImGui.menuItem("Add CircleCollider"))
				{
					if(activeGameObject.getComponent(CircleCollider.class) == null &&
						activeGameObject.getComponent(Box2DCollider.class) == null)
					{
						activeGameObject.addComponent(new CircleCollider());
					}
				}

				ImGui.endPopup();
			}

			activeGameObject.imGui();
			ImGui.end();
		}

	}

	public GameObject getActiveGameObject()
	{
		return activeGameObject;
	}

	public void setActiveGameObject(GameObject activeGameObject)
	{
		this.activeGameObject = activeGameObject;
	}
}
