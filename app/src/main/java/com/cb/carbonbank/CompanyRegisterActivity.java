package com.cb.carbonbank;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CompanyRegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputCompanyRegNo;
    private TextInputLayout textInputCompanyPw;
    private TextInputLayout textInputCompanyName;
    private TextInputLayout textInputOwner;
    private Button btn_create_company_acc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);

        textInputCompanyRegNo = findViewById(R.id.text_input_company_reg_id);
        textInputCompanyPw = findViewById(R.id.text_input_company_password);
        textInputCompanyName = findViewById(R.id.text_input_company_name);
        textInputOwner = findViewById(R.id.text_input_owner_username);
        btn_create_company_acc = findViewById(R.id.btn_company_signUp);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private boolean validateCompany(){
        String companyNoInput = null;
        if(textInputCompanyRegNo.getEditText().getText().toString().trim() != null){
            companyNoInput = textInputCompanyRegNo.getEditText().getText().toString().trim();
        }

        if(companyNoInput.isEmpty()){
            textInputCompanyRegNo.setError("Company registration number can't be empty");
            return false;
        }else if(!checkRegFormat(companyNoInput)){
            textInputCompanyRegNo.setError("Invalid company registration number format");
            return false;
        }else{
            textInputCompanyRegNo.setError(null);
            textInputCompanyRegNo.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCompanyPassword(){
        String companyPasswordInput = null;
        if(textInputCompanyPw.getEditText().getText().toString().trim() != null){
            companyPasswordInput = textInputCompanyPw.getEditText().getText().toString().trim();
        }

        if(companyPasswordInput.isEmpty()){
            textInputCompanyPw.setError("Company password can't be empty");
            return false;
        }else{
            textInputCompanyPw.setError(null);
            textInputCompanyPw.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCompanyName(){
        String companyNameInput = null;
        if(textInputCompanyName.getEditText().getText().toString().trim() != null){
            companyNameInput = textInputCompanyName.getEditText().getText().toString().trim();
        }

        if(companyNameInput.isEmpty()){
            textInputCompanyName.setError("Company name can't be empty");
            return false;
        }else{
            textInputCompanyName.setError(null);
            textInputCompanyName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateOwner(){
        String ownerNameInput = null;
        if(textInputOwner.getEditText().getText().toString().trim() != null){
            ownerNameInput = textInputOwner.getEditText().getText().toString().trim();
        }

        //TODO check Whether Owner username exist
        if(ownerNameInput.isEmpty()){
            textInputOwner.setError("Registered owner name can't be empty");
            return false;
        }else{
            textInputOwner.setError(null);
            textInputOwner.setErrorEnabled(false);
            return true;
        }
    }


    public void permitCreateCompanyAcc(View view){
        if(!validateCompany() | !validateCompanyPassword() | !validateCompanyName() | !validateOwner()){
            return;
        }

        String result = "Success";
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
    }

    private boolean checkRegFormat(String regNo){

        if(regNo.length() == 8){
            if(regNo.charAt(6) == '-'){
                if(Character.isUpperCase(regNo.charAt(7))){
                    for(int i=0;i<6;i++){
                        if(!Character.isDigit(regNo.charAt(i))){
                            return false;
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
