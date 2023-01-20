package backend.serialization;

import backend.tools.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class SceneSaveState
{

    static JsonObject sceneObject;
    static Gson gson;
    private static SceneSaveState instance = new SceneSaveState();
    Writer writer;
    JsonArray componentJsonArray;
    Log log;

    public SceneSaveState() {

        gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            writer = Files.newBufferedWriter(new File("project.json").toPath());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static SceneSaveState getInstance() {
        return instance;
    }

    public void writeProjectDataToJson() throws IOException {
        Writer writer = Files.newBufferedWriter(new File("project.json").toPath());
        JsonObject scene = createJsonStructure();
        gson.toJson(scene , writer);
        writer.close();
        //print out the json to the console
        Log.info("SceneSaveState" + "Project data saved to project.json");
    }

    public JsonObject createJsonStructure() {
        sceneObject = new JsonObject();
        componentJsonArray = new JsonArray();

        return sceneObject;
    }

    public void writeFieldToJson(Field field) {
        JsonObject fieldObject = new JsonObject();
        fieldObject.addProperty("name" , field.getName());
        fieldObject.addProperty("type" , field.getType().toString());
        gson.toJson(fieldObject , writer);

    }

    //create a json object with a root object scene, which contains an array of objects, each object is a model
    //each model object contains a model name, a
    // model path, and a model position

    public void loadSceneData() {

    }

}