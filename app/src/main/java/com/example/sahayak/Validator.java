package com.example.sahayak;

import android.util.Patterns;

public class Validator {
    public static String validate_registration_data(String email, String password){
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "Invalid Email Address!";
        }

        if(password.length()<=5){
            return "Password is Too Short!";
        }
        return null;
    }

}
