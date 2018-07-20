package com.example.hp.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import model.FoodStore;

public class CustomFoodListView extends ArrayAdapter<FoodStore>{

    private Integer resource;
    public static int check = 0;
    private Context context;
    private List<FoodStore> storeList;
    public CustomFoodListView(@NonNull Context context, int resource, @NonNull List<FoodStore> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.storeList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        FoodStore foodStore = storeList.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            System.out.println("food name: "+ foodStore.getName());
            System.out.println("food name: check : "+ check);

            convertView = LayoutInflater.from(context).inflate(R.layout.food_listview,null );
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.food_icon);
            viewHolder.foodName = convertView.findViewById(R.id.food_name);
            viewHolder.address = convertView.findViewById(R.id.food_address);
            viewHolder.phone = convertView.findViewById(R.id.food_phonenumber);
            viewHolder.rating = convertView.findViewById(R.id.food_rating);
            viewHolder.openNow = convertView.findViewById(R.id.food_open_now);
            viewHolder.openTime = convertView.findViewById(R.id.food_open_time);
            viewHolder.website = convertView.findViewById(R.id.food_website);

                convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if(check < storeList.size()){
            //set text
            try {
                System.out.println("food name in view: "+foodStore.getName());
    //            viewHolder.icon.setImageBitmap(showIcon(foodStore.getIconUrl()));
                new LoadImageFood(viewHolder.icon).execute(foodStore.getIconUrl());
                viewHolder.foodName.setText(foodStore.getName());
                viewHolder.address.setText(foodStore.getAddress());
                if(foodStore.getInernationalPhonenumber().trim().equals("")){
                    viewHolder.phone.setText(foodStore.getPhonenumber());
                }else{
                    viewHolder.phone.setText(foodStore.getPhonenumber() + " \n" + foodStore.getInernationalPhonenumber());
                }
                viewHolder.rating.setText(foodStore.getRating());
                viewHolder.openNow.setText(foodStore.getOpenNow());
                String openTime = "";
                for (String s : foodStore.getOpenTime()) {
                    openTime += s + "\n";
                }
                if(openTime.trim().equals("")){
                    viewHolder.openTime.setText("N/A \n");
                }else{
                    viewHolder.openTime.setText(openTime);
                }
                viewHolder.website.setText(foodStore.getWebsite());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            check ++;
//        }
        return convertView;

    }

    static class  ViewHolder{
        ImageView icon;
        TextView foodName, address, phone, rating, openNow, openTime, website;
    }


}
