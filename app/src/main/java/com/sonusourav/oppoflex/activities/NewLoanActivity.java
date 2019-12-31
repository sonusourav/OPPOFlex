package com.sonusourav.oppoflex.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.fragments.FragFinDetails;
import com.sonusourav.oppoflex.fragments.FragLoanDetails;
import com.sonusourav.oppoflex.fragments.FragPersonalDetails;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewLoanActivity extends AppCompatActivity {

  FragmentManager fragmentManager;
  private int PERMISSION_REQUEST_CODE = 101;
  private String value="";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_loan);
    init();
    if (!checkPermission()) {
      requestPermission();
    }

    Intent intent= getIntent();
    String draftLevel=intent.getStringExtra("draftLevel");
    Log.d("newLoanActivity",intent.getStringExtra("draftLevel")+"");

    if (draftLevel != null) {
      Log.d("newLoanActivity","draftNotNull");
      if( !draftLevel.isEmpty()){
        Log.d("newLoanActivity","draftNotEmpty");
        int level=Integer.parseInt(draftLevel);
        initialLoadFrag(level);
      }else{
        Log.d("newLoanActivity","draftIsEmpty");
        loadFragment(new FragPersonalDetails(),"");
      }
    }else{
      Log.d("newLoanActivity","draftIsNull");
      loadFragment(new FragPersonalDetails(),"");
    }
  }

  private void init(){
    fragmentManager=getSupportFragmentManager();
  }


  private void initialLoadFrag(int draftLevel){

    Log.d("NewLoanActivity","reaching new loan activity");
    value="draftLoan";
    switch (draftLevel){
      case 1:
        loadFragment(new FragPersonalDetails(),value);
        break;
      case 2:
        loadFragment(new FragFinDetails(),value);
        break;
      case 3:
        loadFragment(new FragLoanDetails(),value);
        break;
        default:
          loadFragment(new FragPersonalDetails(),"");
          break;
    }
  }

  private void loadFragment(Fragment fragment,String value) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    Bundle args = new Bundle();
    args.putString("draftLoan", value);
    fragment.setArguments(args);
    transaction.replace(R.id.frame_layout, fragment);
    transaction.commit();
  }

  private boolean checkPermission() {
    int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
    int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
    int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
    return (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED);
  }

  private void requestPermission() {
    ActivityCompat.requestPermissions(this, new String[] { RECORD_AUDIO,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE },
        PERMISSION_REQUEST_CODE);

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
      int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0) {

        boolean allGranted = true;
        for (int grantResult : grantResults) {
          if (grantResult != PackageManager.PERMISSION_GRANTED) {
            allGranted = false;
            break;
          }
        }
        if (allGranted) {
          Toast.makeText(getApplicationContext(), "Permission granted",
              Toast.LENGTH_SHORT).show();
          loadFragment(new FragPersonalDetails(),value);
        } else {
          Toast.makeText(getApplicationContext(), "Permission Denied",
              Toast.LENGTH_SHORT).show();

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(RECORD_AUDIO) || shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
              showMessageOKCancel("The app needs audio and storage permissions",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      requestPermissions(new String[] { RECORD_AUDIO,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE },
                          PERMISSION_REQUEST_CODE);
                    }
                  });
            }
          }
        }
      }
    }
  }

  private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(NewLoanActivity.this)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", null)
        .create()
        .show();
  }

  @Override
  public void onBackPressed() {
     super.onBackPressed();
  }
}

