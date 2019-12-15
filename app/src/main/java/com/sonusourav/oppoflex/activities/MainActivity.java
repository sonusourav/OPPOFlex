package com.sonusourav.oppoflex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatDelegate;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.TestActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
  
  private ImageButton newLoanButton,preLoanButton,profileButton,updatePassButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
    homeInit();
    newLoanButton.setOnClickListener(this);
    preLoanButton.setOnClickListener(this);
    profileButton.setOnClickListener(this);
    updatePassButton.setOnClickListener(this);

  }

  private void homeInit(){

    newLoanButton = findViewById(R.id.new_loan);
    preLoanButton = findViewById(R.id.image_pre_loans);
    profileButton = findViewById(R.id.image_profile);
    updatePassButton = findViewById(R.id.image_pass);
  }


  @Override
  public void onClick(View view) {

    switch (view.getId()){
      case R.id.new_loan:
        startActivity(new Intent(MainActivity.this, NewLoanActivity.class));
        break;
      case R.id.image_pre_loans:
        startActivity(new Intent(MainActivity.this, PreLoanActivity.class));
        break;

      case R.id.image_profile:
        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        break;

      case R.id.image_pass:
        startActivity(new Intent(MainActivity.this,UpdatePasswordActivity.class));
        break;

      default:
        startActivity(new Intent(MainActivity.this,MainActivity.class));

    }

  }
}
