package com.yopachara.health.demo;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yopachara.health.demo.Model.FoodModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yopachara on 9/19/15 AD.
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final List<FoodModel.Foods> mPlayers;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView type;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.club);
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view.getRootView(), "position = " + getPosition() + " " + name.getText(), Snackbar.LENGTH_LONG).show();
        }
    }

    public FoodAdapter(Context context, ArrayList<FoodModel.Foods> dataset) {
        mPlayers = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FoodModel.Foods player = mPlayers.get(position);

        viewHolder.name.setText(player.getName());
        viewHolder.type.setText(player.getType());
    }

    @Override
    public int getItemCount() {
        return mPlayers.size();
    }

//    public void setModels(List<FoodModel.Foods> models) {
//        mPlayers = new ArrayList<>(models);
//    }

    public void animateTo(List<FoodModel.Foods> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        Log.d("animateTo",getItemCount()+"");
    }

    private void applyAndAnimateRemovals(List<FoodModel.Foods> newModels) {
        for (int i = mPlayers.size() - 1; i >= 0; i--) {
            final FoodModel.Foods model = mPlayers.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FoodModel.Foods> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final FoodModel.Foods model = newModels.get(i);
            if (!mPlayers.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<FoodModel.Foods> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final FoodModel.Foods model = newModels.get(toPosition);
            final int fromPosition = mPlayers.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public FoodModel.Foods removeItem(int position) {
        final FoodModel.Foods model = mPlayers.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, FoodModel.Foods model) {
        mPlayers.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FoodModel.Foods model = mPlayers.remove(fromPosition);
        mPlayers.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
