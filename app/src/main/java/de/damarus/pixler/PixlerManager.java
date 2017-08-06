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

    private List<PixlerListener> pixlerListeners = new ArrayList<>();

    private PixlerManager() {
    }

    public static PixlerManager getInstance() {
        if (instance == null) {
            instance = new PixlerManager();
        }

        return instance;
    }

    public void onLayerSelected(int layer) {
        checkState(isInitialized());

        pixl.setActiveLayer(layer);
        triggerActiveLayerChange();
    }

    /**
     * If true, it's guaranteed that there is a valid PixlerController in initialized state, ready for working on.
     */
    public boolean isInitialized() {
        return pixl.isInitialized();
    }

    private void triggerActiveLayerChange() {
        for (PixlerListener listener : pixlerListeners) {
            checkNotNull(listener).onActiveLayerChanged(pixl.getActiveLayerIndex());
        }
    }

    public void createNewLayer() {
        checkState(isInitialized());

        pixl.addNewLayer();

        for (PixlerListener listener : pixlerListeners) listener.onLayerAdded(pixl.getActiveLayerIndex());
        triggerActiveLayerChange();
    }

    public void removeLayer() {
        checkState(isInitialized());

        int removedLayer = pixl.getActiveLayerIndex();
        boolean removed = pixl.removeActiveLayer();

        if (removed) {
            for (PixlerListener listener : pixlerListeners) listener.onLayerRemoved(removedLayer);
            triggerActiveLayerChange();
        }
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

    public void createNewPicture(int width, int height) {
        pixl.initialize(width, height);
        triggerCompositionChange();
        triggerActiveLayerChange();
    }


    private void triggerCompositionChange() {
        for (PixlerListener listener : pixlerListeners) {
            listener.onCompositionChanged(pixl.getComposition());
        }
    }

    public void onSingleTap(float x, float y) {
        pixl.applyActionAt(x, y);
        triggerActiveLayerPainted();
    }

    private void triggerActiveLayerPainted() {
        for (PixlerListener listener : pixlerListeners) {
            listener.onLayerPainted(pixl.getActiveLayerIndex());
        }
    }

    public void undoAction() {
        if (pixl.undoAction()) triggerAllLayersPainted();
    }

    private void triggerAllLayersPainted() {
        for (PixlerListener listener : pixlerListeners) {
            listener.onLayerPainted(-1);
        }
    }

    public void redoAction() {
        if (pixl.redoAction()) triggerAllLayersPainted();
    }

    public void changeColor(int color) {
        pixl.setColor(color);
        triggerColorChange();
    }

    private void triggerColorChange() {
        for (PixlerListener listener : pixlerListeners) {
            listener.onColorChanged(pixl.getColor());
        }
    }

    public boolean isRegistered(PixlerListener listener) {
        return pixlerListeners.contains(listener);
    }

    public void registerListener(PixlerListener listener) {
        if (pixlerListeners.contains(checkNotNull(listener))) return;

        pixlerListeners.add(listener);
        listener.onRegistered(pixl.getComposition(), pixl.getActiveLayerIndex(), pixl.getColor());
    }

    public void unregisterListener(PixlerListener listener) {
        boolean removed = pixlerListeners.remove(checkNotNull(listener));
        if (removed) listener.onUnregistered();
    }

    public interface PixlerListener {

        void onRegistered(Composition comp, int activeLayer, int primaryColor);

        void onUnregistered();

        void onCompositionChanged(Composition composition);

        void onColorChanged(int color);

        void onActiveLayerChanged(int selectedLayer);

        void onLayerAdded(int addedIndex);

        void onLayerRemoved(int removedIndex);

        /**
         * changedLayer is -1 if all layers should be updated
         */
        void onLayerPainted(int changedLayer);
    }
}
