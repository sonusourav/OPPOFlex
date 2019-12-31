package com.sonusourav.oppoflex.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PdfConverter;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.anton46.stepsview.StepsView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sonusourav.oppoflex.Dao.LoanDetailsDao;
import com.sonusourav.oppoflex.Dao.PreLoanDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.DialogUtils;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.activities.PdfActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragLoanDetails extends Fragment implements View.OnClickListener, View.OnTouchListener{

  private StepsView stepsView;
  private EditText cost,loanAmount,loanPurpose,tenure;
  private Spinner repayment;
  private Button next, saveAsDraft;
  private DialogUtils dialogUtils;

  private FirebaseAuth firebaseAuth;
  private DatabaseReference baseRef;

  private PreferenceManager loanDetailsPref;
  private LoanDetailsDao loanDetails;
  private PreLoanDao previousLoan;

  private File gpxfile = null;
  private String pathName;
  private File root;
  private String i1;

  public FragLoanDetails(){}

  private static Handler handler = new Handler();
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @SuppressLint("ClickableViewAccessibility") @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_loan_details, null);

    init(view);
    stepsView.setLabels(new String[]{"","",""})
        .setBarColorIndicator(Objects.requireNonNull(getContext()).getResources().getColor(R.color.grey))
        .setProgressColorIndicator(getContext().getResources().getColor(R.color.white))
        .setLabelColorIndicator(getContext().getResources().getColor(R.color.white))
        .setCompletedPosition(2)
        .drawView();

    next.setOnClickListener(this);
    saveAsDraft.setOnClickListener(this);
    cost.setOnTouchListener(this);
    loanAmount.setOnTouchListener(this);
    loanPurpose.setOnTouchListener(this);
    tenure.setOnTouchListener(this);

    return view;
  }
  
  private void init(View view){

    stepsView= Objects.requireNonNull(getActivity()).findViewById(R.id.stepsView);
    cost=view.findViewById(R.id.property_cost);
    loanAmount=view.findViewById(R.id.loan_amount);
    loanPurpose=view.findViewById(R.id.loan_purpose);
    repayment=view.findViewById(R.id.repayment);
    tenure=view.findViewById(R.id.tenure);
    next=getActivity().findViewById(R.id.next);
    saveAsDraft=getActivity().findViewById(R.id.save_as_draft);
    dialogUtils=new DialogUtils(getActivity(),"Sending details");
    
    loanDetailsPref=new PreferenceManager(getActivity());
    firebaseAuth=FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    baseRef= firebaseDatabase.getReference();

    i1 = loanDetailsPref.getDraftLoan().getLoanId();
    pathName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/OPPOFLEX/loanNo"+i1+".pdf";
    Log.d("FragLoanDetails",pathName);


    if(getArguments()!=null){
      Log.d("FragLoanDetails","arguments not null");

      if(getArguments().getString("draftLoan")!=null) {

        Log.d("FragLoanDetails", "bundle not null");
        Log.d("FragLoanDetails", loanDetailsPref.getDraftLevel());
        Log.d("FragLoanDetails", getArguments().getString("draftLoan"));

        if(getArguments().getString("draftLoan").equals("draftLoan") && Integer.parseInt(loanDetailsPref.getDraftLevel())==3){
          LoanDetailsDao perDetailsDao=loanDetailsPref.getLoanDetails();
          cost.setText(perDetailsDao.getPropertyCost());
          loanAmount.setText(perDetailsDao.getLoanAmount());
          loanPurpose.setText(perDetailsDao.getLoanPurpose());
          tenure.setText(perDetailsDao.getTenure());
        }

      }
    }

  }

  private void sendLoanDetails(){

    dialogUtils.showProgressDialog();
    saveAsDraft();

    DatabaseReference childRef = baseRef.child("DraftLoans").child(encodeUserEmail(
        Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))).child("loanInfo"); 
    
    childRef.setValue(loanDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){
          addToPreviousLoans();
          Log.d("FragLoanDetails","taskSuccess");
        }else
        {
          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Failed to saved",Toast.LENGTH_SHORT).show();

        }
      }
    });
  }


  private void convertHtml2PDF(String src){

    File root = new File(Environment.getExternalStorageDirectory(), "OPPOFLEX");
    if (!root.exists()) {
      root.mkdirs();
    }
    if (root.exists()) {

      PdfConverter converter = PdfConverter.getInstance();
      File file = new File(root, "loanNo"+i1+".pdf");
      converter.convert(getActivity(), src, file);
      //dialogUtils.hideProgressDialog();

      notificationDialog();
      Log.d("FragLoanDetails",pathName);
      final Intent intent=new Intent(getActivity(), PdfActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra("filePath",pathName);

      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          dialogUtils.hideProgressDialog();
          startActivity(intent);
        }
      }, 4000);

    }
  }


  private void showAlertDialog(String title, String message) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    alertDialogBuilder.setTitle(title);
    alertDialogBuilder
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();
    // show it
    alertDialog.show();
  }


    private void addToPreviousLoans(){
    Log.d("FragLoanDetails","reaching addToPrevious");
    final DatabaseReference preLoansRef = baseRef.child("PreviousLoans").child(encodeUserEmail(
        Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))).push().getRef();

    preLoansRef.setValue(previousLoan).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){
          Toast.makeText(getActivity(),"Loan Details saved",Toast.LENGTH_SHORT).show();
          loanDetailsPref.setDraftLevel("0");

          String personalDet=getString(R.string.html_template);
          final String finalPersonalDet = replaceLoan(personalDet);

          handler.post(new Runnable() {
            @Override
            public void run() {
              Log.d("FragLoanDetails","convertHtl");

              convertHtml2PDF(finalPersonalDet);

            }
          });
        }else
        {
          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Failed to saved",Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void saveAsDraft(){
    loanDetailsPref.setLoanDetails(loanDetails);
    loanDetailsPref.setDraftLevel("3");
    Toast.makeText(getActivity(),"Saved successfully",Toast.LENGTH_SHORT).show();

  }

  private String replaceLoan(String personalDet){
    personalDet=personalDet.replace("applicant_name","value="+loanDetailsPref.getPerDetails().getFirstName() +" "+loanDetailsPref.getPerDetails().getLastName());
    personalDet=personalDet.replace("email_address","value="+loanDetailsPref.getPerDetails().getEmail());
    personalDet=personalDet.replace("date_of_birth","value="+loanDetailsPref.getPerDetails().getDob());
    personalDet=personalDet.replace("mobile_no","value="+loanDetailsPref.getPerDetails().getMobNo());
    personalDet=personalDet.replace("gender","value="+loanDetailsPref.getPerDetails().getGender());
    personalDet=personalDet.replace("address","value="+loanDetailsPref.getPerDetails().getAddress());

    personalDet=personalDet.replace("employer_name","value="+loanDetailsPref.getFinDetails().getEmpFirstName()+" "+loanDetailsPref.getFinDetails().getEmpLastName());
    personalDet=personalDet.replace("department_name","value="+loanDetailsPref.getFinDetails().getDept());
    personalDet=personalDet.replace("department_name","value="+loanDetailsPref.getFinDetails().getDept());
    personalDet=personalDet.replace("date_of_retirement","value="+loanDetailsPref.getFinDetails().getDor());
    personalDet=personalDet.replace("designation","value="+loanDetailsPref.getFinDetails().getDesignation());
    personalDet=personalDet.replace("experience","value="+loanDetailsPref.getFinDetails().getExp());
    personalDet=personalDet.replace("net_salary","value="+loanDetailsPref.getFinDetails().getSalary());

    personalDet=personalDet.replace("property_cost","value="+loanDetailsPref.getLoanDetails().getPropertyCost());
    personalDet=personalDet.replace("loan_amount","value="+loanDetailsPref.getLoanDetails().getLoanAmount());
    personalDet=personalDet.replace("loan_purpose","value="+loanDetailsPref.getLoanDetails().getLoanPurpose());
    personalDet=personalDet.replace("repayment","value="+loanDetailsPref.getLoanDetails().getRepayment());
    personalDet=personalDet.replace("tenure","value="+loanDetailsPref.getLoanDetails().getTenure());

    return personalDet;
  }

  private boolean checkIfFilled(EditText... inputText){

    for(EditText editText:inputText){
      String value=editText.getText().toString();
      if(value.isEmpty()){
        editText.requestFocus();
        editText.setError("Please fill this",null);
        return false;
      }
    }

    loanDetails=new LoanDetailsDao(cost.getText().toString(),loanAmount.getText().toString(),
        loanPurpose.getText().toString(),repayment.getSelectedItem().toString(),tenure.getText().toString());

    PreLoanDao preLoanDao=loanDetailsPref.getDraftLoan();
    previousLoan=new PreLoanDao(preLoanDao.getLoanId(),preLoanDao.getDate(),preLoanDao.getName(),preLoanDao.getEmail(),preLoanDao.getTitle()
    ,preLoanDao.getBankName(),preLoanDao.getLoanType(),preLoanDao.getImageUrl(),preLoanDao.getFileLocation()
        ,loanDetailsPref.getPerDetails(),loanDetailsPref.getFinDetails(),loanDetails);
    loanDetailsPref.getDraftLoan().setLoanDetailsDao(loanDetails);
    return true;
  }

  private static String encodeUserEmail(String userEmail) {
    return userEmail.replace(".", ",");
  }

  @Override public void onClick(View v) {

    switch (v.getId()){
      case R.id.next:
        if(checkIfFilled(cost,loanAmount,loanPurpose,tenure)){
          Log.d("FragLoanDetails","reaching");
          sendLoanDetails();
        }else
          Toast.makeText(getActivity(),"Fill all the fields",Toast.LENGTH_SHORT).show();

        break;
      case R.id.save_as_draft:
        if(checkIfFilled(cost,loanAmount,loanPurpose,tenure)){
          saveAsDraft();
        }else
          Toast.makeText(getActivity(),"Fill all the fields",Toast.LENGTH_SHORT).show();

        break;
    }
  }


  private void promptSpeechInput(int requestCode) {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
        getString(R.string.speech_prompt));
    try {
      startActivityForResult(intent, requestCode);
    } catch (ActivityNotFoundException a) {
      Toast.makeText(getActivity(),
          getString(R.string.speech_not_supported),
          Toast.LENGTH_SHORT).show();
    }
  }


  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case 91:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          cost.setText(result.get(0));
          cost.setSelection(result.get(0).length());
          cost.setError(null);

        }
        break;
      case 92:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          loanAmount.setText(result.get(0));
          loanAmount.setSelection(result.get(0).length());
          loanAmount.requestFocus();
          loanAmount.setError(null);

        }
        break;
      case 93:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          loanPurpose.setText(result.get(0));
          loanPurpose.setSelection(result.get(0).length());
          loanPurpose.requestFocus();
          loanPurpose.setError(null);

        }
        break;
      case 94:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          tenure.setText(result.get(0));
          tenure.setSelection(result.get(0).length());
          tenure.requestFocus();
          tenure.setError(null);

        }
        break;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    final int DRAWABLE_RIGHT = 2;

    if(event.getAction() == MotionEvent.ACTION_UP) {

      switch (v.getId()){
        case R.id.property_cost:
          if(event.getRawX() >= (cost.getRight() - cost.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(91);
            return true;
          }
          break;
        case R.id.loan_amount:
          if(event.getRawX() >= (loanAmount.getRight() - loanAmount.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(92);
            return true;
          }
          break;
        case R.id.loan_purpose:
          if(event.getRawX() >= (loanPurpose.getRight() - loanPurpose.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(93);
            return true;
          }
          break;
        case R.id.tenure:
          if(event.getRawX() >= (tenure.getRight() - tenure.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(94);
            return true;
          }
          break;
      }
    }
    return false;
  }

  private void notificationDialog() {
    NotificationManager notificationManager = (NotificationManager) Objects.requireNonNull(
        getActivity()).getSystemService(Context.NOTIFICATION_SERVICE);
    String NOTIFICATION_CHANNEL_ID = "oppoflex_01";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      @SuppressLint("WrongConstant")
      NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "PDF_completion", NotificationManager.IMPORTANCE_DEFAULT);
      notificationChannel.setDescription(i1);
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.RED);
      notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
      notificationChannel.enableVibration(true);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(notificationChannel);
      }

      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);
      notificationBuilder.setAutoCancel(true)
          .setDefaults(Notification.DEFAULT_ALL)
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(R.mipmap.icon_logo)
          .setTicker("OppoFlex")
          .setContentTitle("Your PDF is ready")
          .setContentText("Your pdf is stored at "+ i1 +" .pdf in OppofFlex Directory")
          .setContentInfo("Open pdf")
      ;
    }

    Log.d("FragLoanDetails",pathName);
    Intent intent=new Intent(getActivity(), PdfActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("filePath",pathName);
    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);
    notificationBuilder.setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.mipmap.icon_logo)
        .setTicker("OppoFlex")
        .setPriority(Notification.PRIORITY_MAX)
        .setContentTitle("Your PDF is ready")
        .setContentText("Your pdf is stored at "+ i1 +" .pdf in OppofFlex Directory")
        .setContentInfo("Open pdf")
        .setContentIntent(pendingIntent)
    ;

    loanDetailsPref.setDraftLoan(null);
    loanDetailsPref.setLoanDetails(null);
    loanDetailsPref.setFinDetails(null);
    loanDetailsPref.setPerDetails(null);

    notificationManager.notify(1, notificationBuilder.build());
  }


}
