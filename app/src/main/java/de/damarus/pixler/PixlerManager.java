package de.damarus.pixler;

import de.damarus.drawing.PixlerController;
import de.damarus.drawing.data.Composition;
import kotlin.NotImplementedError;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class PixlerManager {

    private static PixlerManager instance;

    private final PixlerController pixl = new PixlerController();

    private List<PixlerListener> listeners = new ArrayList<>();

    private PixlerManager() {
    }

    public static PixlerManager getInstance() {
        if (instance == null) {
            instance = new PixlerManager();
        }

        return instance;
    }

    public void createNewLayer() {
        checkState(isInitialized());

        pixl.addNewLayer();
        triggerActiveLayerChange();
    }

    public void removeLayer() {
        checkState(isInitialized());

        pixl.removeActiveLayer();
        triggerCompositionChange();
    }

    public void resumeSavedState() {
        throw new NotImplementedError();
    }

    /**
     * If this returns true, you can choose to pick up a state backup using {@link #resumeSavedState}.
     */
    public boolean canResumeState() {
        // TODO Implement state save and resume
        return false;
    }

    /**
     * If true, it's guaranteed that there is a valid PixlerController in initialized state, ready for working on.
     */
    public boolean isInitialized() {
        return pixl.isInitialized();
    }

    public void createNewPicture(int width, int height) {
        pixl.initialize(width, height);
        triggerCompositionChange();
    }

    public void onSingleTap(float x, float y) {
        pixl.applyActionAt(x, y);
        triggerCompositionChange();
    }

    public void undoAction() {
        boolean done = pixl.undoAction();
        if (done) triggerCompositionChange();
    }

    public void redoAction() {
        boolean done = pixl.redoAction();
        if (done) triggerCompositionChange();
    }

    public void changeColor(int color) {
        pixl.setColor(color);
        triggerColorChange();
    }

    public boolean isRegistered(PixlerListener listener) {
        return listeners.contains(listener);
    }

    public void registerListener(PixlerListener listener) {
        if (listeners.contains(checkNotNull(listener))) return;

        listeners.add(listener);
        listener.onRegistered();

        // Make the view display all the stuff we can give it, so it doesn't have to do that itself
        triggerActiveLayerChange(listener);
        triggerCompositionChange(listener);
        triggerColorChange(listener);
    }

    public void unregisterListener(PixlerListener listener) {
        listeners.remove(checkNotNull(listener));
    }

    private void triggerActiveLayerChange() {
        for (PixlerListener listener : listeners) {
            triggerActiveLayerChange(listener);
        }
    }

    private void triggerActiveLayerChange(PixlerListener listener) {
        checkNotNull(listener).onActiveLayerChanged(pixl.getActiveLayerIndex());
    }

    private void triggerCompositionChange() {
        for (PixlerListener listener : listeners) {
            triggerCompositionChange(listener);
        }
    }

    private void triggerCompositionChange(PixlerListener listener) {
        listener.onCompositionChanged(pixl.getComposition(), pixl.getActiveLayerIndex());
    }

    private void triggerColorChange() {
        for (PixlerListener listener : listeners) {
            triggerColorChange(listener);
        }
    }

    private void triggerColorChange(PixlerListener listener) {
        listener.onColorChanged(pixl.getColor());
    }

    public interface PixlerListener {

        void onRegistered();

        void onActiveLayerChanged(int selectedLayer);

        void onCompositionChanged(Composition composition, int layer);

        void onColorChanged(int color);
    }
}
