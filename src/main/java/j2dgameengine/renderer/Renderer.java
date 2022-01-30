package j2dgameengine.renderer;

import j2dgameengine.components.SpriteRenderer;
import j2dgameengine.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer
{
	private final int MAX_BATCH_SIZE = 1000;
	private List<RenderBatch> batches;

	public Renderer()
	{
		this.batches = new ArrayList<>();
	}

	public void add(GameObject go)
	{
		SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
		if(sprite != null)
		{
			add(sprite);
		}
	}

	private void add(SpriteRenderer sprite)
	{
		boolean added = false;

		for(RenderBatch batch : batches)
		{
			if(batch.isFull() && batch.zIndex() != sprite.gameObject.zIndex()) continue;

			Texture texture = sprite.getTexture();
			if(texture == null || !batch.hasTexture(texture) || !batch.hasTextureRoom()) continue;

			batch.addSprite(sprite);
			added = true;
			break;
		}

		if(!added)
		{
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex());
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(sprite);
			Collections.sort(batches);
		}
	}

	public void render()
	{
		for(RenderBatch batch : batches)
		{
			batch.render();
		}
	}
}
