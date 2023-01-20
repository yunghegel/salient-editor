package util;

import com.google.gson.*;

import java.lang.reflect.Field;

public class JsonUtils
{

    public static JsonObject getJson(Object object) throws IllegalAccessException {
        JsonObject jsonObject = new JsonObject();

        Field[] fields = object.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = fields[i].get(object);
            if (value.getClass().isPrimitive() || value instanceof String) {
                jsonObject.addProperty(fields[i].getName() , value.toString());
            }
            else if (value.getClass().isArray()) {
                JsonArray jsonArray = new JsonArray();
                for (int j = 0; j < ( (Object[]) value ).length; j++) {
                    jsonArray.add(( (Object[]) value )[j].toString());
                }
                jsonObject.add(fields[i].getName() , jsonArray);
            }
            else {
                jsonObject.addProperty(fields[i].getName() , value.toString());
            }

        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonObject.toString());
        String finalJson = gson.toJson(jsonElement);
        return jsonObject;
    }

}
