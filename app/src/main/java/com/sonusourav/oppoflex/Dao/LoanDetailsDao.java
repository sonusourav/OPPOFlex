package com.sonusourav.oppoflex.Dao;

public class LoanDetailsDao {
  private String propertyCost, loanAmount,loanPurpose, repayment, tenure;

  public LoanDetailsDao(String propertyCost, String loanAmount, String loanPurpose,
      String repayment, String tenure) {
    this.propertyCost = propertyCost;
    this.loanAmount = loanAmount;
    this.loanPurpose = loanPurpose;
    this.repayment = repayment;
    this.tenure = tenure;
  }

  public LoanDetailsDao(){}

  public String getPropertyCost() {
    return propertyCost;
  }

  public void setPropertyCost(String propertyCost) {
    this.propertyCost = propertyCost;
  }

  public String getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(String loanAmount) {
    this.loanAmount = loanAmount;
  }

  public String getLoanPurpose() {
    return loanPurpose;
  }

  public void setLoanPurpose(String loanPurpose) {
    this.loanPurpose = loanPurpose;
  }

  public String getRepayment() {
    return repayment;
  }

  public void setRepayment(String repayment) {
    this.repayment = repayment;
  }

  public String getTenure() {
    return tenure;
  }

  public void setTenure(String tenure) {
    this.tenure = tenure;
  }
}
