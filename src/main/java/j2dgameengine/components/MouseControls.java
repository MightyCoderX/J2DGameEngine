package j2dgameengine.components;

import j2dgameengine.GameObject;
import j2dgameengine.Window;
import j2dgameengine.listeners.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component
{
	private GameObject holdingObject;

	public void pickupObject(GameObject go)
	{
		this.holdingObject = go;
		Window.getCurrentScene().addGameObjectToScene(go);
	}

	void place()
	{
		this.holdingObject = null;
	}

	@Override
	public void update(float dt)
	{
		if(holdingObject == null) return;

		holdingObject.transform.position
				.set(MouseListener.getWorldX() - 16, MouseListener.getWorldY() - 16);

		if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
		{
			place();
		}
	}
}
