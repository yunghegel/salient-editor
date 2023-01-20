package ecs.components;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;


public class Entity3D {
    private ModelInstance model;
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    private boolean isCollider;
    private boolean isKilled;
    private Matrix4 transform;
public void setTransform(Matrix4 inTransform){

        transform = inTransform;
        updateModelTransform();

        Vector3 pos = new Vector3();
        Quaternion rotation = new Quaternion();
        transform.getTranslation(pos);
        transform.getRotation(rotation);
        setPosition(pos.x,pos.y,pos.z);
        setRotation(rotation.getYaw(), rotation.getPitch(), rotation.getRoll());

        }

public void kill(){
        isKilled = true;
        }

public boolean getIsKilled(){
        return isKilled;
        }

public boolean isCollider() {
        return isCollider;
        }

public void setIsCollider(boolean isCollider) {
        this.isCollider = isCollider;
        }

public void init(){

        }

public void update(float delta){

        }

public void dispose(){

        }

public Entity3D(){
        this((ModelInstance)null);
        }

public Entity3D(ModelInstance model) {
        this.model = model;
        position =  new Vector3();
        rotation = new Vector3();
        scale = new Vector3();
        setPosition(0,0,0);
        setRotation(0,0,0);
        setScale(1,1,1);
        init();
        }


public ModelInstance getModel(){
        return model;
        }

public void setModel(ModelInstance newModel){
        model = newModel;
        }

private void updateModelTransform(){
        if (model != null) {
        if (transform == null) {
        model.transform.idt();
        model.transform.set(position, new Quaternion().setEulerAngles(rotation.x, rotation.y, rotation.z), scale);
        } else {
        model.transform.idt().scale(scale.x,scale.y,scale.z).mul(transform);
        }
        }
        }

public Vector3 getPosition() {
        return position;
        }

public void setPosition(float x, float y, float z){
        setPosition(new Vector3(x,y,z));
        }

public void setPosition(Vector3 position) {
        this.position = position;
        updateModelTransform();
        }

public float getX() {
        return position.x;
        }

public void setX(float position) {
        this.position.x = position;
        updateModelTransform();
        }

public float getY() {
        return position.y;
        }

public void setY(float position) {
        this.position.y = position;
        updateModelTransform();
        }

public float getZ() {
        return position.z;
        }

public void setZ(float position) {
        this.position.z = position;
        updateModelTransform();
        }

public Vector3 getRotation() {
        return rotation;
        }

public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
        updateModelTransform();
        }

public void setRotation(float x, float y, float z){
        setRotation(new Vector3(x, y, z));
        }

public float getRX() {
        return rotation.x;
        }

public void setRX(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
        }


public float getRY() {
        return rotation.y;
        }

public void setRY(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
        }


public float getRZ() {
        return rotation.z;
        }

public void setRZ(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
        }

public Vector3 getScale() {
        return scale;
        }

public void setScale(Vector3 scale) {
        this.scale = scale;
        updateModelTransform();
        }

public void setScale(float x, float y, float z){
        setScale(new Vector3(x,y,z));
        }

        }