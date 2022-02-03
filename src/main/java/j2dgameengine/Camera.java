package j2dgameengine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
	public Vector2f position;
	private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;

	private Vector2f projectionSize;

	private float zoom = 1.0f;

	public Camera(Vector2f position)
	{
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjection = new Matrix4f();
		this.inverseView = new Matrix4f();
		this.projectionSize = new Vector2f(6, 3);
		adjustProjection();
	}

	public void adjustProjection()
	{
		projectionMatrix.identity();
		projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f);

		projectionMatrix.invert(inverseProjection);
	}

	public Matrix4f getViewMatrix()
	{
		Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

		viewMatrix.identity();
		viewMatrix.lookAt(
			new Vector3f(position.x, position.y, 20.0f),
			cameraFront.add(position.x, position.y, 0.0f),
			cameraUp
		);

		viewMatrix.invert(inverseView);

		return viewMatrix;
	}

	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}

	public Matrix4f getInverseProjection()
	{
		return inverseProjection;
	}

	public Matrix4f getInverseView()
	{
		return inverseView;
	}

	public Vector2f getProjectionSize()
	{
		return projectionSize;
	}

	public float getZoom()
	{
		return zoom;
	}

	public void setZoom(float zoom)
	{
		this.zoom = zoom;
	}

	public void addZoom(float value)
	{
		this.zoom += value;
	}
}
