package com.sonusourav.oppoflex.Dao;

public class PreLoanDao {

  private String loanId,date,name,email,title,bankName,loanType,imageUrl,fileLocation;
  private PerDetailsDao perDetailsDao;
  private FinDetailsDao finDetailsDao;
  private LoanDetailsDao loanDetailsDao;

  public PreLoanDao(String loanId, String date, String name, String email, String title,
      String bankName, String loanType, String imageUrl, String fileLocation,
      PerDetailsDao perDetailsDao) {
    this.loanId = loanId;
    this.date = date;
    this.name = name;
    this.email = email;
    this.title = title;
    this.bankName = bankName;
    this.loanType = loanType;
    this.imageUrl = imageUrl;
    this.fileLocation = fileLocation;
    this.perDetailsDao = perDetailsDao;
  }

  public PreLoanDao(String loanId, String date, String name, String email, String title,
      String bankName, String loanType, String imageUrl, String fileLocation,
      PerDetailsDao perDetailsDao, FinDetailsDao finDetailsDao) {
    this.loanId = loanId;
    this.date = date;
    this.name = name;
    this.email = email;
    this.title = title;
    this.bankName = bankName;
    this.loanType = loanType;
    this.imageUrl = imageUrl;
    this.fileLocation = fileLocation;
    this.perDetailsDao = perDetailsDao;
    this.finDetailsDao = finDetailsDao;
  }

  public PreLoanDao(String loanId, String date, String name, String email, String title,
      String bankName, String loanType, String imageUrl, String fileLocation,
      PerDetailsDao perDetailsDao, FinDetailsDao finDetailsDao,
      LoanDetailsDao loanDetailsDao) {
    this.loanId = loanId;
    this.date = date;
    this.name = name;
    this.email = email;
    this.title = title;
    this.bankName = bankName;
    this.loanType = loanType;
    this.imageUrl = imageUrl;
    this.fileLocation = fileLocation;
    this.perDetailsDao = perDetailsDao;
    this.finDetailsDao = finDetailsDao;
    this.loanDetailsDao = loanDetailsDao;
  }

  public PreLoanDao(String loanId, String date, String name, String email, String title,
      String bankName, String loanType, String imageUrl) {
    this.loanId = loanId;
    this.date = date;
    this.name = name;
    this.email = email;
    this.title = title;
    this.bankName = bankName;
    this.loanType = loanType;
    this.imageUrl = imageUrl;
  }

  public PreLoanDao (){}

  public String getLoanId() {
    return loanId;
  }

  public void setLoanId(String loanId) {
    this.loanId = loanId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getLoanType() {
    return loanType;
  }

  public void setLoanType(String loanType) {
    this.loanType = loanType;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getFileLocation() {
    return fileLocation;
  }

  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public PerDetailsDao getPerDetailsDao() {
    return perDetailsDao;
  }

  public void setPerDetailsDao(PerDetailsDao perDetailsDao) {
    this.perDetailsDao = perDetailsDao;
  }

  public FinDetailsDao getFinDetailsDao() {
    return finDetailsDao;
  }

  public void setFinDetailsDao(FinDetailsDao finDetailsDao) {
    this.finDetailsDao = finDetailsDao;
  }

  public LoanDetailsDao getLoanDetailsDao() {
    return loanDetailsDao;
  }

  public void setLoanDetailsDao(LoanDetailsDao loanDetailsDao) {
    this.loanDetailsDao = loanDetailsDao;
  }
}
