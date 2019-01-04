package com.cb.carbonbank;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.health.PackageHealthStats;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Base64;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

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

        AlertDialog.Builder popUpAlert = new AlertDialog.Builder(QrScannerActivity.this);

        if(decodeScanQr(scanResult).equals("Carbon Credit")){
            popUpAlert.setMessage("Carbon Credit is Successfully Redeemed.").setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         finish();
                    }
            });
            popUpAlert.setIcon(R.drawable.congrat);

        }else{
            popUpAlert.setMessage("Invalid QR Code.").setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.resumeCameraPreview(QrScannerActivity.this);
                    }
                });
            popUpAlert.setIcon(R.drawable.not_congrat);
        }

        AlertDialog alert = popUpAlert.create();
        alert.show();

        /* AlertDialog.Builder quitAlert = new AlertDialog.Builder(HomeActivity.this);
            quitAlert.setMessage("Are you sure you want to sign out?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            boolean unauthen = false;
                            editor.putBoolean("authenticated",unauthen);
                            editor.commit();
                            signOutFunction();
                            overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_up);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            quitAlert.setIcon(R.drawable.ic_exit_to_app_black_24dp);

            AlertDialog alert = quitAlert.create();
            alert.setTitle("Sign Out");
            alert.show();*/
    }

    private String decodeScanQr(String code){
        String decodedString = "";
        byte[] decodedBytes = Base64.getDecoder().decode(code);

        decodedString = new String(decodedBytes);
        return decodedString;
    }
}
