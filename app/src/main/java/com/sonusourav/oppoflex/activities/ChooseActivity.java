package com.sonusourav.oppoflex.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageButton sbi,hdfc,idbi,kotak,baroda,union,personal,home,edu,car,bike,credit;
  private Button next;
  private String loanProvider="sbi",loanType="personal";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose);

    init();
    sbi.setOnClickListener(this);
    hdfc.setOnClickListener(this);
    idbi.setOnClickListener(this);
    kotak.setOnClickListener(this);
    union.setOnClickListener(this);
    baroda.setOnClickListener(this);

    personal.setOnClickListener(this);
    home.setOnClickListener(this);
    edu.setOnClickListener(this);
    car.setOnClickListener(this);
    bike.setOnClickListener(this);
    credit.setOnClickListener(this);

    next.setOnClickListener(this);

  }

  private void setSelectedFalseBank(){

    sbi.setActivated(false);
    hdfc.setActivated(false);
    idbi.setActivated(false);
    baroda.setActivated(false);
    kotak.setActivated(false);
    union.setActivated(false);

    sbi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    hdfc.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    idbi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    baroda.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    kotak.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    union.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));

  }

  private void setSelectedFalseLoan(){

    personal.setActivated(false);
    edu.setActivated(false);
    home.setActivated(false);
    car.setActivated(false);
    bike.setActivated(false);
    credit.setActivated(false);

    personal.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    edu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    home.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    car.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    bike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
    credit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));

  }

  private boolean checkNext(){
    boolean value1=false,value2=false;

    if(!sbi.isActivated() && !hdfc.isActivated() && !idbi.isActivated() && !kotak.isActivated() && !baroda.isActivated() && !union.isActivated()){
      Toast.makeText(getApplicationContext(),"Please select Loan Provider",Toast.LENGTH_SHORT).show();
    }else
      value1=true;

    if(!personal.isActivated() && !home.isActivated() && !edu.isActivated() && !car.isActivated() && !bike.isActivated() && !credit.isActivated()){
      Toast.makeText(getApplicationContext(),"Please select type of loan",Toast.LENGTH_SHORT).show();
    }else
      value2=true;

    return (value1 && value2);
  }

  private void init(){

    sbi=findViewById(R.id.sbi);
    hdfc=findViewById(R.id.hdfc);
    idbi=findViewById(R.id.idbi);
    kotak=findViewById(R.id.kotak);
    baroda=findViewById(R.id.baroda);
    union=findViewById(R.id.union);

    personal=findViewById(R.id.personal_loan);
    home=findViewById(R.id.home_loan);
    edu=findViewById(R.id.edu_loan);
    car=findViewById(R.id.car_loan);
    bike=findViewById(R.id.bike_loan);
    credit=findViewById(R.id.credit_loan);

    next=findViewById(R.id.choose_next);

  }

  @Override public void onClick(View v) {

    switch (v.getId()){
      case R.id.sbi:
        loanProvider="SBI";
        setSelectedFalseBank();
        sbi.setActivated(true);
        sbi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.hdfc:
        loanProvider="HDFC";
        setSelectedFalseBank();
        hdfc.setActivated(true);
        hdfc.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.idbi:
        loanProvider="IDBI";
        setSelectedFalseBank();
        idbi.setActivated(true);
        idbi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.kotak:
        loanProvider="Kotak";
        setSelectedFalseBank();
        kotak.setActivated(true);
        kotak.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.baroda:
        loanProvider="Bank of Baroda";
        setSelectedFalseBank();
        baroda.setActivated(true);
        baroda.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.union:
        loanProvider="Union Bank";
        setSelectedFalseBank();
        union.setActivated(true);
        union.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.personal_loan:
        loanType="Personal Loan";
        setSelectedFalseLoan();
        personal.setActivated(true);
        personal.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.edu_loan:
        loanType="Education Loan";
        setSelectedFalseLoan();
        edu.setActivated(true);
        edu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.home_loan:
        loanType="Home Loan";
        setSelectedFalseLoan();
        home.setActivated(true);
        home.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.car_loan:
        loanType="Car Loan";
        setSelectedFalseLoan();
        car.setActivated(true);
        car.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.bike_loan:
        loanType="Bike Loan";
        setSelectedFalseLoan();
        bike.setActivated(true);
        bike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.credit_loan:
        loanType="Credit Card Loan";
        setSelectedFalseLoan();
        credit.setActivated(true);
        credit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dot_active_screen3)));
        break;
      case R.id.choose_next:
        if(checkNext()){

          PreferenceManager choosePref=new PreferenceManager(this);
          choosePref.setLoanData(loanProvider,loanType);
          startActivity(new Intent(ChooseActivity.this,NewLoanActivity.class));
         // Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();

        }
        break;

    }

  }

  public void onBackPressed() {
    NavUtils.navigateUpFromSameTask(this);
  }

}
