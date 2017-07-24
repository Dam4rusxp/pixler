package de.damarus.drawing.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public Bitmap getLayer(int layerIndex) {
        return layers.get(layerIndex);
    }

    public void addLayer(int at) {
        Bitmap newLayer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        layers.add(at, newLayer);
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
