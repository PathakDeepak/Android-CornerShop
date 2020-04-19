package com.coder24.cornershop.model;

public class Users {
    private String name, password, phoneNumber, image, address;

    public Users(){

    }

    public Users(String name, String password, String phoneNumber, String image, String address) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
