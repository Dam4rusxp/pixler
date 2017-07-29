package de.damarus.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import de.damarus.drawing.action.Action;
import de.damarus.drawing.action.PencilAction;
import de.damarus.drawing.data.Composition;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;

public class PixlerController {

    public static final int DPP = 20;

    private Composition composition;
    private Action currentAction;

    private int activeLayerIndex = -1;

    private int color = 0xff_00_00_00;

    private transient Deque<Action> actionStack = new ArrayDeque<>();
    private transient Deque<Action> redoStack = new ArrayDeque<>();

    public void initialize(Composition comp) {
        checkState(!isInitialized());

        composition = comp;
    }

    public void initialize(int w, int h) {
        checkState(!isInitialized());

        int width = w / DPP;
        int height = h / DPP;

        composition = Composition.createComposition(width, height);
        activeLayerIndex++;

        Canvas layerCanvas = new Canvas(getActiveLayer());
        layerCanvas.drawRGB(128, 128, 128);
    }

    public boolean isInitialized() {
        return composition != null;
    }

    public void applyActionAt(float x, float y) {
        checkState(isInitialized());

        currentAction = new PencilAction();
        ((PencilAction) currentAction).setColor(getColor());
        currentAction.apply(getActiveLayer(), x, y);
        getActionStack().push(currentAction);
        getRedoStack().clear();
    }

    public void setActiveLayer(int newActiveLayer) {
        checkState(isInitialized());
        checkPositionIndex(newActiveLayer, composition.getLayers().size());

        activeLayerIndex = newActiveLayer;
    }

    /**
     * Adds a new layer above the active one, and makes it active.
     */
    public void addNewLayer() {
        checkState(isInitialized());

        composition.addLayer(++activeLayerIndex);
    }

    public void removeActiveLayer() {
        checkState(isInitialized());

        if (composition.getLayers().size() <= 1) return;

        composition.removeLayer(activeLayerIndex);
        activeLayerIndex = Math.min(activeLayerIndex, composition.getLayers().size() - 1);
    }

    public Bitmap getActiveLayer() {
        if (isInitialized()) return composition.getLayers().get(activeLayerIndex);
        return null;
    }

    public int getActiveLayerIndex() {
        return activeLayerIndex;
    }

    @SuppressWarnings("Duplicates")
    public boolean undoAction() {
        if (!getActionStack().isEmpty()) {
            Action undoAction = getActionStack().pop();
            getRedoStack().push(undoAction);
            undoAction.undoRedo();
            return true;
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    public boolean redoAction() {
        if (!getRedoStack().isEmpty()) {
            Action redoAction = getRedoStack().pop();
            getActionStack().push(redoAction);
            redoAction.undoRedo();
            return true;
        }
        return false;
    }

    public Deque<Action> getActionStack() {
        return actionStack;
    }

    public Deque<Action> getRedoStack() {
        return redoStack;
    }

    public Composition getComposition() {
        return composition;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
