package de.damarus.drawing.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import de.damarus.drawing.PixlerController;

public class RetainedPixlerFragment extends Fragment {

    private PixlerController state;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public PixlerController getRetainedState() {
        return state;
    }

    public void setRetainedState(PixlerController state) {
        this.state = state;
    }
}
