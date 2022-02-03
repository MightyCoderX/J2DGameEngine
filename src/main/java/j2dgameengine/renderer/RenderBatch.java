package j2dgameengine.renderer;

import j2dgameengine.GameObject;
import j2dgameengine.components.Transform;
import j2dgameengine.components.SpriteRenderer;
import j2dgameengine.Camera;
import j2dgameengine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>
{
	// Vertex
	// =======
	// Pos               Color                         TexCoords       Tex id
	// float, float,     float, float, float, float,   float, float,   float

	private final int POS_SIZE = 2;
	private final int COLOR_SIZE = 4;
	private final int TEX_COORDS_SIZE = 2;
	private final int TEX_ID_SIZE = 1;
	private final int ENTITY_ID_SIZE = 1;

	private final int VERTEX_SIZE = 10;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
	private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;

	private final SpriteRenderer[] sprites;
	private final List<Texture> textures;
	private int spritesLen;
	private boolean isFull;
	private final float[] vertices;
	private final int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

	private int vaoID, vboID;
	private int maxBatchSize;
	private int zIndex;

	public RenderBatch(int maxBatchSize, int zIndex)
	{
		this.maxBatchSize = maxBatchSize;
		this.zIndex = zIndex;
		this.textures = new ArrayList<>();
		this.sprites = new SpriteRenderer[maxBatchSize];

		// 4 Vertices quads
		this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

		this.spritesLen = 0;
		this.isFull = false;
	}

	public void start()
	{
		// Generate and bind a VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Allocate space for vertices
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);


		// Create and upload the indices buffer
		int eboID = glGenBuffers();
		int[] indices = generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		// Enable buffer attribute pointers
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);

		glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
		glEnableVertexAttribArray(2);

		glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
		glEnableVertexAttribArray(3);

		glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
		glEnableVertexAttribArray(4);
	}

	public void addSprite(SpriteRenderer sprite)
	{
		// Get index and add renderObject
		int index = spritesLen;
		sprites[index] = sprite;
		this.spritesLen++;

		if(sprite.getTexture() != null && !textures.contains(sprite.getTexture()))
		{
			textures.add(sprite.getTexture());
		}

		// Add properties to local vertices array
		loadVertexProperties(index);

		if(spritesLen >= this.maxBatchSize)
		{
			this.isFull = true;
		}
	}

	public void render()
	{
		boolean rebufferData = false;
		for(int i = 0; i < spritesLen; i++)
		{
			SpriteRenderer spriteRenderer = sprites[i];
			if(spriteRenderer.isDirty())
			{
				loadVertexProperties(i);
				spriteRenderer.setClean();
				rebufferData = true;
			}
		}

		if(rebufferData)
		{
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}

		// For now, we will re-buffer all data every frame
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

		// Use shader
		Camera camera = Window.getScene().getCamera();
		Shader shader = Renderer.getCurrentShader();
		shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		shader.uploadMat4f("uView", camera.getViewMatrix());
		for(int i = 0; i < textures.size(); i++)
		{
			glActiveTexture(GL_TEXTURE0 + i + 1);
			textures.get(i).bind();
		}
		shader.uploadTextures("uTextures", texSlots);

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, spritesLen * 6, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		for(Texture texture : textures)
		{
			texture.unbind();
		}

		shader.detach();
	}

	public boolean destroyIfExists(GameObject go)
	{
		SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
		for(int i = 0; i < spritesLen; i++)
		{
			if(sprites[i] == spriteRenderer)
			{
				for(int j = i; j < spritesLen -1; j++)
				{
					sprites[j] = sprites[j + 1];
					sprites[j].setDirty();
				}
				spritesLen--;
				return true;
			}
		}

		return false;
	}

	private void loadVertexProperties(int index)
	{
		SpriteRenderer sprite = sprites[index];

		// Find offset within array (4 vertices per sprite)
		int offset = index * 4 * VERTEX_SIZE;

		Vector4f color = sprite.getColor();
		Vector2f[] texCoords = sprite.getTexCoords();

		int texID = 0;
		if(sprite.getTexture() != null)
		{
			for(int i = 0; i < textures.size(); i++)
			{
				if(textures.get(i).equals(sprite.getTexture()))
				{
					texID = i + 1;
					break;
				}
			}
		}

		Transform transform = sprite.gameObject.transform;

		boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
		Matrix4f transformMatrix = new Matrix4f().identity();

		if(isRotated)
		{

			transformMatrix.translate(transform.position.x, transform.position.y, 0f);
			transformMatrix.rotate((float)Math.toRadians(transform.rotation), 0, 0, 1);
			transformMatrix.scale(transform.scale.x, transform.scale.y, 1);
		}


		// Add vertices with the appropriate properties
		float xAdd = 0.5f;
		float yAdd = 0.5f;

		for (int i = 0; i < 4; i++)
		{
			if(i == 1)
			{
				yAdd = -0.5f;
			}
			else if(i == 2)
			{
				xAdd = -0.5f;
			}
			else if(i == 3)
			{
				yAdd = 0.5f;
			}

			Vector4f currentPos = new Vector4f(transform.position.x + (xAdd * transform.scale.x),
					transform.position.y + (yAdd * transform.scale.y), 0, 1);

			if(isRotated)
			{
				currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
			}

			// Load Position
			vertices[offset] = currentPos.x;
			vertices[offset + 1] = currentPos.y;

			// Load color
			vertices[offset + 2] = color.x;
			vertices[offset + 3] = color.y;
			vertices[offset + 4] = color.z;
			vertices[offset + 5] = color.w;

			// Load texture coordinates
			vertices[offset + 6] = texCoords[i].x;
			vertices[offset + 7] = texCoords[i].y;

			// Load texture id
			vertices[offset + 8] = texID;

			// Load entity id
			vertices[offset + 9] = sprite.gameObject.getUid() + 1;

			offset += VERTEX_SIZE;
		}
	}

	private int[] generateIndices()
	{
		// 6 indices per quad (3 per triangle)
		int[] elements = new int[6 * maxBatchSize];

		for(int i = 0; i < maxBatchSize; i++)
		{
			loadElementIndices(elements, i);
		}

		return elements;
	}

	private void loadElementIndices(int[] elements, int i)
	{
		int offsetArrayIndex = 6*i;
		int offset = 4*i;

		// 3, 2, 0, 0, 2, 1,       7, 6, 4, 4, 6, 5
		// Triangle 1
		elements[offsetArrayIndex] = offset + 3;
		elements[offsetArrayIndex + 1] = offset + 2;
		elements[offsetArrayIndex + 2] = offset;

		//Triangle 2
		elements[offsetArrayIndex + 3] = offset;
		elements[offsetArrayIndex + 4] = offset + 2;
		elements[offsetArrayIndex + 5] = offset + 1;
	}

	public boolean isFull()
	{
		return isFull;
	}

	public boolean hasTextureRoom()
	{
		return textures.size() < texSlots.length - 1;
	}

	public boolean hasTexture(Texture texture)
	{
		return textures.contains(texture);
	}

	public int zIndex()
	{
		return zIndex;
	}

	@Override
	public int compareTo(RenderBatch o)
	{
		return Integer.compare(zIndex, o.zIndex());
	}
}
