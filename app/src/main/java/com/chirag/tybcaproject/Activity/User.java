package com.chirag.tybcaproject.Activity;
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String houseNo;
    private String area;
    private String state;
    private String city;
    private String pincode;

    public User(String firstName, String lastName, String email) {
        // Default constructor required for Firebase
    }

    public User(String firstName, String lastName, String email, String mobileNo, String houseNo, String area, String state, String city, String pincode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.houseNo = houseNo;
        this.area = area;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getArea() {
        return area;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }
}