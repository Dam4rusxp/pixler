package de.damarus.drawing.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.gson.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class BitmapSerializer implements JsonDeserializer<Bitmap>, JsonSerializer<Bitmap> {

    @Override
    public Bitmap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String encodedData = jsonElement.getAsString();
        byte[] data = Base64.decode(encodedData, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public JsonElement serialize(Bitmap bitmap, Type type, JsonSerializationContext jsonSerializationContext) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] data = out.toByteArray();
        String encodedData = Base64.encodeToString(data, Base64.DEFAULT);
        return new JsonPrimitive(encodedData);
    }
}
