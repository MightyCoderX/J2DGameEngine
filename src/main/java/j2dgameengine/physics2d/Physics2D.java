package j2dgameengine.physics2d;

import j2dgameengine.GameObject;
import j2dgameengine.components.Transform;
import j2dgameengine.physics2d.components.Box2DCollider;
import j2dgameengine.physics2d.components.CircleCollider;
import j2dgameengine.physics2d.components.RigidBody2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class Physics2D
{
	private Vec2 gravity;
	private World world;

	private float physicsTime;
	private float physicsTimeStep;
	private int velocityIterations;
	private int positionIterations;

	public Physics2D()
	{
		this.gravity = new Vec2(0, -10.0f);
		this.world = new World(gravity);
		this.physicsTime = 0.0f;
		this.physicsTimeStep = 1.0f / 60.0f;
		this.velocityIterations = 8;
		this.positionIterations = 3;
	}

	public void add(GameObject go)
	{
		RigidBody2D rb = go.getComponent(RigidBody2D.class);
		if(rb != null && rb.getRawBody() == null)
		{
			Transform transform = go.transform;

			BodyDef bodyDef = new BodyDef();
			bodyDef.angle = (float) Math.toRadians(transform.rotation);
			bodyDef.position.set(transform.position.x, transform.position.y);
			bodyDef.angularDamping = rb.getAngularDamping();
			bodyDef.linearDamping = rb.getLinearDamping();
			bodyDef.fixedRotation = rb.isFixedRotation();
			bodyDef.bullet = rb.isContinuousCollision();

			switch (rb.getBodyType())
			{
				case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
				case STATIC -> bodyDef.type = BodyType.STATIC;
				case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
			}

			PolygonShape shape = new PolygonShape();
			CircleCollider circleCollider;
			Box2DCollider boxCollider;

			if((circleCollider = go.getComponent(CircleCollider.class)) != null)
			{
				shape.setRadius(circleCollider.getRadius());
			}
			else if((boxCollider = go.getComponent(Box2DCollider.class)) != null)
			{
				Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
				Vector2f offset = boxCollider.getOffset();
				Vector2f origin = new Vector2f(boxCollider.getOrigin());

				shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

				Vec2 pos = bodyDef.position;
				float xPos = pos.x + offset.x;
				float yPos = pos.y + offset.y;
				bodyDef.position.set(xPos, yPos);
			}

			Body body = this.world.createBody(bodyDef);
			rb.setRawBody(body);
			body.createFixture(shape, rb.getMass());
		}
	}

	public void destroyGameObject(GameObject go)
	{
		RigidBody2D rb = go.getComponent(RigidBody2D.class);
		if(rb != null && rb.getRawBody() != null)
		{
			world.destroyBody(rb.getRawBody());
			rb.setRawBody(null);
		}
	}

	public void update(float dt)
	{
		physicsTime += dt;

		if(physicsTime >= 0.0f)
		{
			physicsTime -= physicsTimeStep;
			world.step(physicsTimeStep, velocityIterations, positionIterations);
		}


	}
}
