package com.example.sahayak;

import android.util.Patterns;

import java.util.HashSet;

public class Validator {
    public static String validate_registration_data(String first_name, String last_name,String email, String password){
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "Invalid Email Address!";
        }

        if(first_name.length()==0)
            return "First name can't be empty.";
        if(last_name.length()==0)
            return "Last name can't be empty.";

        boolean low = false;
        boolean up= false;
        boolean special = false;
        boolean num = false;

        HashSet<Character> h = new HashSet<>();
        h.add('!');
        h.add('@');
        h.add('#');
        h.add('$');
        h.add('%');
        h.add('^');
        h.add('&');
        h.add('*');
        h.add('(');
        h.add(')');
        h.add('-');
        h.add('+');
        h.add('<');
        h.add('>');
        for(char c: password.toCharArray())
        {
            if(Character.isDigit(c))
                num=true;
            else if(Character.isUpperCase(c))
                up = true;
            else if(Character.isLowerCase(c))
                low = true;
            else if(h.contains(c))
                special = true;
        }

        if(special && low && up && num && password.length()>=8)
            return null;
        else
        {
            if(special==false)
                return "Password must contain special character";
            else if(low==false)
                return "Password must contain lower case character";
            else if(up==false)
                return "Password must contain upper case character";
            else if(num==false)
                return "Password must contain a digit";
            return "Password is Too Short!";
        }

    }
    public static String validate_login_data(String email, String password){
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "Invalid Email Address!";
        }


        boolean low = false;
        boolean up= false;
        boolean special = false;
        boolean num = false;

        HashSet<Character> h = new HashSet<>();
        h.add('!');
        h.add('@');
        h.add('#');
        h.add('$');
        h.add('%');
        h.add('^');
        h.add('&');
        h.add('*');
        h.add('(');
        h.add(')');
        h.add('-');
        h.add('+');
        h.add('<');
        h.add('>');
        for(char c: password.toCharArray())
        {
            if(Character.isDigit(c))
                num=true;
            else if(Character.isUpperCase(c))
                up = true;
            else if(Character.isLowerCase(c))
                low = true;
            else if(h.contains(c))
                special = true;
        }

        if(special && low && up && num && password.length()>=8)
            return null;
        else
        {
            if(special==false)
                return "Password must contain special character";
            else if(low==false)
                return "Password must contain lower case character";
            else if(up==false)
                return "Password must contain upper case character";
            else if(num==false)
                return "Password must contain a digit";
            return "Password is Too Short!";
        }

    }

}