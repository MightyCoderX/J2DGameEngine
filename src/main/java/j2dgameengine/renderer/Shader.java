package j2dgameengine.renderer;

import org.joml.*;
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
	private boolean beingUsed = false;

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
		if(beingUsed) return;
		// Bind shader program
		glUseProgram(shaderProgramID);
		beingUsed = true;
	}

	public void detach()
	{
		glUseProgram(0);
		beingUsed = false;
	}

	public void uploadMat4f(String varName, Matrix4f matrix)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);

		use();

		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		matrix.get(matBuffer);

		glUniformMatrix4fv(varLocation, false, matBuffer);
	}

	public void uploadMat3f(String varName, Matrix3f matrix)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();

		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		matrix.get(matBuffer);

		glUniformMatrix3fv(varLocation, false, matBuffer);
	}

	public void uploadVec4f(String varName, Vector4f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}

	public void uploadVec3f(String varName, Vector3f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}

	public void uploadVec2f(String varName, Vector2f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}

	public void uploadFloat(String varName, float value)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1f(varLocation, value);
	}

	public void uploadInt(String varName, int value)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, value);
	}

	public void uploadIntArray(String varName, int[] values)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1iv(varLocation, values);
	}

	public void uploadTexture(String varName, int slot)
	{
		uploadInt(varName, slot);
	}

	public void uploadTextures(String varName, int[] texSlots)
	{
		uploadIntArray(varName, texSlots);
	}
}
