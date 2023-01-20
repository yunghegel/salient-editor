package editor.tools;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import util.GeometryUtils;

public class MeshCulling {

        private static final Vector3 position = new Vector3();
        private static final Vector3 translation = new Vector3();

        public static void apply(SceneManager sceneManager){
            for(RenderableProvider scene : sceneManager.getRenderableProviders()){
                Scene sceneRoot = (Scene) scene;
                sceneRoot.modelInstance.transform.getTranslation(translation);
                apply(sceneManager.camera, sceneRoot.modelInstance.nodes);
            }
        }

        private static void apply(Camera camera, Iterable<Node> nodes) {
            for(Node node : nodes){
                apply(camera, node);
            }
        }

        private static void apply(Camera camera, Node node) {
            float d = camera.far * .4f; // TODO wrong camera.far
            boolean inBound = node.globalTransform.getTranslation(position).add(translation).dst2(camera.position) < d*d;
            //boolean inBound = node.translation.dst(camera.position) < camera.far; // TODO wrong camera.far
            GeometryUtils.enable(node, inBound);
            apply(camera, node.getChildren());
        }

    }

