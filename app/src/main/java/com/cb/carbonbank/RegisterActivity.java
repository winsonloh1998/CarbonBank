package com.cb.carbonbank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "getUsernameOnly";
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private TextInputLayout textInputEmail;
    private Button btnSignUp;
    private ImageView imageViewProfilePic;
    private static List<Users> usersList;

    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;

    private final int CODE_GALLERY_REQUEST = 999;
    private final int CODE_CAMERA_REQUEST = 998;
    private Bitmap bitmap;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);
        textInputEmail = findViewById(R.id.text_input_email);
        btnSignUp = findViewById(R.id.btn_signUp);

        usersList = new ArrayList<>();
        pDialog = new ProgressDialog(this);

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "Network Service Not Available", Toast.LENGTH_LONG).show();
        }

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        imageViewProfilePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder requestImgAlert = new AlertDialog.Builder(RegisterActivity.this);
                requestImgAlert.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RegisterActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);
                    }
                }).setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RegisterActivity.this,
                                new String[] {Manifest.permission.CAMERA},CODE_CAMERA_REQUEST);

                    }
                });

                requestImgAlert.setTitle("Pick Image From");
                AlertDialog alert = requestImgAlert.create();
                alert.show();
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private boolean validatePassword(){
        boolean passwordAccept = false;
        boolean cPasswordAccept = false;
        String passwordInput = null;
        String cPasswordInput = null;

        if(textInputPassword.getEditText().getText().toString().trim() != null){
            passwordInput = textInputPassword.getEditText().getText().toString().trim();

            if(passwordInput.isEmpty()){
                textInputPassword.setError("Password can't be empty");
                passwordAccept = false;
            }else{
                textInputPassword.setError(null);
                textInputPassword.setErrorEnabled(false);
                passwordAccept = true;
            }
        }

        if(textInputConfirmPassword.getEditText().getText().toString().trim() != null){
            cPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();
            if(cPasswordInput.isEmpty()){
                textInputConfirmPassword.setError("Confirm Password can't be empty");
                cPasswordAccept = false;
            }else{
                textInputConfirmPassword.setError(null);
                textInputConfirmPassword.setErrorEnabled(false);
                cPasswordAccept = true;
            }
        }

        if(passwordAccept && cPasswordAccept){
            if(passwordInput.equals(cPasswordInput)){
                return true;
            }else {
                textInputConfirmPassword.setError("Confirm Password is invalid. Not Equal");
                return false;
            }
        }else{
            return  false;
        }
    }

    private boolean validateEmail(){
        String emailInput = null;

        if(textInputEmail.getEditText().getText().toString().trim() != null){
            emailInput = textInputEmail.getEditText().getText().toString().trim();
        }

        if(emailInput.isEmpty()){
            textInputEmail.setError("Email can't be empty");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textInputEmail.setError("Invalid Email Format");
            return false;
        }else{
            textInputEmail.setError(null);
            textInputEmail.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == CODE_GALLERY_REQUEST) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),CODE_GALLERY_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You don't have permission to access gallery",Toast.LENGTH_LONG).show();
            }
            return;
        }else if(requestCode == CODE_CAMERA_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CODE_CAMERA_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You don't have permission to access camera",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CODE_GALLERY_REQUEST:
                if(resultCode == RESULT_OK && data != null){
                    filePath = data.getData();
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(filePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        imageViewProfilePic.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CODE_CAMERA_REQUEST:
                if(resultCode == RESULT_OK && data != null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageViewProfilePic.setImageBitmap(bitmap);
                }
                break;
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImage;
    }

    //Save User Record
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public void downloadUsers(final Context context, String username){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?Username=" + username;

        if (!pDialog.isShowing())
            pDialog.setMessage("Validating new account...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            usersList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usersResponse = (JSONObject) response.get(i);
                                String username = usersResponse.getString("Username");
                                Users user = new Users(username);
                                usersList.add(user);
                            }
                            checkExist(usersList.size());
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

    //INSERT DATA
    public void regUser(){
        Users users = new Users();

        users.setUsername(textInputUsername.getEditText().getText().toString());
        users.setPassword(textInputPassword.getEditText().getText().toString());
        users.setEmail(textInputEmail.getEditText().getText().toString());
        users.setDisplayName("ProfileName"+(int)(Math.random() * 100000)+1);
        users.setGender("O");
        users.setDob(" ");
        users.setPhoneNo("");
        users.setCarbonCredit(0);
        users.setCarbonTax(0);

        if(bitmap != null){
            users.setProfilePic(imageToString(bitmap));
        }else{
            Bitmap noImg = BitmapFactory.decodeResource(getResources(),R.drawable.testimg);
            users.setProfilePic(imageToString(noImg));
        }
        users.setFirstLogin("T");

        try {
            //TODO: Please update the URL to point to your own server
            makeServiceCall(this, "https://crocodilian-trade.000webhostapp.com/InsertUsers.php", users);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void makeServiceCall(Context context, String url, final Users users) {
        //mPostCommentResponse.requestStarted();
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

                    params.put("Username", users.getUsername());
                    params.put("Password", users.getPassword());
                    params.put("Email", users.getEmail());
                    params.put("DisplayName", users.getDisplayName());
                    params.put("Gender", users.getGender());
                    params.put("DoB", users.getDob());
                    params.put("CarbonCredit", String.valueOf(users.getCarbonCredit()));
                    params.put("CarbonTax", String.valueOf(users.getCarbonTax()));
                    params.put("ProfilePic",users.getProfilePic());
                    params.put("PhoneNo",users.getPhoneNo());
                    params.put("FirstLogin", users.getFirstLogin());
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

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

private boolean validateUser(){
    String usernameInput = null;
    if(textInputUsername.getEditText().getText().toString().trim() != null){
        usernameInput = textInputUsername.getEditText().getText().toString().trim();
    }

    if(usernameInput.isEmpty()){
        textInputUsername.setError("Username can't be empty");
        return false;
    }else{
        textInputUsername.setError(null);
        textInputUsername.setErrorEnabled(false);
        return true;
    }
}


    //WHEN BUTTON CLICK
    public void permitCreateAcc(View view){
        if(!validateUser() | !validatePassword() | !validateEmail()){
            return;
        }

        downloadUsers(getApplicationContext(),textInputUsername.getEditText().getText().toString());
    }

    public void checkExist(int size){
        if(size > 0){
            textInputUsername.setError("Username ["+textInputUsername.getEditText().getText().toString()+"] already exists. Please Try With Another One.");
        }else{
            textInputUsername.setError(null);
            textInputUsername.setErrorEnabled(false);
            regUser();
        }
    }

}
