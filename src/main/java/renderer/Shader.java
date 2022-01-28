package renderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader
{
	private int shaderProgramID;

	private String vertexSource;
	private String fragmentSource;

	private String filePath;

	public Shader(String filePath)
	{
		this.filePath = filePath;

		try
		{
			String source = new String(Files.readAllBytes(Paths.get(filePath)));
			String[] splitString = source.split("(#type)( )+([a-zA-z]+)");

			// Find the first pattern after #type
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\r\n", index);

			String firstPattern = source.substring(index, eol).trim();

			// Find the second pattern after #type
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\r\n", index);

			String secondPattern = source.substring(index, eol);

			if(firstPattern.equals("vertex"))
			{
				vertexSource = splitString[1];
			}
			else if(firstPattern.equals("fragment"))
			{
				fragmentSource = splitString[1];
			}
			else
			{
				throw new IOException("Unexpected token '" + firstPattern + "'");
			}

			if(secondPattern.equals("vertex"))
			{
				vertexSource = splitString[2];
			}
			else if(secondPattern.equals("fragment"))
			{
				fragmentSource = splitString[2];
			}
			else
			{
				throw new IOException("Unexpected token '" + secondPattern + "'");
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert false : "Error could not open file for shader: '" + filePath + "'";
		}

		System.out.println(vertexSource);
		System.out.println(fragmentSource);
	}

	public void compileAndLink()
	{
		// ================================================
		// Compile and link shaders
		// ================================================

		int vertexID, fragmentID;

		// First load and compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);

		// Check for errors in compilation
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);

		if(success == GL_FALSE)
		{
			int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filePath + "'\n\tVertex shader compilation failed");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}

		// First load and compile the fragment shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);

		// Check for errors in compilation
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);

		if(success == GL_FALSE)
		{
			int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.err.println("ERROR: '" + filePath + "'\n\tFragment shader compilation failed");
			System.err.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}

		// Link shaders and check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);

		// Check for linking errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);

		if(success == GL_FALSE)
		{
			int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.err.println("ERROR: '" + filePath + "'\n\tShader linking failed");
			System.err.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}

	public void use()
	{
		// Bind shader program
		glUseProgram(shaderProgramID);
	}

	public void detach()
	{
		glUseProgram(0);
	}

	public void uploadMat4f(String varName, Matrix4f mat4)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);

		glUniformMatrix4fv(varLocation, false, matBuffer);
	}
}
