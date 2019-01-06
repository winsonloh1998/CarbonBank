package com.cb.carbonbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePicActivity extends AppCompatActivity {

    private CircleImageView profileActivityPic;
    private TextView profileCCAmt;
    private TextView profileCTAmt;
    private TextView profileActEmail;
    private TextView profileActPhone;
    private TextView profileActGender;
    private TextView profileActDob;
    private TextView tvProfileName;
    private Button editBtn;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;

    private static final String TAG = "getUserByUsername";
    private static List<Users> userList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;
    private String authUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);

        profileActivityPic = findViewById(R.id.profileActivityPic);
        profileCCAmt = findViewById(R.id.profileCCAmt);
        profileCTAmt = findViewById(R.id.profileCTAmt);
        profileActEmail = findViewById(R.id.profile_act_email);
        profileActPhone = findViewById(R.id.profile_act_Phone);
        profileActGender = findViewById(R.id.profile_act_gender);
        profileActDob = findViewById(R.id.profile_act_birthdate);
        tvProfileName = findViewById(R.id.tvProfileName);
        editBtn = findViewById(R.id.editBtn);

        pDialog = new ProgressDialog(this);
        userList = new ArrayList<>();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePicActivity.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");
        downloadUsers(getApplicationContext(), authUser);
    }

    public void downloadUsers(Context context, String username){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?Username=" + username;

        if (!pDialog.isShowing())
            pDialog.setMessage("Getting Your Profile...");
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
                                String profilePic = usersResponse.getString("ProfilePic");
                                String phoneNo = usersResponse.getString("PhoneNo");
                                String firstLogin = usersResponse.getString("FirstLogin");

                                Users user = new Users(username,email,displayName,gender,dob,cc,ct,profilePic,phoneNo,firstLogin);
                                userList.add(user);
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
        byte[] decodedStringImg = Base64.decode(userList.get(0).getProfilePic(),Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedStringImg, 0, decodedStringImg.length);

        if(myBitmap != null){
            profileActivityPic.setImageBitmap(myBitmap);
        }else{
            profileActivityPic.setImageResource(R.drawable.testimg);
        }

        tvProfileName.setText(userList.get(0).getDisplayName());

        profileCCAmt.setText(String.format("%d",userList.get(0).getCarbonCredit()));
        profileCTAmt.setText(String.format("RM%d",userList.get(0).getCarbonTax()));
        profileActEmail.setText(userList.get(0).getEmail());
        profileActPhone.setText(userList.get(0).getPhoneNo());

        if(userList.get(0).getGender().equals("M")){
            profileActGender.setText("Male");
        }else if(userList.get(0).getGender().equals("F")){
            profileActGender.setText("Female");
        }else{
            profileActGender.setText("Other");
        }

        profileActDob.setText(userList.get(0).getDob());
    }


    @Override
    protected void onResume() {
        downloadUsers(this,authUser);
        super.onResume();
    }
}
