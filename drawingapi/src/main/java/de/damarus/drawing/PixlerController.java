package de.damarus.drawing;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import de.damarus.drawing.action.Action;
import de.damarus.drawing.action.PencilAction;
import de.damarus.drawing.data.Composition;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PixlerController {

    public static final int DPP = 20;

    private Composition composition;
    private Action currentAction;

    private int color = 0xff_00_00_00;

    private transient Deque<Action> actionStack = new ArrayDeque<>();
    private transient Deque<Action> redoStack = new ArrayDeque<>();

    private List<PixlerListener> listeners = new ArrayList<>();

    public void initialize(int w, int h) {
        if (composition != null) return;

        createStartupBitmap(w, h);
    }

    private void createStartupBitmap(int wholeWidth, int wholeHeight) {
        int width = wholeWidth / DPP;
        int height = wholeHeight / DPP;

        composition = Composition.createComposition(width, height);

        Canvas layerCanvas = new Canvas(composition.getActiveLayer());
        layerCanvas.drawRGB(128, 128, 128);

        triggerColorChange();
        triggerCompositionChange();
    }

    public void registerListener(@NonNull PixlerListener listener) {
        if (listeners.contains(checkNotNull(listener))) return;

        listeners.add(listener);
        listener.onRegistered(this);
    }

    public void unregisterListener(@NonNull PixlerListener listener) {
        listeners.remove(checkNotNull(listener));
    }

    public void applyActionAt(float x, float y) {
        currentAction = new PencilAction();
        ((PencilAction) currentAction).setColor(getColor());
        currentAction.apply(composition.getActiveLayer(), x, y);
        getActionStack().push(currentAction);
        getRedoStack().clear();

        triggerCompositionChange();
    }

    @SuppressWarnings("Duplicates")
    public void undoAction() {
        if (!getActionStack().isEmpty()) {
            Action undoAction = getActionStack().pop();
            getRedoStack().push(undoAction);
            undoAction.undoRedo();

            triggerCompositionChange();
        }
    }

    @SuppressWarnings("Duplicates")
    public void redoAction() {
        if (!getRedoStack().isEmpty()) {
            Action redoAction = getRedoStack().pop();
            getActionStack().push(redoAction);
            redoAction.undoRedo();

            triggerCompositionChange();
        }
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

    public void setComposition(Composition comp) {
        this.composition = comp;
        triggerCompositionChange();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        triggerColorChange();
    }

    private void triggerCompositionChange() {
        for (PixlerListener listener : listeners) {
            listener.onCompositionChanged(getComposition());
        }
    }

    private void triggerColorChange() {
        for (PixlerListener listener : listeners) {
            listener.onColorChanged(getColor());
        }
    }

    public interface PixlerListener {

        void onCompositionChanged(Composition composition);

        void onColorChanged(int color);

        void onRegistered(PixlerController controller);
    }
}
