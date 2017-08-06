package de.damarus.pixler;

import de.damarus.drawing.data.Composition;

public abstract class SimplePixlerListener implements PixlerManager.PixlerListener {

    @Override
    public void onRegistered(Composition comp, int activeLayer, int primaryColor) {
        onCompositionChanged(comp);
        onActiveLayerChanged(activeLayer);
        onColorChanged(primaryColor);
    }

    @Override
    public void onUnregistered() {

    }

    @Override
    public void onCompositionChanged(Composition composition) {

    }

    @Override
    public void onColorChanged(int color) {

    }

    @Override
    public void onActiveLayerChanged(int selectedLayer) {

    }

    @Override
    public void onLayerAdded(int addedIndex) {

    }

    @Override
    public void onLayerRemoved(int removedIndex) {

    }

    @Override
    public void onLayerPainted(int changedLayer) {

    }
}
