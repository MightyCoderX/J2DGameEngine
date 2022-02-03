package j2dgameengine.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import j2dgameengine.Window;
import j2dgameengine.listeners.MouseListener;
import j2dgameengine.observers.EventSystem;
import j2dgameengine.observers.events.Event;
import j2dgameengine.observers.events.EventType;
import org.joml.Vector2f;

public class GameViewWindow
{
	private float leftX, rightX, topY, bottomY;
	private boolean isPlaying = false;

	public void imGui()
	{
		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		ImGui.begin("Game Viewport",
				ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse |
				ImGuiWindowFlags.MenuBar);

		ImGui.beginMenuBar();
		if(ImGui.menuItem("Play", "", isPlaying, !isPlaying))
		{
			isPlaying = true;
			EventSystem.notify(null, new Event(EventType.GAME_ENGINE_START_PLAY));
		}

		if(ImGui.menuItem("Stop", "", !isPlaying, isPlaying))
		{
			isPlaying = false;
			EventSystem.notify(null, new Event(EventType.GAME_ENGINE_STOP_PLAY));
		}
		ImGui.endMenuBar();

		ImGui.popStyleVar(1);

		ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		leftX = windowPos.x + 10;
		bottomY = windowPos.y;
		rightX = windowPos.x + windowSize.x + 10;
		topY = windowPos.y + windowSize.y;

		System.out.println("Viewport Size " + windowSize);
		System.out.println("Viewport Pos " + windowPos);
		System.out.println();

		int texID = Window.getFramebuffer().getTextureId();
		ImGui.image(texID, windowSize.x, windowSize.y, 0, 1, 1, 0);

		MouseListener.setGameViewportPos(new Vector2f(windowPos.x + 10, windowPos.y));
		MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

		ImGui.end();
	}

	private ImVec2 getLargestSizeForViewport()
	{
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);

		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

		if(aspectHeight > windowSize.y)
		{
			// Switch to pillar-box mode
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getTargetAspectRatio();
		}

		return new ImVec2(aspectWidth, aspectHeight);
	}

	private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
	{
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);

		float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
		float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}

	public boolean getWantCaptureMouse()
	{
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
				MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
	}
}
