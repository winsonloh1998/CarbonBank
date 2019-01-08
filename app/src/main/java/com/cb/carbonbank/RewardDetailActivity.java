package com.cb.carbonbank;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardDetailActivity extends AppCompatActivity {

    private TextView rdTitle,rdDesc,ccReq,ccc;
    private ImageView rdImage;
    private Button btnRedeem;
    Date date = Calendar.getInstance().getTime();
    private static final String rewardIDPref = "theRewardID";
    private SharedPreferences sharedPreferences;
    private String rewardID;
    private static final String prefName = "AuthenticatedUser";
    private String authUser;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectReward.php";
    RequestQueue queue;
    private ProgressDialog pDialog;
    private static final String TAG = "getRewardDetail";
    private static List<Rewards> rewardsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");
        rdTitle = findViewById(R.id.rdTitle);
        rdDesc = findViewById(R.id.rdDesc);
        ccReq = findViewById(R.id.ccReq);
        ccc = findViewById(R.id.ccc);

        pDialog = new ProgressDialog(this);
        rewardsList =  new ArrayList<>();

        rdImage = findViewById(R.id.rdImage);
        btnRedeem = findViewById(R.id.btnRedeem);


        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemReward();
            }
        });
        sharedPreferences = getSharedPreferences(rewardIDPref,MODE_PRIVATE);
        rewardID = sharedPreferences.getString("rewardID","R0000");

        downloadReward(this,rewardID);

    }

    public void downloadReward(Context context, String rewardID){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?RewardID=" + rewardID;

        if (!pDialog.isShowing())
            pDialog.setMessage("Getting Reward Detail...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            rewardsList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usersResponse = (JSONObject) response.get(i);
                                String rewardID = usersResponse.getString("RewardID");
                                String rewardTitle = usersResponse.getString("RewardTitle");
                                String rewardDesc = usersResponse.getString("RewardDesc");
                                String rewardType = usersResponse.getString("RewardType");
                                String rewardImage = usersResponse.getString("RewardImage");
                                int ccReq = Integer.parseInt(usersResponse.getString("CCRequired"));

                                Rewards rewards = new Rewards(rewardID,rewardTitle,rewardDesc,rewardType,rewardImage,ccReq);
                                rewardsList.add(rewards);
                            }
                            setInformation();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    private void setInformation(){
    rdTitle.setText(rewardsList.get(0).getRewardTitle());

    rdDesc.setText(rewardsList.get(0).getRewardDesc());

    ccReq.setText(String.format("%d",rewardsList.get(0).getCcRequired()));

    ccc.setText(String.format("%d",HomeActivity.cc));

    byte[] decodedStringImg = Base64.decode(rewardsList.get(0).getRewardImage(),Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedStringImg, 0, decodedStringImg.length);
        rdImage.setImageBitmap(myBitmap);
        if(HomeActivity.cc<rewardsList.get(0).getCcRequired()){
            btnRedeem.setEnabled(false);
            btnRedeem.setBackgroundColor(Color.rgb(0xb4,0xb4,0xb4));
        }
    }


    public void redeemReward(){
        CharSequence dt=android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss",date.getTime());
        int newTaxes=HomeActivity.ct;
        int newCC;
        int payAmount=rewardsList.get(0).getCcRequired();
        newCC=HomeActivity.cc-rewardsList.get(0).getCcRequired();
        CreditHistory newch=new CreditHistory(String.format("%s",dt),"Reward Exchange",payAmount,0);
        CarbonCreditActivity.creditList.push(newch);
        notifcationCall();
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        updateCC(getApplicationContext(),"https://crocodilian-trade.000webhostapp.com/UpdateCCnCT.php",authUser,newTaxes,newCC);
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(RewardDetailActivity.this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Carbon Bank Notification")
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}) //VIBRATION
                .setLights(Color.BLUE,3000,3000) //LED LIGHT
                .setContentText("The reward has been redeemed");

        final Intent intent = new Intent(this,HomeActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1,notificationBuilder.build());
    }
}
