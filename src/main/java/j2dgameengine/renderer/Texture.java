package j2dgameengine.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture
{
	private String filePath;
	private transient int texID;
	private int width, height;

//	public Texture(String filePath)
//	{
//		this.filePath = filePath;
//
//		// Generate texture on GPU
//		texID = glGenTextures();
//		glBindTexture(GL_TEXTURE_2D, texID);
//
//		// Set the texture parameters
//		// Repeat the image in both directions
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//		// When stretching the image, pixelate
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//		// When shrinking the image, pixelate
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//
//		IntBuffer width = BufferUtils.createIntBuffer(1);
//		IntBuffer height = BufferUtils.createIntBuffer(1);
//		IntBuffer channels = BufferUtils.createIntBuffer(1);
//
//		stbi_set_flip_vertically_on_load(true);
//
//		ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
//
//		if(image != null)
//		{
//			this.width = width.get(0);
//			this.height= height.get(0);
//
//			if(channels.get(0) == 3)
//			{
//				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0),
//						height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
//			}
//			else if(channels.get(0) == 4)
//			{
//				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0),
//						height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
//			}
//			else
//			{
//				assert false : "ERROR: (Texture) Unknown number of channels '" + channels.get(0) + "'";
//			}
//		}
//		else
//		{
//			assert false : "ERROR: (Texture) Could not load image '" + filePath + "'";
//		}
//
//		stbi_image_free(image);
//	}

	public Texture()
	{
		this.texID = -1;
		this.width = -1;
		this.height = -1;
	}

	public Texture(int width, int height)
	{
		this.filePath = "generated";
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	}

	public void load(String filePath)
	{
		this.filePath = filePath;

		// Generate texture on GPU
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);

		// Set the texture parameters
		// Repeat the image in both directions
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// When stretching the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		// When shrinking the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

		stbi_set_flip_vertically_on_load(true);

		ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

		if(image != null)
		{
			this.width = width.get(0);
			this.height= height.get(0);

			if(channels.get(0) == 3)
			{
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0),
						height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
			}
			else if(channels.get(0) == 4)
			{
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0),
						height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			}
			else
			{
				assert false : "ERROR: (Texture) Unknown number of channels '" + channels.get(0) + "'";
			}
		}
		else
		{
			assert false : "ERROR: (Texture) Could not load image '" + filePath + "'";
		}

		stbi_image_free(image);
	}

	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, texID);
	}

	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public String getFilePath()
	{
		return filePath;
	}

	public int getId()
	{
		return texID;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(!(o instanceof Texture oTex)) return false;

		return oTex.getId() == this.texID && oTex.width == this.width &&
				oTex.height == this.height && oTex.getFilePath().equals(this.filePath);
	}
}
