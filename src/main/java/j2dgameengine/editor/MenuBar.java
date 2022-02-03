package j2dgameengine.editor;

import imgui.ImGui;
import j2dgameengine.observers.EventSystem;
import j2dgameengine.observers.events.Event;
import j2dgameengine.observers.events.EventType;

public class MenuBar
{
	public void imGui()
	{
		ImGui.beginMenuBar();

		if(ImGui.beginMenu("File"))
		{
			if(ImGui.menuItem("Save", "Ctrl+S"))
			{
				EventSystem.notify(null, new Event(EventType.SAVE_LEVEL));
			}

			if(ImGui.menuItem("Load", "Ctrl+O"))
			{
				EventSystem.notify(null, new Event(EventType.LOAD_LEVEL));
			}

			ImGui.endMenu();
		}

		ImGui.endMenuBar();
	}
}
