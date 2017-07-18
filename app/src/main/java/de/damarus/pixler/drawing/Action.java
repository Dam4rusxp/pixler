package de.damarus.pixler.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Action {

    private boolean applied;
    private Bitmap undoPatch;

    private Bitmap appliedBitmap;
    private Rect appliedArea;

    public final void apply(Bitmap bitmap, float x, float y) {
        appliedBitmap = bitmap;

        // Prepare undoRedo
        RectF patchArea = getPatchRect(x, y);
        Rect safeArea = new Rect();
        patchArea.roundOut(safeArea);

        undoPatch = Bitmap.createBitmap(bitmap, safeArea.left, safeArea.top, safeArea.width(), safeArea.height());
        appliedArea = safeArea;

        // Do the change
        doApply(bitmap, x, y, patchArea);

        applied = true;
    }

    protected abstract void doApply(Bitmap bitmap, float x, float y, RectF area);

    public final void undoRedo() {
        if (applied) {
            Bitmap realUndoPatch = undoPatch;
            undoPatch = Bitmap.createBitmap(appliedBitmap, appliedArea.left, appliedArea.top, appliedArea.width(), appliedArea.height());

            Canvas canvas = new Canvas(appliedBitmap);
            canvas.drawBitmap(realUndoPatch, appliedArea.left, appliedArea.top, null);
        }
    }

    protected float getPatchWidth() {
        return Float.MAX_VALUE;
    }

    protected float getPatchHeight() {
        return Float.MAX_VALUE;
    }

    private RectF getPatchRect(float x, float y) {
        return new RectF(
                Math.max(0, x - getPatchWidth() / 2),
                Math.max(0, y - getPatchHeight() / 2),
                Math.min(appliedBitmap.getWidth(), x + getPatchWidth() / 2),
                Math.min(appliedBitmap.getHeight(), y + getPatchHeight() / 2));
    }

    public boolean isApplied() {
        return applied;
    }
}
