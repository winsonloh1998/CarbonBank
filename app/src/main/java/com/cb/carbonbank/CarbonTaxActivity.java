package com.cb.carbonbank;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CarbonTaxActivity extends AppCompatActivity {


    private TextView textViewTaxes;
    private TextView textViewConvert;
    private TextView textViewCurrent;
    private int convertCC;
    private Button btnCC;
    private Button btnOB;
    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private String authUser;
    Date date = Calendar.getInstance().getTime();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_tax);

        textViewTaxes=(TextView)findViewById(R.id.textViewTaxes);
        textViewConvert=(TextView)findViewById(R.id.textViewConvert);
        textViewCurrent=(TextView)findViewById(R.id.textViewCurrent);
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");
        textViewTaxes.setText("RM "+ HomeActivity.ct);
        convertCC=(int)Math.floor(HomeActivity.ct*0.1);
        textViewConvert.setText(String.format("%d cc",convertCC));
        textViewCurrent.setText(HomeActivity.cc+" cc");
        btnCC=(Button)findViewById(R.id.btnCC);
        btnOB=(Button)findViewById(R.id.btnOB);
        if(convertCC==0){
            btnCC.setEnabled(false);
            btnOB.setEnabled(false);
            btnCC.setBackgroundColor(Color.rgb(0xb4,0xb4,0xb4));
            btnOB.setBackgroundColor(Color.rgb(0xb4,0xb4,0xb4));
        }
        else if(HomeActivity.cc==0){
            btnCC.setEnabled(false);
            btnCC.setBackgroundColor(Color.rgb(0xb4,0xb4,0xb4));
        }

        btnOB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Proceed to Online Banking Website", Toast.LENGTH_SHORT);
                toast.show();

                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.pbebank.com/"));
                startActivity(viewIntent);
            }
        });

        btnCC.setOnClickListener(new View.OnClickListener() {


            public void onClick(View arg0) {
                paymentCC();

            }
        });
    }
    public void paymentCC(){
        CharSequence dt=android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss",date.getTime());
        int newTaxes;
        int newCC;
        int payAmount;
        if(convertCC<=HomeActivity.cc){
            newTaxes=0;
            newCC=HomeActivity.cc-convertCC;
            updateCC(getApplicationContext(),"https://crocodilian-trade.000webhostapp.com/UpdateCCnCT.php",authUser,newTaxes,newCC);
            payAmount=convertCC;

        }
        else{
            convertCC=convertCC-HomeActivity.cc;
            newCC=0;
            newTaxes=convertCC*10;
            updateCC(getApplicationContext(),"https://crocodilian-trade.000webhostapp.com/UpdateCCnCT.php",authUser,newTaxes,newCC);
            payAmount=HomeActivity.cc;
        }
        CreditHistory newch=new CreditHistory(String.format("%s",dt),"Carbon Taxes Payment",payAmount,0);
        CarbonCreditActivity.creditList.push(newch);
        notifcationCall();
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void updateCC(Context context, String url, final String authUser, final int newTaxes, final int newCC){
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Username",authUser);
                    params.put("CarbonCredit",String.valueOf(newCC));
                    params.put("CarbonTax", String.valueOf(newTaxes));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void notifcationCall(){
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID="Carbon Credit Added";
        CharSequence name = "Congratulation";
        String Description = "1000 carbon credit has been added your account.";

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(CarbonTaxActivity.this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Carbon Bank Notification")
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}) //VIBRATION
                .setLights(Color.BLUE,3000,3000) //LED LIGHT
                .setContentText("Carbon Tax has been paid");

        final Intent intent = new Intent(this,HomeActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1,notificationBuilder.build());
    }
}
