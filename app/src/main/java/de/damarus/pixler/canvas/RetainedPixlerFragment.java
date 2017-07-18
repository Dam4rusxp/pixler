package de.damarus.pixler.canvas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class RetainedPixlerFragment extends Fragment {

    private PixlerState state;
    private int secondaryColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public PixlerState getRetainedState() {
        return state;
    }

    public void setRetainedState(PixlerState state) {
        this.state = state;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }
}
