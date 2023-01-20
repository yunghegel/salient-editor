package utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import physics.MotionState;

public class BulletUtils
{
	public static btConvexHullShape createConvexHullShape(final Model model, boolean optimize) {



		final Mesh mesh = model.meshes.get(0);
		final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(),
		                                                      mesh.getVertexSize());
		if (!optimize) return shape;
		// now optimize the shape
		final btShapeHull hull = new btShapeHull(shape);
		hull.buildHull(shape.getMargin());
		final btConvexHullShape result = new btConvexHullShape(hull);
		// delete the temporary shape
		shape.dispose();
		hull.dispose();
		return result;
	}

	public static btConvexHullShape createConvexHullShape(final Model model) {
		return createConvexHullShape(model, true);
	}



	public static btConvexHullShape createConvexHullShape(final ModelInstance modelInstance) {
		return createConvexHullShape(modelInstance.model, true);
	}



	public static btConvexHullShape createConvexHullShape(Mesh mesh, boolean optimize) {
		final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(),
				mesh.getVertexSize());
		if (!optimize) return shape;
		// now optimize the shape
		final btShapeHull hull = new btShapeHull(shape);
		hull.buildHull(shape.getMargin());
		final btConvexHullShape result = new btConvexHullShape(hull);
		// delete the temporary shape
		shape.dispose();
		hull.dispose();
		return result;
	}

	public static btRigidBody constructRigidBodyFromConvexHullShape(final ModelInstance model, boolean optimize, float mass) {

		final btConvexHullShape shape = createConvexHullShape(model.model, optimize);
		Vector3 localInertia = new Vector3();
		shape.calculateLocalInertia(mass, localInertia);
		final btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		final btRigidBody body = new btRigidBody(info);
		attachMotionStateToRigidBody(body, model);
		info.dispose();
		return body;
	}

	public static btRigidBody constructRigidBodyFromConvexHullShape(final ModelInstance model, float mass, btCollisionShape shape) {

		Vector3 localInertia = new Vector3();
		shape.calculateLocalInertia(mass, localInertia);
		final btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		final btRigidBody body = new btRigidBody(info);
		attachMotionStateToRigidBody(body, model);
		info.dispose();
		return body;
	}

	public static void attachMotionStateToRigidBody(final btRigidBody body, final ModelInstance model) {
		final MotionState bulletMotionState = new MotionState(model.transform);
		body.setMotionState(bulletMotionState);
	}

	//generate a btCollisionShape from a model
	/*public static btCollisionShape createCollisionShape(final Model model) {
		final Mesh mesh = model.meshes.get(0);
		final btGImpactMeshShape shape = new btGImpactMeshShape(mesh);
		shape.updateBound();
		return shape;
	}*/
	//create a compound shape from a model
	public static btCompoundShape createCompoundShape(final Model model) {
		final btCompoundShape compoundShape = new btCompoundShape();
		for (Mesh mesh: model.meshes) {
			btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), 12);
			compoundShape.addChildShape(new Matrix4(), shape);

		}
		return compoundShape;
	}

	//create a box shape equal to the bounding box of the model
	public static btBoxShape createBoxShapeFromBoundingBox(ModelInstance gameObject) {
		BoundingBox boundingBox = new BoundingBox();
		gameObject.calculateBoundingBox(boundingBox);
		//center the box
		Vector3 center = new Vector3();
		boundingBox.getCenter(center);

		//divide by 2 because the bounding box is centered on the origin
		btBoxShape shape = new btBoxShape(new Vector3(boundingBox.getWidth()/2, boundingBox.getHeight()/2, boundingBox.getDepth()/2));
		//set the position of the box



return shape;
	}

	public static btRigidBody.btRigidBodyConstructionInfo createRigidBodyConstructionInfo(float mass, btCollisionShape shape) {
		Vector3 localInertia = new Vector3();
		shape.calculateLocalInertia(mass, localInertia);
		final btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		return info;
	}


	public static btGImpactMeshShape createGImpactMeshShape(Model model) {
		btTriangleIndexVertexArray meshVertexArray = new btTriangleIndexVertexArray(model.meshParts);
		btGImpactMeshShape meshShape = new btGImpactMeshShape(meshVertexArray);
		meshShape.setLocalScaling(new Vector3(1, 1, 1));
		meshShape.updateBound();
		return meshShape;
	}
	public static btTriangleMeshShape createTriangleMeshShape(Model model) {
		btBvhTriangleMeshShape triangleShape;


		btTriangleIndexVertexArray vertexArray = new btTriangleIndexVertexArray(model.meshParts);

		triangleShape = new btBvhTriangleMeshShape(vertexArray, true);
		//triangleShape.setLocalScaling(new Vector3(39.3701f, 39.3701f, 39.3701f));
		return triangleShape;
	}

	public static Model buildCapsuleCharacter() {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		Material bodyMaterial = new Material();
		bodyMaterial.set(ColorAttribute.createDiffuse(Color.YELLOW));

		Material armMaterial = new Material();
		armMaterial.set(ColorAttribute.createDiffuse(Color.BLUE));

		// Build the cylinder body
		MeshPartBuilder builder = modelBuilder.part("body", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, bodyMaterial);
		CapsuleShapeBuilder.build(builder, .5f, 2f, 12);

		// Build the arms
		builder = modelBuilder.part("arms", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, armMaterial);
		BoxShapeBuilder.build(builder, .5f, 0, 0f, .25f,1f,.25f);
		BoxShapeBuilder.build(builder, -.5f, 0, 0f, .25f,1f,.25f);

		// Hat
		builder.setVertexTransform(new Matrix4().trn(0, 1f, 0));
		ConeShapeBuilder.build(builder, .75f, .5f, .75f, 12);

		// Left Eye
		builder.setVertexTransform(new Matrix4().trn(-.15f,.5f,.5f));
		SphereShapeBuilder.build(builder, .15f, .15f, .15f, 12,12);

		// Right Eye
		builder.setVertexTransform(new Matrix4().trn(.15f,.5f,.5f));
		SphereShapeBuilder.build(builder, .15f, .15f, .15f, 12,12);

		// Finish building
		return modelBuilder.end();
	}

	public static boolean collisionDetection(btCollisionDispatcher dispatcher,btCollisionObject collisionObject, btCollisionObject otherCollisionObject) {
		//collision detection (runs every frame)

		CollisionObjectWrapper co0 = new CollisionObjectWrapper(collisionObject);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(otherCollisionObject);
		btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
		ci.setDispatcher1(dispatcher);
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
		btPersistentManifold manifold = result.getPersistentManifold();
		btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper,manifold,1);

		btDispatcherInfo info = new btDispatcherInfo();



		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

		boolean isColliding = result.getPersistentManifold().getNumContacts() > 0;

		dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
		result.dispose();
		info.dispose();
		co1.dispose();
		co0.dispose();

		return isColliding;
	}

}
