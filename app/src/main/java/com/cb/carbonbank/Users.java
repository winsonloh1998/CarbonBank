package com.cb.carbonbank;

public class Users {
    private String username;
    private String password;
    private String email;
    private String displayName;
    private String gender;
    private String dob;
    private int carbonCredit;
    private int carbonTax;
    //private byte[] profilePic;
    private String firstLogin;

    public Users() {
    }


    //For Checking Existing Username
    public Users(String username) {
        this.username = username;
    }

    //For Checking Username & Password
    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //To Insert User Personal Info
    public Users(String username, String password, String email, String displayName, String gender, String dob, int carbonCredit, int carbonTax, String firstLogin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.displayName = displayName;
        this.gender = gender;
        this.dob = dob;
        this.carbonCredit = carbonCredit;
        this.carbonTax = carbonTax;
        this.firstLogin = firstLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getCarbonCredit() {
        return carbonCredit;
    }

    public void setCarbonCredit(int carbonCredit) {
        this.carbonCredit = carbonCredit;
    }

    public int getCarbonTax() {
        return carbonTax;
    }

    public void setCarbonTax(int carbonTax) {
        this.carbonTax = carbonTax;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }
}
