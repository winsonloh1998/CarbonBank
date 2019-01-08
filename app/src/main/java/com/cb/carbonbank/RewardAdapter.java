package com.cb.carbonbank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RewardAdapter extends ArrayAdapter<Rewards> {

    public RewardAdapter(Activity context,int resource, List<Rewards> list){
        super(context,resource,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Rewards reward = getItem(position);

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.reward_content,parent,false);

        TextView titleReward,ccReqReward;
        CircleImageView imgReward;

        titleReward = (TextView) rowView.findViewById(R.id.titleReward);
        ccReqReward = (TextView) rowView.findViewById(R.id.ccReqReward);
        imgReward = (CircleImageView) rowView.findViewById(R.id.imgReward);

        titleReward.setText(reward.getRewardTitle());
        ccReqReward.setText(String.format("%d CC",reward.getCcRequired()));

        byte[] decodedStringImg = Base64.decode(reward.getRewardImage(),Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedStringImg, 0, decodedStringImg.length);
        imgReward.setImageBitmap(myBitmap);
        return rowView;
    }

}
