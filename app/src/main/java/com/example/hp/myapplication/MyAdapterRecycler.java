package com.example.hp.myapplication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import model.FoodStore;


public class MyAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private ILoadmore iLoadmore;
        private boolean isLoading;
        private Activity activity;
        private List<FoodStore> storeList;
        private int visiableThreshold = 5;
        private int lastVisibleItem, totalItemCount;


        public MyAdapterRecycler(Activity activity, List<FoodStore> storeList, RecyclerView recyclerView) {
            this.activity = activity;
            this.storeList = storeList;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    System.out.println(" recycle totalItemCount: "+ totalItemCount);
                    System.out.println(" recyvle lastVisibleItem: "+ lastVisibleItem);
                    System.out.println(" recyvle visiableThreshold: "+ visiableThreshold);
                    if(!isLoading && totalItemCount <= (lastVisibleItem + visiableThreshold)){
                        if(iLoadmore !=null){
                            iLoadmore.onLoadMore();
                            isLoading = true;
                        }
                    }
                }
            });
        }

        public void setLoadMore(ILoadmore loadMore){
            this.iLoadmore = loadMore;
        }

        @Override
        public int getItemViewType(int position) {
            return storeList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.food_listview, viewGroup, false);
                final RecyclerView.ViewHolder holder = new FoodViewHolder(view);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "vl item", Toast.LENGTH_SHORT).show();
                    }
                });
                return holder;
            } else if (i == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.content_loading, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder instanceof FoodViewHolder){
                FoodStore foodStore = storeList.get(i);
                FoodViewHolder foodViewHolder = (FoodViewHolder) viewHolder;
                try {
                    new LoadImageFood(foodViewHolder.icon).execute(foodStore.getIconUrl());
                    foodViewHolder.foodName.setText(foodStore.getName());
                    foodViewHolder.address.setText(foodStore.getAddress());
                    if(foodStore.getInernationalPhonenumber().trim().equals("")){
                        foodViewHolder.phone.setText(foodStore.getPhonenumber());
                    }else{
                        foodViewHolder.phone.setText(foodStore.getPhonenumber() + " \n" + foodStore.getInernationalPhonenumber());
                    }
                    foodViewHolder.rating.setText(foodStore.getRating());
                    foodViewHolder.openNow.setText(foodStore.getOpenNow());
                    String openTime = "";
                    for (String s : foodStore.getOpenTime()) {
                        openTime += s + "\n";
                    }
                    if(openTime.trim().equals("")){
                        foodViewHolder.openTime.setText("N/A \n");
                    }else{
                        foodViewHolder.openTime.setText(openTime);
                    }
                    foodViewHolder.website.setText(foodStore.getWebsite());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(viewHolder instanceof LoadingViewHolder){
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }

        public void setLoader(){
            isLoading = false;
        }
}

class LoadingViewHolder  extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
    }
}
class FoodViewHolder extends RecyclerView.ViewHolder{
    ImageView icon;
    TextView foodName, address, phone, rating, openNow, openTime, website;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.food_icon);
        foodName = itemView.findViewById(R.id.food_name);
        address = itemView.findViewById(R.id.food_address);
        phone = itemView.findViewById(R.id.food_phonenumber);
        rating = itemView.findViewById(R.id.food_rating);
        openNow = itemView.findViewById(R.id.food_open_now);
        openTime = itemView.findViewById(R.id.food_open_time);
        website = itemView.findViewById(R.id.food_website);
    }
}

