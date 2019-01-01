package com.cb.carbonbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Check Whether is Authenticated
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        boolean authenticated = sharedPreferences.getBoolean("authenticated",false);
        if(!authenticated){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    //Testing Purpose
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean unauthen = false;
        editor.putBoolean("authenticated",unauthen);
        editor.commit();
    }
}
