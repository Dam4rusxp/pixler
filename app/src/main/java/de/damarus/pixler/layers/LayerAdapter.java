package de.damarus.pixler.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.damarus.drawing.data.Composition;
import de.damarus.pixler.PixlerManager;
import de.damarus.pixler.R;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.ViewHolder> implements PixlerManager.PixlerListener {

    private final Context context;

    private int selectedLayer = -1;
    private Bitmap[] layers = new Bitmap[0];

    public LayerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.layer_recycler_vh, parent, false);
        ViewHolder h = new ViewHolder(v);

        // Setup view
        // ...

        return h;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == selectedLayer) {
            holder.vg.setBackgroundColor(ContextCompat.getColor(context, R.color.layersSelected));
        } else {
            holder.vg.setBackgroundColor(ContextCompat.getColor(context, R.color.layersUnselected));
        }

        ImageView v = holder.imgView;

        // Disable blurry scaling
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), layers[position]);
        drawable.setFilterBitmap(false);

        v.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return layers.length;
    }

    @Override
    public void onRegistered() {}

    @Override
    public void onActiveLayerChanged(int newLayer) {
        if (newLayer != selectedLayer) {
            int temp = selectedLayer;
            selectedLayer = newLayer;
            notifyItemChanged(temp);
            notifyItemChanged(selectedLayer);
        }
    }

    @Override
    public void onCompositionChanged(Composition composition, int layer) {
        if (composition != null) {
            Bitmap[] newLayers = new Bitmap[composition.getLayers().size()];
            composition.getLayers().toArray(newLayers);
            layers = newLayers;

            notifyDataSetChanged();
        }
    }

    @Override
    public void onColorChanged(int color) {}

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewGroup vg;

        @BindView(R.id.layerImg)
        ImageView imgView;

        ViewHolder(ViewGroup vg) {
            super(vg);

            this.vg = vg;

            ButterKnife.bind(this, vg);

            vg.setOnClickListener(view -> PixlerManager.getInstance().onLayerSelected(getAdapterPosition()));
        }
    }
}
