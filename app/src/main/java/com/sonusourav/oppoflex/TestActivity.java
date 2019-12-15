package com.sonusourav.oppoflex;

import android.os.Bundle;
import com.sonusourav.oppoflex.activities.BaseActivity;

public class TestActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getLayoutInflater().inflate(R.layout.fragment_loan_details, frameLayout);

  }
}
