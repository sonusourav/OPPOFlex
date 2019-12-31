package com.sonusourav.oppoflex.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import com.sonusourav.oppoflex.R;

public class DialogUtils {
  
  private Activity context;
  private String message;
  private ProgressDialog progressDialog;

  public DialogUtils(Activity context,String msg){
    this.context=context;
    this.message=msg;
    progressDialog=new ProgressDialog(context);
    progressDialog.setProgressStyle(R.style.MyAlertDialogStyle);

  }

  public void showProgressDialog() {

    if (progressDialog == null) {
      progressDialog=new ProgressDialog(context);
      progressDialog.setProgressStyle(R.style.MyAlertDialogStyle);
      progressDialog.setMessage(message);
      progressDialog.setIndeterminate(true);
    }
    progressDialog.show();
  }

  public void hideProgressDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }
}
