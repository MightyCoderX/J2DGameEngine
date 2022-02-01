package j2dgameengine.renderer;

import j2dgameengine.Window;
import j2dgameengine.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw
{
	private static int MAX_LINES = 5000;

	private static List<Line2D> lines = new ArrayList<>();
	// 6 floats per vertex, 2 vertices per line
	private static float[] vertexArray = new float[MAX_LINES * 6 * 2];

	private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

	private static int vaoID, vboID;

	private static boolean started = false;

	public static void start()
	{
		// Create VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create VBO and buffer some memory
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

		// Enable vertex array attributes
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		glLineWidth(10.0f);
	}

	public static void beginFrame()
	{
		if(!started)
		{
			start();
			started = true;
		}

		// Remove all the dead lines
		for(int i = 0; i < lines.size(); i++)
		{
			if(lines.get(i).beginFrame() > 0) continue;
			lines.remove(i);
			i--;
		}

	}

	public static void draw()
	{
		if(lines.isEmpty()) return;

		int index = 0;
		for(Line2D line : lines)
		{
			for(int i = 0; i < 2; i++)
			{
				Vector2f position = i == 0 ? line.getFrom() : line.getTo();
				Vector3f color = line.getColor();

				// Load position
				vertexArray[index] = position.x;
				vertexArray[index + 1] = position.y;
				vertexArray[index + 2] = -10.0f;

				// Load color
				vertexArray[index + 3] = color.x;
				vertexArray[index + 4] = color.y;
				vertexArray[index + 5] = color.z;

				index += 6;
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

		// Use shader
		shader.use();
		shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

		// Bind VAO and enable attributes
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// Draw the batch
		glDrawArrays(GL_LINES, 0, lines.size());

		// Unbind VAO and disable attributes
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		shader.detach();
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifeTime)
	{
		if(lines.size() >= MAX_LINES) return;
		lines.add(new Line2D(from, to, color, lifeTime));
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color)
	{
		addLine2D(from, to, color, 1);
	}

	public static void addLine2D(Vector2f from, Vector2f to)
	{
		addLine2D(from, to, new Vector3f(0, 1, 0), 1);
	}
}
