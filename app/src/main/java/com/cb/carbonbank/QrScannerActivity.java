package com.cb.carbonbank;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.health.PackageHealthStats;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "updateCarbonCreditToAccount";
    private static final int REQUEST_CAMERA = 1;
    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private ZXingScannerView scannerView;
    private String currentUser;
    private static final int amtCCToAdd= 3;
    long vibrateDuration = 2000;
    Date date = Calendar.getInstance().getTime();
    CharSequence dt=android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss",date.getTime());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        currentUser = sharedPreferences.getString("authenticatedUser","Anonymous");

        scannerView = new ZXingScannerView(this);
        RelativeLayout qrLayout = (RelativeLayout) findViewById(R.id.relative_scan_take_single);
        qrLayout.addView(scannerView);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!checkPermission()){
                requestPermission();
            }
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(QrScannerActivity.this,CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(QrScannerActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(QrScannerActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        if(shouldShowRequestPermissionRationale(CAMERA)){
                            displayAlertMessage("You need to allow access for both permissions"
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
                scannerView.setSoundEffectsEnabled(true);
                scannerView.setAutoFocus(true);
            }else{
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    protected void onPause()  {
        super.onPause() ;
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(QrScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("Okay", listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();



        if(decodeScanQr(scanResult).equals("Carbon Credit")){
            AlertDialog.Builder popUpAlert = new AlertDialog.Builder(QrScannerActivity.this);
            popUpAlert.setMessage("Carbon Credit is Successfully Redeemed.").setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //TODO: Please update the URL to point to your own server
                            makeServiceCall(getApplicationContext(), "https://crocodilian-trade.000webhostapp.com/UpdateCarbonCredit.php", currentUser,amtCCToAdd);
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        finish();
                    }
            });
            popUpAlert.setIcon(R.drawable.congrat);
            AlertDialog alert = popUpAlert.create();
            alert.show();
            notifcationCall();
            CreditHistory newch=new CreditHistory(String.format("%s",dt),"Public Transport Reward",3,1);
            CarbonCreditActivity.creditList.push(newch);
        }else{
            AlertDialog.Builder popUpAlert = new AlertDialog.Builder(QrScannerActivity.this);
            popUpAlert.setMessage("Invalid QR Code.").setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.resumeCameraPreview(QrScannerActivity.this);
                    }
                });
            popUpAlert.setIcon(R.drawable.not_congrat);
            AlertDialog alert = popUpAlert.create();
            alert.show();
        }
    }

    private void notifcationCall(){
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID="Carbon Credit Added";
        CharSequence name = "Congratulation";
        String Description = "3 carbon credit has been added your account.";

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(QrScannerActivity.this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Carbon Bank Notification")
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}) //VIBRATION
                .setLights(Color.BLUE,3000,3000) //LED LIGHT
                .setContentText("Congratulations, you have just received 3 carbon credit by using qr scanner.");

        final Intent intent = new Intent(this,HomeActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1,notificationBuilder.build());
    }

    private String decodeScanQr(String code){
        String decodedString = "";
        byte[] decodedBytes = Base64.getDecoder().decode(code);

        decodedString = new String(decodedBytes);
        return decodedString;
    }

    public void makeServiceCall(Context context, String url, final String currentUsername, final int currentCarbonCredit) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);
        url = url + "?Username=" +currentUsername+"&CarbonCredit=" +currentCarbonCredit;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
