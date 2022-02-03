package j2dgameengine.components;

import j2dgameengine.GameObject;
import j2dgameengine.Prefabs;
import j2dgameengine.Window;
import j2dgameengine.editor.PropertiesWindow;
import j2dgameengine.listeners.KeyListener;
import j2dgameengine.listeners.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component
{
	private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
	private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
	private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
	private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

	private GameObject xAxisObject;
	private GameObject yAxisObject;
	private SpriteRenderer xAxisSpriteRenderer;
	private SpriteRenderer yAxisSpriteRenderer;
	protected GameObject activeGameObject;

	private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6 / 80f);
	private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

	private float gizmoWidth = 16f / 80f;
	private float gizmoHeight = 48f / 80f;

	protected boolean xAxisActive = false;
	protected boolean yAxisActive = false;

	private boolean using = false;

	private PropertiesWindow propertiesWindow;

	public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		this.xAxisSpriteRenderer = this.xAxisObject.getComponent(SpriteRenderer.class);
		this.yAxisSpriteRenderer = this.yAxisObject.getComponent(SpriteRenderer.class);

		this.propertiesWindow = propertiesWindow;

		this.xAxisObject.addComponent(new NonPickable());
		this.yAxisObject.addComponent(new NonPickable());

		Window.getScene().addGameObjectToScene(this.xAxisObject);
		Window.getScene().addGameObjectToScene(this.yAxisObject);
	}

	@Override
	public void start()
	{
		this.xAxisObject.transform.rotation = 90.0f;
		this.yAxisObject.transform.rotation = 180.0f;

		this.xAxisObject.transform.zIndex = 100;
		this.yAxisObject.transform.zIndex = 100;

		this.xAxisObject.setNoSerialize();
		this.yAxisObject.setNoSerialize();
	}

	@Override
	public void update(float dt)
	{
		if(using)
		{
			this.setInactive();
		}


	}

	@Override
	public void editorUpdate(float dt)
	{
		if(!using) return;

		this.activeGameObject = this.propertiesWindow.getActiveGameObject();
		if(this.activeGameObject != null)
		{
			this.setActive();

			//TODO: Move this into its own keyEditorBinding component class
			if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
				KeyListener.keyBeginPress(GLFW_KEY_D))
			{
				GameObject newObject = this.activeGameObject.copy();
				Window.getScene().addGameObjectToScene(newObject);
				newObject.transform.position.add(0.1f, 0.1f);
				this.propertiesWindow.setActiveGameObject(newObject);
				return;
			}
			else  if(KeyListener.keyBeginPress(GLFW_KEY_DELETE))
			{
				activeGameObject.destroy();
				this.setInactive();
				this.propertiesWindow.setActiveGameObject(null);
				return;
			}
		}
		else
		{
			this.setInactive();
			return;
		}

		boolean xAxisHot = checkXHoverState();
		boolean yAxisHot = checkYHoverState();

		boolean leftClickDragging = MouseListener.isDragging()
				&& MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT);

		if((xAxisHot || xAxisActive) && leftClickDragging)
		{
			xAxisActive = true;
			yAxisActive = false;
		}
		else if((yAxisHot || yAxisActive) && leftClickDragging)
		{
			yAxisActive = true;
			xAxisActive = false;
		}
		else
		{
			xAxisActive = false;
			yAxisActive = false;
		}

		if(this.activeGameObject != null)
		{
			this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
			this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
			this.xAxisObject.transform.position.add(xAxisOffset);
			this.yAxisObject.transform.position.add(yAxisOffset);
		}
	}

	private void setActive()
	{
		this.xAxisSpriteRenderer.setColor(xAxisColor);
		this.yAxisSpriteRenderer.setColor(yAxisColor);
	}

	private void setInactive()
	{
		this.activeGameObject = null;
		this.xAxisSpriteRenderer.setColor(0, 0, 0, 0);
		this.yAxisSpriteRenderer.setColor(0, 0, 0, 0);
	}

	private boolean checkXHoverState()
	{
		Vector2f mousePos = MouseListener.getWorldPos();
		Vector2f axisPos = xAxisObject.transform.position;
		if (mousePos.x <= axisPos.x + (gizmoHeight / 2.0f) &&
			mousePos.x >= axisPos.x - (gizmoWidth / 2.0f) &&
			mousePos.y >= axisPos.y - (gizmoHeight / 2.0f)&&
			mousePos.y <= axisPos.y + (gizmoWidth / 2.0f))
		{
			xAxisSpriteRenderer.setColor(xAxisColorHover);
			return true;
		}

		xAxisSpriteRenderer.setColor(xAxisColor);
		return false;
	}

	private boolean checkYHoverState()
	{
		Vector2f mousePos = MouseListener.getWorldPos();
		Vector2f axisPos = yAxisObject.transform.position;
		if (mousePos.x <= axisPos.x + (gizmoWidth / 2.0f) &&
			mousePos.x >= axisPos.x - (gizmoWidth / 2.0f) &&
			mousePos.y <= axisPos.y + (gizmoHeight / 2.0f) &&
			mousePos.y >= axisPos.y - (gizmoHeight / 2.0f))
		{
			yAxisSpriteRenderer.setColor(yAxisColorHover);
			return true;
		}

		yAxisSpriteRenderer.setColor(yAxisColor);
		return false;
	}

	public void setUsing()
	{
		this.using = true;
	}

	public void setNotUsing()
	{
		this.using = false;
		this.setInactive();
	}
}
