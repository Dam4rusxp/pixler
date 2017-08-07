package de.damarus.drawing.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Composition {

    private int width = -1;
    private int height = -1;

    private List<Bitmap> layers = new ArrayList<>();

    private Composition() {
    }

    public static Composition createComposition(int w, int h) {
        Composition c = new Composition();
        c.width = w;
        c.height = h;
        c.addLayer(0);

        return c;
    }

    public static Composition createComposition(Bitmap bitmap) {
        checkNotNull(bitmap);

        Composition c = new Composition();
        c.width = bitmap.getWidth();
        c.height = bitmap.getHeight();

        Bitmap insertedBitmap = bitmap;
        if (!bitmap.isMutable()) insertedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        c.insertLayer(0, insertedBitmap);

        return c;
    }

    public Bitmap getLayer(int layerIndex) {
        return layers.get(layerIndex);
    }

    public void addLayer(int at) {
        Bitmap newLayer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        insertLayer(at, newLayer);
    }

    public void insertLayer(int at, Bitmap bitmap) {
        layers.add(at, bitmap);
    }

    public void removeLayer(int index) {
        if (layers.size() <= 1) throw new IllegalStateException("Can't remove last layer");

        layers.remove(index);
    }

    public List<Bitmap> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
