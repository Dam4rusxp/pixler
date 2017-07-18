package de.damarus.pixler.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class PencilAction extends Action {

    private Paint paint = new Paint();
    private int size = 1;

    @Override
    protected void doApply(Bitmap bitmap, float x, float y, RectF area) {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(area, paint);
    }

    public void setSize(int size) {
        if (size > 0) this.size = size;
    }

    public void setARGB(int a, int r, int g, int b) {
        paint.setARGB(a, r, g, b);
    }

    public void setColor(int color) {
        setARGB(
                (color >> 24) & 0xff,
                (color >> 16) & 0xff,
                (color >> 8) & 0xff,
                (color) & 0xff
        );
    }

    @Override
    protected float getPatchWidth() {
        return size;
    }

    @Override
    protected float getPatchHeight() {
        return size;
    }
}
