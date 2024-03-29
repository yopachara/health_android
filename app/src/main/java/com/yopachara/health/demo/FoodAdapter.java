package com.yopachara.health.demo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yopachara on 9/19/15 AD.
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final List<FoodModel.Foods> mPlayers;
    private Context mContext;
    public List<Boolean> isExpanded;
    FragmentManager fragmentManager;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
        public TextView name;
        public TextView type;
        public TextView cal;
        public TextView pro;
        public TextView fat;
        public TextView carbo;
        Dialog.Builder builder = null;
        String API = "http://pachara.me:3000";
        FragmentManager fragmentManager;

        ViewGroup expandableLayout;


        public ViewHolder(View view, FragmentManager fragmentManagers) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.club);
            cal = (TextView) view.findViewById(R.id.foodcal);
            pro = (TextView) view.findViewById(R.id.foodprotein);
            fat = (TextView) view.findViewById(R.id.foodfat);
            carbo = (TextView) view.findViewById(R.id.foodcarbo);
            expandableLayout = (ViewGroup) itemView.findViewById(R.id.food_expandable_part_layout);
            fragmentManager = fragmentManagers;

        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view.getRootView(), "position = " + getPosition() + " " + name.getText(), Snackbar.LENGTH_LONG).show();

            int position = getAdapterPosition();
            Log.d("onClick History " + position, isExpanded.get(position) + "");
                        if (!isExpanded.get(position)) {
                expandableLayout.setVisibility(View.VISIBLE);

            } else {
                expandableLayout.setVisibility(View.GONE);
            }
            isExpanded.set(position, !isExpanded.get(position));



//            if (!isExpanded ){
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
//                this.name.setLayoutParams(lp);
//                this.isExpanded = true;
//            } else if (isExpanded){
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, 1f);
//                this.name.setLayoutParams(lp);
//                this.isExpanded = false;
//            }
        }
        @Override
        public boolean onLongClick(View view) {
            Log.d("History Long Click", getPosition() + view.toString());
            Toast.makeText(view.getContext(), "long click food = " + name.getText(), Toast.LENGTH_SHORT).show();
            createDialog();
            return true;
        }


        private void createDialog() {
            boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

            builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    super.onPositiveActionClicked(fragment);
                    postHistory("yopachara",mPlayers.get(getPosition()).getName(),mPlayers.get(getPosition()).getCal(),mPlayers.get(getPosition()).getProtein(),mPlayers.get(getPosition()).getCarbo(),mPlayers.get(getPosition()).getFat());
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder) builder)
                    .title("คุณต้องการที่จะเพิ่ม"+name.getText()+"หรือไม่?")
                    .positiveAction("ใช่")
                    .negativeAction("ไม่");
            DialogFragment fragment = DialogFragment.newInstance(builder);

            fragment.show(fragmentManager, null);

        }

        private void postHistory(String id,String name,float cal,float pro,float carbo, float fat) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API).build();
            HealthService api = restAdapter.create(HealthService.class);

            api.postHistory("yopachara", name, cal, pro, carbo, fat, new Callback<HistoryModel>() {
                @Override
                public void success(HistoryModel historyModel, Response response) {
                    Log.d("Success", response.getBody().toString());
//                    Fragment currentFragment = (Fragment) vp.getAdapter().instantiateItem(vp, 0);//gets current fragment
                    //now you have to cast it to your fragment, let's say it's name is SenapatiFragment
//                    ((HomeFragment) currentFragment).getRefreshDeco();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.toString());
                }
            });
        }
    }

    public FoodAdapter(Context context, ArrayList<FoodModel.Foods> dataset, FragmentManager fragmentManagers) {
        mPlayers = dataset;
        mContext = context;
        fragmentManager = fragmentManagers;


        isExpanded = new ArrayList<>(mPlayers.size());
        for (int i = 0; i < mPlayers.size(); i++) {
            isExpanded.add(false);
        }
        Log.d("isExpanded Size", isExpanded.size() + "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view,fragmentManager);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FoodModel.Foods player = mPlayers.get(position);

        viewHolder.name.setText(player.getName());
        viewHolder.type.setText("ประเภท " + player.getType());
        viewHolder.cal.setText("แคลลอรี่ "+player.getCal()+" แคล");
        viewHolder.fat.setText("ไขมัน "+player.getFat()+" กรัม");
        viewHolder.carbo.setText("คาร์โบไฮเดรต "+player.getCarbo()+" กรัม");
        viewHolder.pro.setText("โปรตีน "+player.getProtein()+" กรัม");

        if (isExpanded.get(position)) {
            viewHolder.expandableLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.expandableLayout.setVisibility(View.GONE);
        }
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
