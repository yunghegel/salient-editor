package editor.graphics.scene;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTree;
import core.components.BulletComponent;
import core.components.SceneComponent;

public class SceneGraph
{

    VisTree tree;
    Node root;
    Node selectedNode;

    VisSelectBox<NodeType> nodeTypeSelectBox;

    public SceneGraph() {
        tree = new VisTree();
        root = new Node(new GameObject("root"));
        tree.add(root);

    }

    public void init() {
        nodeTypeSelectBox = new VisSelectBox<NodeType>();
        nodeTypeSelectBox.setItems(NodeType.values());
    }

    public void createListener() {
        nodeTypeSelectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event , Actor actor) {
                NodeType type = nodeTypeSelectBox.getSelected();
                if (type == NodeType.Spatial) {

                }
                else if (type == NodeType.Light) {

                }
                else if (type == NodeType.Scene) {
                    Node node = new Node(new SceneComponent("Scene"));
                }
                tree.add(selectedNode);
            }
        });
    }

    private void addNode(Node parent , Node child) {
        parent.add(child);
    }

    public void addChild(Node parent , GameObject go) {
        if (go instanceof SceneComponent) {
            SceneNode child = new SceneNode((SceneComponent) go);
            parent.go.addChild(go);
            parent.add(child);

        }
        else if (go instanceof BulletComponent) {
            PhysicsNode child = new PhysicsNode((BulletComponent) go);
            parent.go.addChild(go);
            parent.add(child);
        }
        else {
            Node child = new Node(go);
            parent.go.addChild(go);
            parent.add(child);
        }

    }

    enum NodeType
    {
        Spatial, Light, Scene, Physics
    }

    static class Node extends Tree.Node<VisTree.Node, GameObject, VisLabel>
    {

        public GameObject go;

        public Node(GameObject go) {
            super(new VisLabel(go.name));
            this.go = go;
        }

        public void applyTransform(Matrix4 transform) {
            go.setTransform(transform);
        }

    }

    static class SceneNode extends Tree.Node<VisTree.Node, SceneComponent, VisLabel>
    {

        public SceneComponent go;

        public SceneNode(SceneComponent go) {
            super(new VisLabel(go.name));
            this.go = go;
        }

        public void applyTransform(Matrix4 transform) {

            go.setTransform(transform);
        }

        public void applyTransformLocal(Matrix4 transform) {
            go.setTransformLocal(transform);
        }

    }

    static class PhysicsNode extends Tree.Node<VisTree.Node, BulletComponent, VisLabel>
    {

        public BulletComponent go;

        public PhysicsNode(BulletComponent go) {
            super(new VisLabel(go.name));
            this.go = go;
        }

        public void applyTransform(Matrix4 transform) {

            go.setTransform(transform);
        }

        public void applyTransformLocal(Matrix4 transform) {
            go.setTransformLocal(transform);
        }

    }

}
