package sys.io;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sys.Log;

import java.io.*;

public class RegistryWriter {

    ResourceRegistry resourceRegistry;
    ComponentRegistry componentRegistry;
    static JsonObject resourceObject = new JsonObject();
    static JsonArray componentObject = new JsonArray();
    static Writer componentWriter;
    static Writer resourceWriter;

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static {
        try {
             resourceWriter = new BufferedWriter(new FileWriter("resources.json"));
                componentWriter = new BufferedWriter(new FileWriter("components.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        componentObject.add("components");
    }
    Reader reader;

    public static RegistryWriter instance = new RegistryWriter();

    private RegistryWriter() {
        resourceRegistry = ResourceRegistry.instance;
        componentRegistry = ComponentRegistry.instance;
    }

    public RegistryWriter getInstance() {
        return instance;
    }

    public static void writeToResourceRegistry(ObjectMap<Class, Object> map) {
        JsonObject jsonObject = new JsonObject();
        for (Object object : map.values()) {
            jsonObject.add(object.getClass().getSimpleName(), gson.toJsonTree(object));
        }
    }

    public static void writeToResourceRegistry(Class clazz, String name, String path) throws IOException {
        Array<Triple<Class,String,String>> resources = ResourceRegistry.resources;
        JsonArray jsonArray = ResourceRegistry.getResourcesAsJson();

        JsonObject jsonObject = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File("resources.json");


            resourceWriter = new BufferedWriter(new FileWriter(file));

            resourceWriter.write(gson.toJson(jsonArray));
            resourceWriter.flush();









        Log.info("ResourceRegistry", "Added an instance of " + clazz.getSimpleName() + " to the resource registry");


    }

        public static void writeToComponentRegistry(Class clazz, String name, String path) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("path", path);
        jsonObject.addProperty("type", clazz.getSimpleName());

        gson.toJson(ResourceRegistry.resources, componentWriter);
        try {

            componentWriter.append(jsonObject.toString());

            componentWriter.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void dispose() {
        try {
            resourceWriter.close();
            componentWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteContents() {
        try {
            resourceWriter.write(" ");
            componentWriter.write(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readFromResourceRegistry(String path, Object object) {
        String json = gson.toJson(object);
    }

    public static void writeToComponentRegistry(String path, Object object) {
        String json = gson.toJson(object);
    }

    public static void readFromComponentRegistry(String path, Object object) {
        String json = gson.toJson(object);
    }
}