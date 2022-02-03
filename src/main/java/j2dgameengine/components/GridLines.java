package j2dgameengine.components;

import j2dgameengine.Camera;
import j2dgameengine.Window;
import j2dgameengine.renderer.DebugDraw;
import j2dgameengine.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component
{
	@Override
	public void editorUpdate(float dt)
	{
		Camera camera = Window.getScene().getCamera();
		Vector2f cameraPos = camera.position;
		Vector2f projectionSize = camera.getProjectionSize();

		float firstX = ((int) (cameraPos.x / Settings.GRID_COLUMN_WIDTH) - 1) * Settings.GRID_COLUMN_WIDTH;
		float firstY = ((int) (cameraPos.y / Settings.GRID_ROW_HEIGHT) * Settings.GRID_ROW_HEIGHT);

		int numVLines = (int) ((projectionSize.x * camera.getZoom() / Settings.GRID_COLUMN_WIDTH) + 2);
		int numHLines = (int) ((projectionSize.y * camera.getZoom() / Settings.GRID_ROW_HEIGHT) + 2);

		float width = (int) (projectionSize.x * camera.getZoom()) + Settings.GRID_ROW_HEIGHT * 2;
		float height = (int) (projectionSize.y * camera.getZoom()) + Settings.GRID_COLUMN_WIDTH * 2;

		int maxLines = Math.max(numVLines, numHLines);
		Vector3f color = new Vector3f(0.2f);
		for(int i = 0; i < maxLines; i++)
		{
			float x = firstX + (Settings.GRID_COLUMN_WIDTH * i);
			float y = firstY + (Settings.GRID_ROW_HEIGHT * i);

			if(i < numVLines)
			{
				DebugDraw.addLine(x, firstY, x, firstY + height, color);
			}

			if(i < numHLines)
			{
				DebugDraw.addLine(firstX, y, firstX + width, y, color);
			}
		}
	}
}
