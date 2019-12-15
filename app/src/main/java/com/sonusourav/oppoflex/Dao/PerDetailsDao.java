package com.sonusourav.oppoflex.Dao;

public class PerDetailsDao {

  private String firstName,lastName,email,dob,gender,mobNo,address;

  public PerDetailsDao(String firstName, String lastName, String email, String dob,
      String gender, String mobNo, String address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.dob = dob;
    this.gender = gender;
    this.mobNo = mobNo;
    this.address = address;
  }

  public PerDetailsDao(){}

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getMobNo() {
    return mobNo;
  }

  public void setMobNo(String mobNo) {
    this.mobNo = mobNo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }


}
