package j2dgameengine.components;

import j2dgameengine.GameObject;
import j2dgameengine.Window;
import j2dgameengine.listeners.MouseListener;
import j2dgameengine.util.Settings;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component
{
	private GameObject holdingObject;

	public void pickupObject(GameObject go)
	{
		this.holdingObject = go;
		Window.getScene().addGameObjectToScene(go);
	}

	void place()
	{
		this.holdingObject = null;
	}

	@Override
	public void editorUpdate(float dt)
	{
		if(holdingObject == null) return;

		Vector2f position = holdingObject.transform.position;

		System.out.println("MouseListener -> WorldPos (" + MouseListener.getWorldX() + ", " + MouseListener.getWorldY() + ")");

		position.set(MouseListener.getWorldPos());

		position.set(
			((int)Math.floor(position.x / Settings.GRID_COLUMN_WIDTH) * Settings.GRID_COLUMN_WIDTH) + Settings.GRID_COLUMN_WIDTH / 2.0f,
			((int)Math.floor(position.y / Settings.GRID_ROW_HEIGHT) * Settings.GRID_ROW_HEIGHT) + Settings.GRID_ROW_HEIGHT / 2.0f
		);

		if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
		{
			place();
		}
	}
}
