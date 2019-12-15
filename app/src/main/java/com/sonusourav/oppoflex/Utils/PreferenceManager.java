package com.sonusourav.oppoflex.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.sonusourav.oppoflex.Dao.FinDetailsDao;
import com.sonusourav.oppoflex.Dao.LoanDetailsDao;
import com.sonusourav.oppoflex.Dao.PerDetailsDao;
import com.sonusourav.oppoflex.Dao.PreLoanDao;

public class PreferenceManager {

  private static final String PREF_NAME = "oppoflex";
  private static final String IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch";
  private static final String IS_LOGGED_IN = "isLoggedIn";
  private static final String IS_FIRST_GOOGLE_LOGIN = "isFirstGoogleLogin";
  private static final String IS_PASSWORD_UPDATED = "isPasswordUpdated";
  private static final String PREF_EMAIL = "email";
  private static final String PREF_USERNAME = "username";
  private static final String PREF_PASSWORD = "password";
  private static final String AUTH_TYPE = "authType";
  private static final String  PER_DETAILS = "perDetails";
  private static final String FIN_DETAILS = "finDetails";
  private static final String LOAN_DETAILS = "loanDetails";
  private static final String DRAFT_LOAN = "draftLoan";
  private static final String DRAFT_LEVEL = "0";
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;

  public PreferenceManager(Context context) {
    int PRIVATE_MODE = 0;
    pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
    editor.apply();
  }

  public void setAuthType(String authType) {
    editor.putString(AUTH_TYPE, authType);
    editor.commit();
  }

  public void setLoginCredentials(String email, String password, String authType) {
    editor.putString(PREF_EMAIL, email);
    editor.putString(PREF_PASSWORD, password);
    editor.putString(AUTH_TYPE, authType);
    editor.commit();
  }

  public void setIsLoggedIn(boolean isLoggedIn) {
    editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
    editor.commit();
  }

  public void setIsFirstGoogleLogin(boolean googleLogin) {
    editor.putBoolean(IS_FIRST_GOOGLE_LOGIN, googleLogin);
    editor.commit();
  }

  public void setIsPassUpdated(boolean passUpdated) {
    editor.putBoolean(IS_PASSWORD_UPDATED, passUpdated);
    editor.commit();
  }

  public void setFirstTimeLaunch(boolean isFirstTime) {
    editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
    editor.commit();
  }

  public void setPrefName(String name) {
    editor.putString(PREF_USERNAME, name);
    editor.commit();
  }

  public void setPrefEmail(String email) {
    editor.putString(PREF_EMAIL, email);
    editor.commit();
  }

  public void setDraftLevel(String draftLevel){
    editor.putString(DRAFT_LEVEL,draftLevel);
    editor.commit();
  }

  public void setPerDetails(PerDetailsDao perDetails){
    Gson gson = new Gson();
    String json = gson.toJson(perDetails);
    editor.putString(PER_DETAILS, json);
    editor.commit();
  }

  public void setFinDetails(FinDetailsDao finDetails){
    Gson gson = new Gson();
    String json = gson.toJson(finDetails);
    editor.putString(FIN_DETAILS, json);
    editor.commit();
  }
  public void setLoanDetails(LoanDetailsDao loanDetails){
    Gson gson = new Gson();
    String json = gson.toJson(loanDetails);
    editor.putString(LOAN_DETAILS, json);
    editor.commit();
  }

  public void setDraftLoan(PreLoanDao draftLoan){
    Gson gson = new Gson();
    String json = gson.toJson(draftLoan);
    editor.putString(DRAFT_LOAN, json);
    editor.commit();
  }

  public String getPrefEmail() {
    return pref.getString(PREF_EMAIL, "email");
  }

  public String getPrefPassword() {
    return pref.getString(PREF_PASSWORD, "password");
  }

  public String getUserName() {
    return pref.getString(PREF_USERNAME, "username");
  }

  public String getAuthType() {
    return pref.getString(AUTH_TYPE, "authType");
  }

  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGGED_IN, false);
  }

  public boolean isFirstGoogleLogin() {
    return pref.getBoolean(IS_FIRST_GOOGLE_LOGIN, true);
  }

  public boolean isPassUpdated() {
    return pref.getBoolean(IS_PASSWORD_UPDATED, false);
  }

  public boolean isFirstTimeLaunch() {
    return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
  }

  public String getDraftLevel() {
    return DRAFT_LEVEL;
  }

  public PerDetailsDao getPerDetails() {
    Gson gson = new Gson();
    String json = pref.getString(PER_DETAILS, null);
    return gson.fromJson(json, PerDetailsDao.class);
  }

  public FinDetailsDao getFinDetails() {
    Gson gson = new Gson();
    String json = pref.getString(FIN_DETAILS, null);
    return gson.fromJson(json, FinDetailsDao.class);
  }

  public LoanDetailsDao getLoanDetails() {
    Gson gson = new Gson();
    String json = pref.getString(LOAN_DETAILS, null);
    return gson.fromJson(json, LoanDetailsDao.class);
  }

  public PreLoanDao getDraftLoan() {
    Gson gson = new Gson();
    String json = pref.getString(DRAFT_LOAN, null);
    return gson.fromJson(json, PreLoanDao.class);
  }
}