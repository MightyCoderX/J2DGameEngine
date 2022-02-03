package scenes;

import j2dgameengine.Scene;

public abstract class SceneInitializer
{
	public abstract void init(Scene scene);
	public abstract void loadResources(Scene scene);
	public abstract void imGui();
}
