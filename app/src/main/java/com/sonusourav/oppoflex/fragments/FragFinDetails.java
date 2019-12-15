package com.sonusourav.oppoflex.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import com.sonusourav.oppoflex.Dao.FinDetailsDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.DialogUtils;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragFinDetails extends Fragment implements View.OnClickListener, View.OnTouchListener{

  private StepsView stepsView;
  private EditText empFirstName,empLastName,depart,design,exp,salary;
  private TextView dor;
  private Button next, saveAsDraft;
  private DialogUtils dialogUtils;
  private Calendar calendar;

  private FirebaseDatabase firebaseDatabase;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference baseRef, childRef;

  private PreferenceManager finDetailsPref;
  private FinDetailsDao finDetails;
  private String value="";

  public FragFinDetails(){}

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @SuppressLint("ClickableViewAccessibility") @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_fin_details, null);

    init(view);
    stepsView.setLabels(new String[]{"","",""})
        .setBarColorIndicator(Objects.requireNonNull(getContext()).getResources().getColor(R.color.grey))
        .setProgressColorIndicator(getContext().getResources().getColor(R.color.white))
        .setLabelColorIndicator(getContext().getResources().getColor(R.color.white))
        .setCompletedPosition(1)
        .drawView();

    next.setOnClickListener(this);
    saveAsDraft.setOnClickListener(this);
    dor.setOnClickListener(this);
    empFirstName.setOnTouchListener(this);
    empLastName.setOnTouchListener(this);
    depart.setOnTouchListener(this);
    design.setOnTouchListener(this);
    salary.setOnTouchListener(this);
    exp.setOnTouchListener(this);
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
    String myFormat = "dd/MM/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    dor.setText(sdf.format(calendar.getTime()));
  }
  
  private void init(View view){

    stepsView= Objects.requireNonNull(getActivity()).findViewById(R.id.stepsView);
    empFirstName=view.findViewById(R.id.emp_first_name);
    empLastName=view.findViewById(R.id.emp_last_name);
    depart=view.findViewById(R.id.depart);
    dor=view.findViewById(R.id.dor);
    design=view.findViewById(R.id.designation);
    exp=view.findViewById(R.id.experience);
    salary=view.findViewById(R.id.net_salary);
    next=getActivity().findViewById(R.id.next);
    saveAsDraft=getActivity().findViewById(R.id.save_as_draft);
    dialogUtils=new DialogUtils(getActivity(),"Sending details");
    calendar=Calendar.getInstance();

    finDetailsPref=new PreferenceManager(getActivity());
    firebaseAuth=FirebaseAuth.getInstance();
    firebaseDatabase=FirebaseDatabase.getInstance();
    baseRef=firebaseDatabase.getReference();

    if(getArguments()!=null){
      if(getArguments().getString("draftLoan").equals("draftLoan") && Integer.parseInt(finDetailsPref.getDraftLevel())>1){
        FinDetailsDao perDetailsDao=finDetailsPref.getFinDetails();
        empFirstName.setText(perDetailsDao.getEmpFirstName());
        empLastName.setText(perDetailsDao.getEmpLastName());
        depart.setText(perDetailsDao.getDept());
        dor.setText(perDetailsDao.getDor());
        design.setText(perDetailsDao.getDesignation());
        exp.setText(perDetailsDao.getExp());
        salary.setText(perDetailsDao.getSalary());
        value="draftLoan";
      }
    }


  }

  private void sendFinancialDetails(){

    dialogUtils.showProgressDialog();
    saveAsDraft();
    
        childRef=baseRef.child("DraftLoans").child(encodeUserEmail(
        Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))).child("financialInfo");  
    
    childRef.setValue(finDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){
          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Financial Details saved",Toast.LENGTH_SHORT).show();
          loadFragment(new FragLoanDetails(),value);

        }else
        {
          dialogUtils.hideProgressDialog();
          Toast.makeText(getActivity(),"Failed to saved",Toast.LENGTH_SHORT).show();

        }
      }
    });

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

  private void saveAsDraft(){

    finDetailsPref.setFinDetails(finDetails);
    finDetailsPref.setDraftLevel("2");
    Toast.makeText(getActivity(),"Saved successfully",Toast.LENGTH_SHORT).show();


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

    if(salary.getText().length()>=12){
      salary.requestFocus();
      salary.setError("Is you salary really beyond 12 digits",null);
      return false;
    }

    finDetails=new FinDetailsDao(empFirstName.getText().toString(),empLastName.getText().toString(),
        depart.getText().toString(),dor.getText().toString(),design.getText().toString(),
        exp.getText().toString(),salary.getText().toString());

    finDetailsPref.getDraftLoan().setFinDetailsDao(finDetails);
    
    return true;
  }

  private static String encodeUserEmail(String userEmail) {
    return userEmail.replace(".", ",");
  }

  @Override public void onClick(View v) {

    switch (v.getId()){
      case R.id.next:
        if(checkIfFilled(empFirstName,empLastName,depart,design,exp,salary)){
          sendFinancialDetails();
        }
        break;
      case R.id.save_as_draft:
        if(checkIfFilled(empFirstName,empLastName,depart,design,exp,salary)){
          saveAsDraft();
        }
        break;
      case R.id.dor:
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
          empFirstName.setText(result.get(0));
          empFirstName.setSelection(result.get(0).length());
          empFirstName.requestFocus();
          empFirstName.setError(null);
        }
        break;
      case 92:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          empLastName.setText(result.get(0));
          empLastName.setSelection(result.get(0).length());
          empLastName.requestFocus();
          empLastName.setError(null);
        }
        break;
      case 93:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          depart.setText(result.get(0));
          depart.setSelection(result.get(0).length());
          depart.requestFocus();
          depart.setError(null);

        }
        break;
      case 94:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          design.setText(result.get(0));
          design.setSelection(result.get(0).length());
          design.requestFocus();
          design.setError(null);

        }
        break;
      case 95:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          exp.setText(result.get(0));
          exp.setSelection(result.get(0).length());
          exp.requestFocus();
          exp.setError(null);

        }
        break;
      case 96:
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> result = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          salary.setText(result.get(0));
          salary.setSelection(result.get(0).length());
          salary.requestFocus();
          salary.setError(null);

        }
        break;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {

    final int DRAWABLE_RIGHT = 2;
    if(event.getAction() == MotionEvent.ACTION_UP) {

      switch (v.getId()){
        case R.id.emp_first_name:
          if(event.getRawX() >= (empFirstName.getRight() - empFirstName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(91);
            return true;
          }
          break;
        case R.id.emp_last_name:
          if(event.getRawX() >= (empLastName.getRight() - empLastName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(92);
            return true;
          }
          break;
        case R.id.depart:
          if(event.getRawX() >= (depart.getRight() - depart.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(93);
            return true;
          }
          break;
        case R.id.designation:
          if(event.getRawX() >= (design.getRight() - design.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(94);
            return true;
          }
        case R.id.experience:
          if(event.getRawX() >= (exp.getRight() - exp.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(95);
            return true;
          }
          break;
        case R.id.net_salary:
          if(event.getRawX() >= (salary.getRight() - salary.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())-100) {
            promptSpeechInput(96);
            return true;
          }
          break;
      }
    }
    return false;
  }
}
