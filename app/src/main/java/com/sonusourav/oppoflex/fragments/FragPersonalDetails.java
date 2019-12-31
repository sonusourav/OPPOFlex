package com.sonusourav.oppoflex.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.anton46.stepsview.StepsView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sonusourav.oppoflex.Dao.PerDetailsDao;
import com.sonusourav.oppoflex.Dao.PreLoanDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.DialogUtils;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class FragPersonalDetails extends Fragment implements View.OnClickListener, View.OnTouchListener{

  private StepsView stepsView;
  private EditText firstName,lastName,email,mobNo,address;
  private EditText dob;
  private Spinner gender;
  private Button next, saveAsDraft;
  private DialogUtils dialogUtils;
  private Calendar calendar;
  private SimpleDateFormat sdf;

  private FirebaseDatabase firebaseDatabase;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference baseRef, childRef;

  private PreferenceManager perDetailsPref;
  private PerDetailsDao personalDetails;
  private String id,value="";
  private String photoURL="";
  public FragPersonalDetails(){}

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @SuppressLint("ClickableViewAccessibility") @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_per_details, null);

    init(view);
    stepsView.setLabels(new String[]{"","",""})
        .setBarColorIndicator(Objects.requireNonNull(getContext()).getResources().getColor(R.color.grey))
        .setProgressColorIndicator(getContext().getResources().getColor(R.color.white))
        .setLabelColorIndicator(getContext().getResources().getColor(R.color.white))
        .setCompletedPosition(0)
        .drawView();

    next.setOnClickListener(this);
    saveAsDraft.setOnClickListener(this);
    dob.setOnClickListener(this);
    firstName.setOnTouchListener(this);
    lastName.setOnTouchListener(this);
    email.setOnTouchListener(this);
    mobNo.setOnTouchListener(this);
    address.setOnTouchListener(this);

    return view;
  }

  private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
        int dayOfMonth) {
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, monthOfYear);
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      updateLabel();
    }
  };

  private void updateLabel() {

    dob.setText(sdf.format(calendar.getTime()));
    dob.requestFocus();
    dob.setSelection(dob.getText().length());
  }


  private void init(View view){

    stepsView= Objects.requireNonNull(getActivity()).findViewById(R.id.stepsView);
    firstName=view.findViewById(R.id.first_name);
    lastName=view.findViewById(R.id.last_name);
    email=view.findViewById(R.id.email);
    dob=view.findViewById(R.id.dob);
    mobNo=view.findViewById(R.id.mobile_no);
    gender=view.findViewById(R.id.gender);
    address=view.findViewById(R.id.address);
    next=getActivity().findViewById(R.id.next);
    saveAsDraft=getActivity().findViewById(R.id.save_as_draft);
    dialogUtils=new DialogUtils(getActivity(),"Sending details");
    calendar=Calendar.getInstance();
    String myFormat = "dd/MM/yy";
    sdf = new SimpleDateFormat(myFormat, Locale.US);

    perDetailsPref=new PreferenceManager(getActivity());
    firebaseAuth=FirebaseAuth.getInstance();
    firebaseDatabase=FirebaseDatabase.getInstance();
    baseRef=firebaseDatabase.getReference();

    email.setText(perDetailsPref.getPrefEmail());
    dob.setText(sdf.format(calendar.getTime()));

    if(getArguments()!=null){
      Log.d("FragPersonalDetails","arguments not null");
      if(getArguments().getString("draftLoan")!=null){
        Log.d("FragPersonalDetails","bundle not null");
        Log.d("FragPersonalDetails",perDetailsPref.getDraftLevel());
        Log.d("FragPersonalDetails",getArguments().getString("draftLoan"));

        if(getArguments().getString("draftLoan").equals("draftLoan") && Integer.parseInt(perDetailsPref.getDraftLevel())==1){
          PerDetailsDao perDetailsDao=perDetailsPref.getPerDetails();
          firstName.setText(perDetailsDao.getFirstName());
          lastName.setText(perDetailsDao.getLastName());
          email.setText(perDetailsDao.getEmail());
          dob.setText(perDetailsDao.getDob());
          mobNo.setText(perDetailsDao.getMobNo());
          address.setText(perDetailsDao.getAddress());
        }
      }

    }

  }

  private void loadFragment(Fragment fragment,String value) {
    FragmentTransaction transaction = Objects.requireNonNull(getActivity())
        .getSupportFragmentManager().beginTransaction();
    Bundle args = new Bundle();
    args.putString("draftLoan", value);
    fragment.setArguments(args);
    transaction.replace(R.id.frame_layout, fragment);
    transaction.commit();
  }

  private void sendPersonalDetails(){

    dialogUtils.showProgressDialog();
    saveAsDraft();

    childRef=baseRef.child("DraftLoans").child(encodeUserEmail(
        Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))).child("personalInfo");
        childRef.setValue(personalDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){

          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Personal Details saved",Toast.LENGTH_SHORT).show();
          loadFragment(new FragFinDetails(),value);

        }else
        {
          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Failed to saved",Toast.LENGTH_SHORT).show();

        }
      }
    });


  }

  private void saveAsDraft(){

    perDetailsPref.setPerDetails(personalDetails);
    perDetailsPref.setDraftLevel("1");
    Toast.makeText(getActivity(),"Saved successfully",Toast.LENGTH_SHORT).show();
    value="draftLoan";

    if(firebaseAuth.getCurrentUser()!=null){
      if(firebaseAuth.getCurrentUser().getPhotoUrl()!=null){
        photoURL=firebaseAuth.getCurrentUser().getPhotoUrl().toString();
      }

    }
    id = (int)((Math.random() * ((1000000 - 100) + 1)) + 100 )+"";
    String pathName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OPPOFLEX/loanNo"+id+".pdf";
    Log.d("FragPersonalDetails",pathName);

    PreLoanDao preLoanDao=new PreLoanDao(id,sdf.format(calendar.getTime()),(firstName.getText().toString().trim()+lastName.getText().toString().trim()),email.getText().toString().trim(),
        perDetailsPref.getLoanProvider() + " "+ perDetailsPref.getLoanType(),perDetailsPref.getLoanProvider(),perDetailsPref.getLoanType(),photoURL,pathName,personalDetails);

    perDetailsPref.setDraftLoan(preLoanDao);
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

    if(!validateEmail(email.getText().toString().replaceAll(" ",""))){
      email.setError("Check your Email",null);
      return false;
    }

    if(mobNo.getText().toString().replaceAll(" ","").trim().length()>10){
      mobNo.requestFocus();
      mobNo.setError("Check you mob no",null);
      return false;
    }

    personalDetails=new PerDetailsDao(firstName.getText().toString(),lastName.getText().toString(),
        email.getText().toString(),dob.getText().toString(),
        gender.getSelectedItem().toString(),mobNo.getText().toString(),address.getText().toString());

    return true;
  }

  private boolean validateEmail(String email) {

    Pattern pattern;
    Matcher matcher;
    String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    pattern = Pattern.compile(EMAIL_PATTERN);
    matcher = pattern.matcher(email);
    return matcher.matches();
  }

  private static String encodeUserEmail(String userEmail) {
    return userEmail.replace(".", ",");
  }

  @Override public void onClick(View v) {

    switch (v.getId()){
      case R.id.next:
        if(checkIfFilled(firstName,lastName,email,dob,mobNo,address)){
          sendPersonalDetails();
        }
        break;
      case R.id.save_as_draft:
        if(checkIfFilled(firstName,lastName,email,dob,mobNo,address)){
          saveAsDraft();
        }
        break;
      case R.id.dob:
        new DatePickerDialog(Objects.requireNonNull(getActivity()), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show();
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

  /**
   * Receiving speech input
   * */
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case 91:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          firstName.setText(result.get(0));
          firstName.setSelection(result.get(0).length());
          firstName.requestFocus();
          firstName.setError(null);

        }
        break;
      case 92:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          lastName.setText(result.get(0));
          lastName.setSelection(result.get(0).length());
          lastName.requestFocus();
          lastName.setError(null);
        }
        break;
      case 93:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          email.setText(result.get(0).replaceAll(" ","").trim());
          email.setSelection(result.get(0).replaceAll(" ","").length());
          email.requestFocus();
          email.setError(null);
        }
        break;
      case 94:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          mobNo.setText(result.get(0).replaceAll(" ","").trim());
          mobNo.setSelection(result.get(0).replaceAll(" ","").trim().length());
          mobNo.requestFocus();
          mobNo.setError(null);

        }
        break;
      case 95:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          address.setText(result.get(0));
          address.setSelection(result.get(0).length());
          address.requestFocus();
          address.setError(null);

        }
        break;

    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    final int DRAWABLE_RIGHT = 2;

    if(event.getAction() == MotionEvent.ACTION_UP) {

      switch (v.getId()){
        case R.id.first_name:
          if(event.getRawX() >= (firstName.getRight() - firstName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(91);
            return true;
          }
          break;
        case R.id.last_name:
          if(event.getRawX() >= (lastName.getRight() - lastName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(92);
            return true;
          }
          break;
        case R.id.email:
          if(event.getRawX() >= (email.getRight() - email.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(93);
            return true;
          }
          break;
        case R.id.mobile_no:
          if(event.getRawX() >= (mobNo.getRight() - mobNo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(94);
            return true;
          }
        case R.id.address:
          if(event.getRawX() >= (address.getRight() - address.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(95);
            return true;
          }
          break;
      }
    }
    return false;
  }
}
