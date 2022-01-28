package j2dgameengine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
	private final float[] vertexArray = {
		//position                    //color
		 100.5f, 0.5f,    0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
		 0.5f,   100.5f,  0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
		 100.5f, 100.5f,  0.0f,       0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
		 0.5f,   0.5f,    0.0f,       1.0f, 1.0f, 0.0f, 1.0f // Bottom Left  3
	};

	// IMPORTANT: must be in counter-clockwise order
	private final int[] elementArray = {
		/*
		*	x      x
		*
		*   x      x
		* */
		2, 1, 0, // Top right triangle
		0, 1, 3  // Bottom left triangle
	};

	private int vaoID, vboID, eboID;

	private Shader defaultShader;

	public LevelEditorScene()
	{
		camera = new Camera(new Vector2f());
		defaultShader = new Shader("assets/shaders/default.glsl");
	}

	@Override
	public void init()
	{
		defaultShader.compileAndLink();
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());

		// ===========================================================
		// Generate VAO, VBO and EBO buffer objects, and send to GPU
		// ===========================================================
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create a float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();

		// Create VBO upload the vertex buffer
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

		// Add the vertex attribute pointers
		int positionsSize = 3;
		int colorSize = 4;
		int floatSizeBytes = 4;
		int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
		glEnableVertexAttribArray(1);
	}

	@Override
	public void update(float deltaTime)
	{
		defaultShader.use();

		// Bind the VAO that we're using
		glBindVertexArray(vaoID);

		// Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		// Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glBindVertexArray(0);

		defaultShader.detach();
	}
}
