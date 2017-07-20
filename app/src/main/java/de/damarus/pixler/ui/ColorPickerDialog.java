package de.damarus.pixler.ui;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import de.damarus.pixler.R;

public class ColorPickerDialog extends DialogFragment {

    @BindView(R.id.colorPicker)
    HSLColorPicker picker;

    @BindView(R.id.imageView)
    ImageView imageView;

    private int currentColor;
    private OnColorSelectedListener callback;

    public static ColorPickerDialog newInstance(int startColor, OnColorSelectedListener callback) {
        ColorPickerDialog dialog = new ColorPickerDialog();

        dialog.callback = callback;

        Bundle args = new Bundle();
        args.putInt("color", startColor);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.color_dialog, null);
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        picker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                updateColor(color);
            }
        });

        imageView.setOnClickListener(v -> {
            callback.onColorSelected(currentColor);
            dismiss();
        });

        Bundle args = getArguments();
        if (args != null && args.containsKey("color")) {
            picker.setColor(args.getInt("color"));
            updateColor(args.getInt("color"));
        }

        return builder.create();
    }

    private void updateColor(int color) {
        currentColor = color;
        imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int selectedColor);
    }
}
