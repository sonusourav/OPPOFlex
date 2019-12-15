package com.sonusourav.oppoflex.Dao;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserDao implements Serializable {
  private String name;
  private String email;
  private String mobile;
  private String gender;
  private String dob;
  private String pass;
  private String profilePic;
  private String coverPic;

  public UserDao() {
  }

  public UserDao(String userEmail, String uname, String userPic) {

    this.email = userEmail;
    this.name = uname;
    this.profilePic = userPic;
  }

  public UserDao(String userEmail, String pass) {
    this.name = "";
    this.email = userEmail;
    this.mobile = "";
    this.gender = "";
    this.dob = "";
    this.pass = pass;
    this.profilePic = "";
    this.coverPic = "";
  }

  public UserDao(String userEmail) {
    this.name = "";
    this.email = userEmail;
    this.pass = "fa31b7bcb9e0d9ad4ab7e94e0230f2af7";
    this.mobile = "";
    this.gender = "";
    this.dob = "";
    this.profilePic = "";
    this.coverPic = "";
  }

  public UserDao(String name, String email, String mobile, String gender, String dob,
      String pass, String profilePic, String coverPic) {
    this.name = name;
    this.email = email;
    this.mobile = mobile;
    this.gender = gender;
    this.dob = dob;
    this.pass = pass;
    this.profilePic = profilePic;
    this.coverPic = coverPic;
  }

  public UserDao(String name, String email, String mobile, String gender, String dob,
      String profilePic, String coverPic) {
    this.name = name;
    this.email = email;
    this.mobile = mobile;
    this.gender = gender;
    this.dob = dob;
    this.profilePic = profilePic;
    this.coverPic = coverPic;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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

  public String getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  public String getCoverPic() {
    return coverPic;
  }

  public void setCoverPic(String coverPic) {
    this.coverPic = coverPic;
  }
}
