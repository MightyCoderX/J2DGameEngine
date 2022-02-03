package j2dgameengine.components;

import j2dgameengine.Camera;
import j2dgameengine.listeners.KeyListener;
import j2dgameengine.listeners.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component
{
	private Camera levelEditorCamera;
	private Vector2f clickOrigin;
	private boolean reset = false;

	private float lerpTime = 0.0f;
	private float dragDebounce = 0.032f;
	private float dragSensitivity = 30.0f;
	private float scrollSensitivity = 0.1f;

	public EditorCamera(Camera levelEditorCamera)
	{
		this.levelEditorCamera = levelEditorCamera;
		this.clickOrigin = new Vector2f();
	}

	@Override
	public void editorUpdate(float dt)
	{
		if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0)
		{
			clickOrigin = MouseListener.getWorldPos();
			dragDebounce -= dt;
		}
		else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
		{
			Vector2f mousePos = MouseListener.getWorldPos();
			Vector2f delta = new Vector2f(mousePos)
					.sub(clickOrigin);

			levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
			this.clickOrigin.lerp(mousePos, dt);
		}

		if(dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
		{
			dragDebounce = 0.032f;
		}

		if(MouseListener.getScrollY() != 0.0f)
		{
			float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
					1 / levelEditorCamera.getZoom());

			addValue *= -Math.signum(MouseListener.getScrollY());
			levelEditorCamera.addZoom(addValue);
		}

		if(KeyListener.isKeyPressed(GLFW_KEY_KP_0))
		{
			reset = true;
		}

		if(reset)
		{
			levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
			levelEditorCamera.setZoom(levelEditorCamera.getZoom() +
					((1.0f - levelEditorCamera.getZoom()) * lerpTime));
			lerpTime += 0.1f * dt;

			if(Math.abs(levelEditorCamera.position.x) <= 5.0f &&
					Math.abs(levelEditorCamera.position.y) <= 5.0f)
			{
				lerpTime = 0;
				levelEditorCamera.position.set(0, 0);
				levelEditorCamera.setZoom(1.0f);
				reset = false;
			}
		}
	}
}
