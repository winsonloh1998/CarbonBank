package com.cb.carbonbank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CreditHistoryAdapter extends ArrayAdapter<CreditHistory> {

    public CreditHistoryAdapter(Activity context, int resource, List<CreditHistory> list) {
        super(context, resource, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CreditHistory ch = getItem(position);

        LayoutInflater inflater  = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.credithistory, parent, false);

        TextView textViewCode, textViewTitle, textViewCredit;

        textViewCode = (TextView)rowView.findViewById(R.id.textViewDate);
        textViewTitle = (TextView)rowView.findViewById(R.id.textViewDesciption);
        textViewCredit = (TextView)rowView.findViewById(R.id.textViewTransaction);
        int direction=ch.getDirection();

        textViewCode.setText(String.format("%s",ch.getTimestamp()));
        textViewTitle.setText(String.format("%s",ch.getDescription()));
        if(direction==1) {
            textViewCredit.setText(String.format("+ %d cc", ch.getAmount()));
            textViewCredit.setTextColor(Color.rgb(23,216,23));
        }
        else{
            textViewCredit.setText(String.format("- %d cc",  ch.getAmount()));
            textViewCredit.setTextColor(Color.rgb(215,21,21));
        }

        return rowView;

    }
}
