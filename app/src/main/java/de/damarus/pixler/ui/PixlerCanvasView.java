package de.damarus.pixler.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;
import de.damarus.drawing.data.Composition;
import de.damarus.pixler.PixlerManager;

public class PixlerCanvasView extends View implements PixlerManager.PixlerListener {

    public static final int DPP = 20;
    public static final int OVERSCROLL = 150;

    private OverScroller scroller;
    private float lastX, lastY;

    private Rect viewport = new Rect();
    private Paint drawPaint = new Paint();

    private Matrix drawMatrix = new Matrix();
    private Matrix inverseMatrix = new Matrix();

    private Composition currentComposition;

    public PixlerCanvasView(Context context) {
        super(context);
        whenConstructing();
    }

    public PixlerCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        whenConstructing();
    }

    public PixlerCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        whenConstructing();
    }

    public PixlerCanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        whenConstructing();
    }

    private void whenConstructing() {
        drawPaint.setAntiAlias(false);

        drawMatrix.postScale(DPP, DPP);

        scroller = new OverScroller(getContext());

        GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                scroller.forceFinished(true);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float[] touchPoint = {motionEvent.getX(), motionEvent.getY()};

                inverseMatrix.mapPoints(touchPoint);


                if (PixlerManager.getInstance().isInitialized()) {
                    // Do not handle if the tap was outside of the image
                    if (!getImageBounds().contains(touchPoint[0], touchPoint[1])) return false;

                    PixlerManager.getInstance().onSingleTap(touchPoint[0], touchPoint[1]);
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                drawMatrix.postTranslate(-distanceX, -distanceY);
                updateCamera();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                RectF window = getWindow();

                float[] bounds = getScrollBounds(window);

                lastX = window.left;
                lastY = window.top;

                scroller.fling(
                        (int) window.left,
                        (int) window.top,
                        (int) velocityX,
                        (int) velocityY,
                        (int) bounds[0],
                        (int) bounds[1],
                        (int) bounds[2],
                        (int) bounds[3]
                );
                // Let the view know that we want to start using computeScroll()
                postInvalidateOnAnimation();

                return true;
            }
        });
        // Remove the double tap listener reference, to get more reliable single tap triggers
        detector.setOnDoubleTapListener(null);

        ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();

                if (Float.isNaN(factor) || Float.isInfinite(factor)) return false;

                drawMatrix.postScale(factor, factor, detector.getFocusX(), detector.getFocusY());
                updateCamera();

                return true;
            }
        });

        setOnTouchListener((view, motionEvent) -> detector.onTouchEvent(motionEvent) | scaleDetector.onTouchEvent(motionEvent));
    }

    @Override
    public void computeScroll() {
        if (!scroller.computeScrollOffset()) return;

        int x = scroller.getCurrX();
        int y = scroller.getCurrY();

        float diffX = lastX - x;
        float diffY = lastY - y;

        RectF window = getWindow();
        float xSpace = viewport.width() - window.width();
        float ySpace = viewport.height() - window.height();

        if (xSpace > OVERSCROLL * 2) diffX = 0;
        if (ySpace > OVERSCROLL * 2) diffY = 0;

        drawMatrix.postTranslate(-diffX, -diffY);
        updateCamera(false);
        lastX = x;
        lastY = y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewport = new Rect(0, 0, w, h);

        // Those are both 0 when the view is displayed for the first time
        if (oldw == 0 && oldh == 0 && !isInEditMode()) {
//            longEdge = Math.max(w, h);

//            if (main != null) main.initialize(longEdge, longEdge);
        }

        updateCamera();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Background
        canvas.drawRGB(15, 15, 15);

        // Working Image
//        canvas.drawBitmap(picBitmap, null, viewport, drawPaint);
        canvas.clipRect(viewport);
        canvas.setMatrix(drawMatrix);
        if (currentComposition != null) {
            for (Bitmap layer : currentComposition.getLayers()) {
                canvas.drawBitmap(layer, viewport.left, viewport.top, drawPaint);
            }
        } else {
            canvas.drawRGB(128, 128, 128);
        }

        // UI...
    }

    private void updateCamera() {
        updateCamera(true);
    }

    private void updateCamera(boolean clamp) {
        if (clamp) {
            RectF window = getWindow();

            float[] bounds = getScrollBounds(window);

            float xSpace = viewport.width() - window.width();
            float ySpace = viewport.height() - window.height();

            if (xSpace > OVERSCROLL * 2) {
                // Center if there is too much space
                drawMatrix.postTranslate((xSpace / 2) - window.left, 0);
            } else {
                // Otherwise do normal clamping
                if (window.left < bounds[0]) drawMatrix.postTranslate(bounds[0] - window.left, 0);
                if (window.left > bounds[1]) drawMatrix.postTranslate(bounds[1] - window.left, 0);
            }

            if (ySpace > OVERSCROLL * 2) {
                drawMatrix.postTranslate(0, (ySpace / 2) - window.top);
            } else {
                if (window.top < bounds[2]) drawMatrix.postTranslate(0, bounds[2] - window.top);
                if (window.top > bounds[3]) drawMatrix.postTranslate(0, bounds[3] - window.top);
            }
        }

        drawMatrix.invert(inverseMatrix);
        postInvalidateOnAnimation();
    }

    private RectF getWindow() {
        RectF rWindow = getImageBounds();
        drawMatrix.mapRect(rWindow);
        return rWindow;
    }

    private float[] getScrollBounds(RectF window) {
        return new float[]{
                viewport.width() - window.width() - OVERSCROLL,
                OVERSCROLL,
                viewport.height() - window.height() - OVERSCROLL,
                OVERSCROLL};
    }

    private RectF getImageBounds() {
        if (currentComposition == null) return new RectF();

        return new RectF(
                0,
                0,
                currentComposition.getWidth(),
                currentComposition.getHeight());
    }

    @Override
    public void onActiveLayerChanged(int selectedLayer) {
    }

    @Override
    public void onCompositionChanged(Composition composition, int layer) {
        currentComposition = composition;
        invalidate();
    }

    @Override
    public void onColorChanged(int color) {
    }

    @Override
    public void onRegistered() {
    }
}
