package j2dgameengine.observers;

import j2dgameengine.GameObject;
import j2dgameengine.observers.events.Event;

public interface Observer
{
	void onNotify(GameObject gameObject, Event event);
}
