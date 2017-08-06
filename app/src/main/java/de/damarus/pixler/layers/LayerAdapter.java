package de.damarus.pixler.layers;

import android.content.Context;
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
import de.damarus.pixler.SimplePixlerListener;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.ViewHolder> {

    private final Context context;

    private int selectedLayer = -1;
    private Composition comp;

    private final PixlerManager.PixlerListener listener = new SimplePixlerListener() {

        @Override
        public void onActiveLayerChanged(int newSelected) {
            if (newSelected != selectedLayer) {
                int oldSelected = selectedLayer;
                selectedLayer = newSelected;
                notifyItemChanged(oldSelected);
                notifyItemChanged(selectedLayer);
            }
        }

        @Override
        public void onLayerAdded(int addedIndex) {
            notifyItemInserted(addedIndex);
        }

        @Override
        public void onLayerRemoved(int removedIndex) {
            notifyItemRemoved(removedIndex);
            // Force update in onActiveLayerChanged
            selectedLayer = -1;
        }

        @Override
        public void onLayerPainted(int changedLayer) {
            if (changedLayer == -1) {
                notifyDataSetChanged();
            } else {
                notifyItemChanged(changedLayer);
            }
        }

        @Override
        public void onUnregistered() {
            comp = null;
            notifyDataSetChanged();
        }

        @Override
        public void onCompositionChanged(Composition newComposition) {
            if (newComposition != comp) {
                comp = newComposition;
                notifyDataSetChanged();
            }
        }
    };

    public LayerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        PixlerManager.getInstance().registerListener(listener);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        PixlerManager.getInstance().unregisterListener(listener);
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
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), comp.getLayer(position));
        drawable.setFilterBitmap(false);

        v.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        if (comp == null) return 0;
        return comp.getLayers().size();
    }

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
