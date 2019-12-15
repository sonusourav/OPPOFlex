package com.sonusourav.oppoflex.Dao;

public class FinDetailsDao {

  private String empFirstName,empLastName,dept,dor,designation,exp,salary;

  public FinDetailsDao(String empFirstName, String empLastName, String dept, String dor,
      String designation, String exp, String salary) {
    this.empFirstName = empFirstName;
    this.empLastName = empLastName;
    this.dept = dept;
    this.dor = dor;
    this.designation = designation;
    this.exp = exp;
    this.salary = salary;
  }

  public FinDetailsDao(){}

  public String getEmpFirstName() {
    return empFirstName;
  }

  public void setEmpFirstName(String empFirstName) {
    this.empFirstName = empFirstName;
  }

  public String getEmpLastName() {
    return empLastName;
  }

  public void setEmpLastName(String empLastName) {
    this.empLastName = empLastName;
  }

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  public String getDor() {
    return dor;
  }

  public void setDor(String dor) {
    this.dor = dor;
  }

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  public String getExp() {
    return exp;
  }

  public void setExp(String exp) {
    this.exp = exp;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }
}
