package com.example.blooddonation.Classes;

public class UserModel {
    public String name,email,phone,password;

    public UserModel() {
    }

    public UserModel(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserModel(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
