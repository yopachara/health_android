package com.yopachara.health.demo;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yopachara on 9/20/15 AD.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<HistoryModel.History> history;
    private Context mContext;
    FragmentManager fragmentManager;
    RecyclerView recyclerView;
    private HistoryModel historyModel;
    public List<Boolean> isExpanded;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mTextView;
        public TextView name;
        public TextView date;
        public TextView username;
        public TextView cal;
        public TextView pro;
        public TextView fat;
        public TextView carbo;
        public String id;
        private List<HistoryModel.History> history;
        private HistoryAdapter ha;
        String API = "http://pachara.me:3000";
        Dialog.Builder builder = null;
        ViewGroup expandableLayout;
        FragmentManager fragmentManager;
        CardView cv;

        private ViewHolder(View view, FragmentManager fragmentManagers) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            username = (TextView) view.findViewById(R.id.username);
            cal = (TextView) view.findViewById(R.id.cal);
            pro = (TextView) view.findViewById(R.id.pro);
            fat = (TextView) view.findViewById(R.id.fat);
            carbo = (TextView) view.findViewById(R.id.carbo);
            expandableLayout = (ViewGroup) itemView.findViewById(R.id.expandable_part_layout);
            cv = (CardView) view.findViewById(R.id.card_view);

            fragmentManager = fragmentManagers;


        }

        @Override
        public void onClick(View view) {
//            Log.d("History Short Click", getAdapterPosition() + view.toString());
            Toast.makeText(view.getContext(), "Short click position = " + getPosition(), Toast.LENGTH_SHORT).show();

            int position = getAdapterPosition();
            Log.d("onClick History " + position, isExpanded.get(position) + "");
//            if (!isExpanded.get(position)) {
//                expandableLayout.setVisibility(View.VISIBLE);
////                cv.setElevation(16);
//
////                cv.setLayoutParams(CardView.MarginLayoutParams("5dp"));
//            } else {
//                expandableLayout.setVisibility(View.GONE);
////                cv.setElevation(8);
//            }
//            isExpanded.set(position, !isExpanded.get(position));



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
            Toast.makeText(view.getContext(), "Delete food history = " + name.getText(), Toast.LENGTH_SHORT).show();
            createDialog(id, view);

            return true;
        }

        private void createDialog(final String id, View view) {
            boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

            builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    super.onPositiveActionClicked(fragment);
                    delHistory(id);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder) builder)
                    .message(name.getText() + " is add on " + date.getText())
                    .title("You want to delete food history?")
                    .positiveAction("Yes")
                    .negativeAction("No");
            DialogFragment fragment = DialogFragment.newInstance(builder);

            fragment.show(fragmentManager, null);

        }

        private void delHistory(String id) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API).build();
            HealthService api = restAdapter.create(HealthService.class);

            api.delHistoryID(id, new Callback<HistoryModel>() {
                @Override
                public void success(HistoryModel historyModel, Response response) {
                    Log.d("Success", "Delete history");
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }


    }

    public HistoryAdapter(Context context, ArrayList<HistoryModel.History> dataset, FragmentManager fragmentManagers) {
        history = dataset;
        mContext = context;
        fragmentManager = fragmentManagers;

        isExpanded = new ArrayList<>(history.size());
        for (int i = 0; i < history.size(); i++) {
            isExpanded.add(false);
        }
        Log.d("isExpanded Size", isExpanded.size() + "");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_history_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, fragmentManager);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        HistoryModel.History player = history.get(position);

        viewHolder.name.setText(player.getFoodname());
        viewHolder.date.setText(player.getDate());
        viewHolder.username.setText(player.getUsername());
        viewHolder.cal.setText(player.getCal());
        viewHolder.fat.setText(player.getFat());
        viewHolder.carbo.setText(player.getCarbo());
        viewHolder.pro.setText(player.getProtein());
        viewHolder.id = player.getId();
//        System.out.println("EXPANDED" + viewHolder. + " " + history.get(position).getDate());
        // TODO : Ensure this following method working correctly

        if (isExpanded.get(position)) {
            viewHolder.expandableLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.expandableLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
