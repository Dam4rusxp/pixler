package de.damarus.drawing;

import android.graphics.Canvas;
import de.damarus.drawing.action.Action;
import de.damarus.drawing.action.PencilAction;
import de.damarus.drawing.data.Composition;

import java.util.ArrayDeque;
import java.util.Deque;

public class PixlerController {

    public static final int DPP = 20;

    private Composition composition;
    private Action currentAction;

    private int color = 0xff_00_00_00;

    private transient Deque<Action> actionStack = new ArrayDeque<>();
    private transient Deque<Action> redoStack = new ArrayDeque<>();

    public void initialize(Composition comp) {
        if (composition != null) throw new IllegalStateException();

        composition = comp;
    }

    public void initialize(int w, int h) {
        if (composition != null) throw new IllegalStateException();

        createStartupBitmap(w, h);
    }

    public boolean isInitialized() {
        return composition != null;
    }

    private void createStartupBitmap(int wholeWidth, int wholeHeight) {
        int width = wholeWidth / DPP;
        int height = wholeHeight / DPP;

        composition = Composition.createComposition(width, height);

        Canvas layerCanvas = new Canvas(composition.getActiveLayer());
        layerCanvas.drawRGB(128, 128, 128);
    }

    public void applyActionAt(float x, float y) {
        currentAction = new PencilAction();
        ((PencilAction) currentAction).setColor(getColor());
        currentAction.apply(composition.getActiveLayer(), x, y);
        getActionStack().push(currentAction);
        getRedoStack().clear();
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
