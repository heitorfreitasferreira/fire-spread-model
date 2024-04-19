package utils;

import com.beust.jcommander.IStringConverter;
import com.google.gson.JsonObject;

public class JsonConverter implements IStringConverter<JsonObject> {
    public JsonObject convert(String path) {
        var jsonHandler = new JsonFileHandler();
        return jsonHandler.readJson(path);
    }
}
