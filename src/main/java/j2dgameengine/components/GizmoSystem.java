package j2dgameengine.components;

import j2dgameengine.Window;
import j2dgameengine.listeners.KeyListener;

import static org.lwjgl.glfw.GLFW.*;

public class GizmoSystem extends Component
{
	private Spritesheet gizmoSprites;
	private int usingGizmo = 0;

	public GizmoSystem(Spritesheet gizmoSprites)
	{
		this.gizmoSprites = gizmoSprites;
	}

	@Override
	public void start()
	{
		gameObject.addComponent(new TranslateGizmo(gizmoSprites.getSprite(1),
				Window.getImGuiLayer().getPropertiesWindow()));
		gameObject.addComponent(new ScaleGizmo(gizmoSprites.getSprite(2),
				Window.getImGuiLayer().getPropertiesWindow()));
	}

	@Override
	public void editorUpdate(float dt)
	{
		if(usingGizmo == 0)
		{
			gameObject.getComponent(TranslateGizmo.class).setUsing();
			gameObject.getComponent(ScaleGizmo.class).setNotUsing();
		}
		else if(usingGizmo == 1)
		{
			gameObject.getComponent(TranslateGizmo.class).setNotUsing();
			gameObject.getComponent(ScaleGizmo.class).setUsing();
		}

		if(KeyListener.isKeyPressed(GLFW_KEY_E))
		{
			usingGizmo = 0;
		}
		else if(KeyListener.isKeyPressed(GLFW_KEY_R))
		{
			usingGizmo = 1;
		}
	}
}
