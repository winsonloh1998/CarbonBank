package com.cb.carbonbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class CarbonCreditActivity extends AppCompatActivity {

    ListView listView;
    public static Stack<CreditHistory> creditList=new Stack<>();
    Date date = Calendar.getInstance().getTime();
    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private String authUser;
    private TextView ccAmount;
    private static int no;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_credit);
        if(no==0)
            initialize();
        listView=(ListView)findViewById(R.id.listViewContent);
        ccAmount=(TextView)findViewById(R.id.ccAmount);
        ccAmount.setText(HomeActivity.cc+" cc");
        loadHistory();
    }

    private void loadHistory() {
        final CreditHistoryAdapter adapter = new CreditHistoryAdapter(this, R.layout.credithistory, creditList);
        listView.setAdapter(adapter);

    }

    private void initialize(){
        CharSequence dt=android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss",date.getTime());
        CreditHistory ch1=new CreditHistory(String.format("%s",dt),"Purchase Ticket",20,0);
        CreditHistory ch2=new CreditHistory("30/12/2018  04:20:55","Running 2 km",25,1);
        CreditHistory ch3=new CreditHistory("31/12/2018  13:15:16","Sold Carbon Credit",200,0);
        CreditHistory ch4=new CreditHistory("31/12/2018  18:12:30","Bus event",20,1);
        creditList.push(ch1);
        creditList.push(ch2);
        creditList.push(ch3);
        creditList.push(ch4);
        no++;
    }


}
