package j2dgameengine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import j2dgameengine.GameObject;
import j2dgameengine.Window;

import java.util.List;

public class SceneHierarchyWindow
{
	private static String payloadDragDropType = "SceneHierarchy";

	public void imGui()
	{
		ImGui.begin("Scene Hierarchy");

		List<GameObject> gameObjects = Window.getScene().getGameObjects();
		int index = 0;
		for(GameObject obj : gameObjects)
		{
			if(!obj.doSerialization()) continue;

			boolean treeNodeOpen = doTreeNode(obj, index);
			if(treeNodeOpen)
			{
				ImGui.treePop();
			}

			index++;
		}

		ImGui.end();
	}

	private boolean doTreeNode(GameObject obj, int index)
	{
		ImGui.pushID(index);
		boolean treeNodeOpen = ImGui.treeNodeEx(
				obj.name,
				ImGuiTreeNodeFlags.DefaultOpen |
						ImGuiTreeNodeFlags.FramePadding |
						ImGuiTreeNodeFlags.OpenOnArrow |
						ImGuiTreeNodeFlags.SpanAvailWidth,
				obj.name
		);
		ImGui.popID();

		if(ImGui.beginDragDropSource())
		{
			ImGui.setDragDropPayload(payloadDragDropType, obj);
			ImGui.text(obj.name);
			ImGui.endDragDropSource();
		}

		if(ImGui.beginDragDropTarget())
		{
			Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
			if(payloadObj != null && payloadObj.getClass().isAssignableFrom(GameObject.class))
			{
				GameObject gameObject = (GameObject) payloadObj;
				System.out.println("Payload accepted '" + gameObject.name + "'");
			}

			ImGui.endDragDropTarget();
		}

		return treeNodeOpen;
	}
}
