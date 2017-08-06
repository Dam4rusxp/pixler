package de.damarus.pixler.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clans.fab.FloatingActionButton;
import de.damarus.pixler.PixlerManager;
import de.damarus.pixler.R;
import de.damarus.pixler.SimplePixlerListener;

public class PixlerFragment extends Fragment {

    @BindView(R.id.main_canvas)
    PixlerCanvasView canvas;

    @BindView(R.id.fab_undo)
    FloatingActionButton fabUndo;

    @BindView(R.id.fab_redo)
    FloatingActionButton fabRedo;

    @BindView(R.id.pixlerPrimaryColor)
    FloatingActionButton fabPrimaryColor;

    @BindView(R.id.pixlerSecondaryColor)
    FloatingActionButton fabSecondaryColor;

    private final PixlerManager.PixlerListener listener = new SimplePixlerListener() {

        @Override
        public void onColorChanged(int color) {
            fabPrimaryColor.setColorNormal(color);
            fabPrimaryColor.setColorPressed(color);
        }
    };

    // Required empty constructor
    public PixlerFragment() {

    }

    public static PixlerFragment createInstance() {
        PixlerFragment frag = new PixlerFragment();

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_pixler, container, false);
        ButterKnife.bind(this, root);

        // Undo/redo menu
        fabUndo.setOnClickListener(v -> PixlerManager.getInstance().undoAction());
        fabRedo.setOnClickListener(v -> PixlerManager.getInstance().redoAction());

        // Color Pickers
        fabPrimaryColor.setOnClickListener(v -> {
            DialogFragment frag = ColorPickerDialog.newInstance(
                    fabPrimaryColor.getColorNormal(),
                    c -> PixlerManager.getInstance().changeColor(c));

            frag.show(getActivity().getSupportFragmentManager(), "color-primary");
        });

        fabSecondaryColor.setOnClickListener(v -> {
            // Swap primary and secondary color
            int nowPrimary = fabSecondaryColor.getColorNormal();
            int nowSecondary = fabPrimaryColor.getColorNormal();

            fabSecondaryColor.setColorNormal(nowSecondary);
            fabSecondaryColor.setColorPressed(nowSecondary);
            PixlerManager.getInstance().changeColor(nowPrimary);
        });

        if (savedInstanceState != null) {
            int color = savedInstanceState.getInt("secondary-color");
            fabSecondaryColor.setColorNormal(color);
            fabSecondaryColor.setColorPressed(color);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        PixlerManager man = PixlerManager.getInstance();

        man.registerListener(listener);

        if (man.canResumeState()) {
            man.resumeSavedState();
        } else {
            // First time color init
            man.changeColor(ContextCompat.getColor(getContext(), R.color.pixlerPrimary));
            fabSecondaryColor.setColorNormal(ContextCompat.getColor(getContext(), R.color.pixlerSecondary));
            fabSecondaryColor.setColorPressed(ContextCompat.getColor(getContext(), R.color.pixlerSecondary));

            canvas.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (!man.isInitialized()) {
                        int longEdge = Math.max(canvas.getWidth(), canvas.getHeight());
                        man.createNewPicture(longEdge, longEdge);
                    }

                    v.removeOnLayoutChangeListener(this);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        PixlerManager.getInstance().unregisterListener(listener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("secondary-color", fabSecondaryColor.getColorNormal());
    }
}
