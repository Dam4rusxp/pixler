package de.damarus.pixler.canvas;

import android.graphics.Bitmap;
import de.damarus.pixler.drawing.Action;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class PixlerState {

    private List<Bitmap> picLayers = new ArrayList<>();
    private int currentLayer = -1;

    private Deque<Action> actionStack = new ArrayDeque<>();
    private Deque<Action> redoStack = new ArrayDeque<>();

    private int color = 0xff_00_00_00;

    public List<Bitmap> getLayers() {
        return picLayers;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public Deque<Action> getActionStack() {
        return actionStack;
    }

    public Deque<Action> getRedoStack() {
        return redoStack;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) { this.color = color; }

    public void addLayer(Bitmap newLayer) {
        picLayers.add(++currentLayer, newLayer);
    }

    public void removeLayer() {
        if (picLayers.size() <= 1) throw new IllegalStateException("Can't remove last layer");

        picLayers.remove(currentLayer);
        currentLayer = Math.min(currentLayer, picLayers.size() - 1);
    }

    public void setLayers(List<Bitmap> layers) {
        if (layers.isEmpty()) throw new IllegalArgumentException("Can't set empty layers.");

        this.picLayers = new ArrayList<>(layers);
        currentLayer = Math.min(currentLayer, picLayers.size() - 1);
        if (currentLayer < 0) currentLayer = 0;
    }


    public void setCurrentLayer(int currentLayer) {
        if (currentLayer < 0 || currentLayer >= picLayers.size())
            throw new IllegalArgumentException("currentLayer is out of range");

        this.currentLayer = currentLayer;
    }
}
