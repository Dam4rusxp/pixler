package de.damarus.pixler.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clans.fab.FloatingActionButton;
import de.damarus.drawing.PixlerController;
import de.damarus.drawing.data.Composition;
import de.damarus.drawing.ui.PixlerCanvasView;
import de.damarus.pixler.R;

public class PixlerFragment extends Fragment implements PixlerController.PixlerListener {

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

    private PixlerController con;

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
        fabUndo.setOnClickListener(v -> con.undoAction());
        fabRedo.setOnClickListener(v -> con.redoAction());

        // Color Pickers
        fabPrimaryColor.setOnClickListener(v -> {
            DialogFragment frag = ColorPickerDialog.newInstance(con.getColor(), c -> {
                con.setColor(c);
            });

            frag.show(getActivity().getSupportFragmentManager(), "color-primary");
        });

        fabSecondaryColor.setOnClickListener(v -> {
            // Swap primary and secondary color
            int temp = fabSecondaryColor.getColorNormal();
            fabSecondaryColor.setColorNormal(con.getColor());
            fabSecondaryColor.setColorPressed(con.getColor());
            con.setColor(temp);
        });

        if (savedInstanceState != null) {
            int color = savedInstanceState.getInt("secondary-color");
            fabSecondaryColor.setColorNormal(color);
            fabSecondaryColor.setColorPressed(color);
        }

        if (con != null) {
            con.registerListener(canvas);
            onColorChanged(con.getColor());
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("secondary-color", fabSecondaryColor.getColorNormal());
    }

    @Override
    public void onCompositionChanged(Composition composition) {

    }

    @Override
    public void onColorChanged(int color) {
        if (fabPrimaryColor != null) {
            fabPrimaryColor.setColorNormal(color);
            fabPrimaryColor.setColorPressed(color);
        }
    }

    @Override
    public void onRegistered(PixlerController controller) {
        if (con != null) {
            con.unregisterListener(canvas);
            con.unregisterListener(this);
        }

        con = controller;
        if (canvas != null) con.registerListener(canvas);
        onColorChanged(con.getColor());
    }
}
