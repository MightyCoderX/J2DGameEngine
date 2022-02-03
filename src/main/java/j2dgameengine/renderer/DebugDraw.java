package j2dgameengine.renderer;

import j2dgameengine.Window;
import j2dgameengine.util.AssetPool;
import j2dgameengine.util.JMath;
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

		glLineWidth(2.0f);
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
			if(lines.get(i).beginFrame() >= 0) continue;
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
		shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

		// Bind VAO and enable attributes
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// Draw the batch
		glDrawArrays(GL_LINES, 0, lines.size() * 2);

		// Unbind VAO and disable attributes
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		shader.detach();
	}

	// ===========================
	// Add Line2D methods
	// ===========================
	public static void addLine(Vector2f from, Vector2f to, Vector3f color, int lifeTime)
	{
		if(lines.size() >= MAX_LINES) return;
		lines.add(new Line2D(from, to, color, lifeTime));
	}

	public static void addLine(float x1, float y1, float x2, float y2, Vector3f color, int lifeTime)
	{
		addLine(new Vector2f(x1, y1), new Vector2f(x2, y2), color, lifeTime);
	}

	public static void addLine(Vector2f from, Vector2f to, Vector3f color)
	{
		addLine(from, to, color, 1);
	}

	public static void addLine(float x1, float y1, float x2, float y2, Vector3f color)
	{
		addLine(new Vector2f(x1, y1), new Vector2f(x2, y2), color);
	}

	public static void addLine(Vector2f from, Vector2f to)
	{
		addLine(from, to, new Vector3f(0, 1, 0), 1);
	}

	public static void addLine(float x1, float y1, float x2, float y2)
	{
		addLine(new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	// ===========================
	// Add Rect methods
	// ===========================
	public static void addRect(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifeTime)
	{
		Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2.0f));
		Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2.0f));

		Vector2f[] vertices = {
			new Vector2f(min.x, min.y),
			new Vector2f(min.x, max.y),
			new Vector2f(max.x, max.y),
			new Vector2f(max.x, min.y)
		};

		if(rotation != 0.0f)
		{
			for(Vector2f vert : vertices)
			{
				JMath.rotate(vert, rotation, center);
			}
		}

		addLine(vertices[0], vertices[1], color, lifeTime);
		addLine(vertices[1], vertices[2], color, lifeTime);
		addLine(vertices[2], vertices[3], color, lifeTime);
		addLine(vertices[3], vertices[0], color, lifeTime);
	}

	public static void addRect(Vector2f center, Vector2f dimensions, float rotation, Vector3f color)
	{
		addRect(center, dimensions, rotation, color, 1);
	}

	public static void addRect(Vector2f center, Vector2f dimensions, float rotation)
	{
		addRect(center, dimensions, rotation, new Vector3f(1, 0, 0));
	}

	public static void addRect(float centerX, float centerY, float width, float height, float rotation)
	{
		addRect(new Vector2f(centerX, centerY), new Vector2f(width, height), rotation);
	}

	public static void addRect(Vector2f center, Vector2f dimensions)
	{
		addRect(center, dimensions, 0, new Vector3f(1, 0, 0), 1);
	}

	public static void addRect(float centerX, float centerY, float width, float height)
	{
		addRect(new Vector2f(centerX, centerY), new Vector2f(width, height));
	}

	// ===========================
	// Add Circle methods
	// ===========================

	public static void addCircle(Vector2f center, float radius)
	{
		addCircle(center, radius, new Vector3f(1, 0, 0));
	}

	public static void addCircle(Vector2f center, float radius, Vector3f color)
	{

	}

	public static void addCircle(Vector2f center, float radius, Vector3f color, int lifeTime)
	{
		Vector2f[] points = new Vector2f[20];
		float increment = 360.0f / (float) points.length;
		float currentAngle = 0;

		for(int i = 0; i < points.length; i++)
		{
			Vector2f tmp = new Vector2f(0, radius);
			JMath.rotate(tmp, currentAngle, new Vector2f());
			points[i] = new Vector2f(tmp).add(center);

			if(i > 0)
			{
				addLine(points[i - 1], points[i], color, lifeTime);
			}

			currentAngle += increment;
		}

		addLine(points[points.length - 1], points[0], color, lifeTime);
	}
}
