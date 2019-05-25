package com.example.hero;

class User {
    public String lastName;
    public String firstName;
    public String middleName;
    public String birthDay;
    public String mobileNumber;
    public String email;


    public User() {

    }

    public User(String lastName, String firstName, String middleName, String birthDay, String mobileNumber, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthDay = birthDay;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }
}
