package com.cb.carbonbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private ImageView drawerProfilePic;

    private TextView tvDrawerDisplayName;
    private TextView tvDrawerEmail;

    //Variable Get User Information
    private static final String TAG = "getUserByUsername";
    private static List<Users> userList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        tvDrawerDisplayName = (TextView)findViewById(R.id.drawerDisplayName);
        tvDrawerEmail = (TextView)findViewById(R.id.drawerEmail);

        pDialog = new ProgressDialog(this);
        userList = new ArrayList<>();

        //Check Whether is Authenticated
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        boolean authenticated = sharedPreferences.getBoolean("authenticated",false);
        if(!authenticated){
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        final String authUser = sharedPreferences.getString("authenticatedUser","Anonymous");
        downloadUsers(getApplicationContext(),authUser);

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //For the drawer toggle on left hand side to operate
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.logout:
//                sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                boolean unauthen = false;
//                editor.putBoolean("authenticated",unauthen);
//                editor.commit();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }



    @Override
    public void finish(){
        super.finish();
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean unauthen = false;
        editor.putBoolean("authenticated",unauthen);
        editor.commit();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_up);
    }

    public void downloadUsers(Context context, String username){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?Username=" + username;

        if (!pDialog.isShowing())
            pDialog.setMessage("Sync With Server...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            userList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usersResponse = (JSONObject) response.get(i);
                                String username = usersResponse.getString("Username");
                                String email = usersResponse.getString("Email");
                                String displayName = usersResponse.getString("DisplayName");
                                String gender = usersResponse.getString("Gender");
                                String dob = usersResponse.getString("DoB");
                                int cc = Integer.parseInt(usersResponse.getString("CarbonCredit"));
                                int ct = Integer.parseInt(usersResponse.getString("CarbonTax"));
                                String firstLogin = usersResponse.getString("FirstLogin");

                                Users user = new Users(username,email,displayName,gender,dob,cc,ct,firstLogin);
                                userList.add(user);
                            }
                            setInformation(userList.size());
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

    public void setInformation(int size){
        if(size > 0){
//            if(userList.get(0).getDisplayName() != null){
//                tvDrawerDisplayName.setText(userList.get(0).getDisplayName());
//            }
//            if(userList.get(0).getEmail() != null){
//                tvDrawerEmail.setText(userList.get(0).getEmail());
//            }

        }else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }
}
