package com.sonusourav.oppoflex.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sonusourav.oppoflex.Dao.UserDao;
import com.sonusourav.oppoflex.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EditInfoActivity extends AppCompatActivity {

    private EditText infoName;
    private EditText infoEmail;
    private EditText infoMobile;
    private EditText infoDob;
    private Spinner infoGender;
    private Button infoSaveBtn;
    private ProgressDialog infoProgress;
    ArrayAdapter<String> spinnerArrayAdapter1;
    private Calendar myCalendar;
    private TextWatcher textWatcher;
    private AdapterView.OnItemSelectedListener spinnerListener;

    private DatabaseReference infoUserRef;
    private FirebaseUser firebaseUser;

    String proName;
    String proEmail;
    String proMob;
    String proDob;
    String proGender;
    Boolean textChanged=false;
    int pos1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        editInfoInit();

        showProgressDialog();
        fetchUserData();

        infoSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateInput()){

                    showProgressDialog();
                    updateUserData();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        infoDob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(EditInfoActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        infoDob.setText(sdf.format(myCalendar.getTime()));
    }


    private void fetchUserData(){

        infoUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    UserDao userDao =dataSnapshot.getValue(UserDao.class);
                    if (userDao != null) {
                        infoName.setText(userDao.getName().trim());
                        infoEmail.setText(userDao.getEmail().trim());
                        infoMobile.setText(userDao.getMobile().trim());
                        infoDob.setText(userDao.getDob().trim());
                        pos1=spinnerArrayAdapter1.getPosition(userDao.getGender());
                        infoGender.setSelection(pos1);}

                    hideProgressDialog();

                    infoDob.addTextChangedListener(textWatcher);
                    infoName.addTextChangedListener(textWatcher);
                    infoEmail.addTextChangedListener(textWatcher);
                    infoMobile.addTextChangedListener(textWatcher);

                    infoGender.setOnItemSelectedListener(spinnerListener);
                }else {
                    Toast.makeText(getApplicationContext(),"User data does not exist",Toast.LENGTH_SHORT).show();
                    hideProgressDialog();

                    infoName.addTextChangedListener(textWatcher);
                    infoEmail.addTextChangedListener(textWatcher);
                    infoDob.addTextChangedListener(textWatcher);
                    infoMobile.addTextChangedListener(textWatcher);
                    infoGender.setOnItemSelectedListener(spinnerListener);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hideProgressDialog();
                Toast.makeText(getApplicationContext(),"Failed to fetch user data",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void editInfoInit(){

        infoName=findViewById(R.id.pro_edit_name);
        infoEmail=findViewById(R.id.pro_edit_email);
        infoMobile=findViewById(R.id.pro_edit_mob);
        infoDob=findViewById(R.id.pro_edit_dob);
        infoGender=findViewById(R.id.pro_edit_gender);
        infoSaveBtn=findViewById(R.id.pro_edit_save);

        myCalendar =Calendar.getInstance();
        infoSaveBtn.setEnabled(false);
        FirebaseAuth infoAuth = FirebaseAuth.getInstance();
        FirebaseUser infoUser = infoAuth.getCurrentUser();
        assert infoUser!=null;
        FirebaseDatabase infoInstance = FirebaseDatabase.getInstance();
        DatabaseReference infoRootRef = infoInstance.getReference().child("Users");
        infoUserRef= infoRootRef.child(encodeUserEmail(Objects.requireNonNull(infoUser.getEmail()))).getRef();

       spinnerArrayAdapter1 = new ArrayAdapter<>(
                this, R.layout.spinner_item1, getResources().getStringArray(R.array.gender)
        );
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_drop_down_item1);
        infoGender.setAdapter(spinnerArrayAdapter1);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged","reaching");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(!s.toString().isEmpty()){
                    Log.d("onTextChanged",s.toString());

                    textChanged=true;
                    infoSaveBtn.setEnabled(true);
                }else if(s.equals("")){
                    textChanged=false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged","reaching");
            }
        };



        spinnerListener =new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

               if(parentView==infoGender && position!=pos1){
                   Log.d("onItemSelected","1reaching");

                   textChanged=true;
                   infoSaveBtn.setEnabled(true);
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        };
    }


    private boolean validateInput(){

         proName=infoName.getText().toString().trim();
         proEmail=infoEmail.getText().toString().trim();
         proMob=infoMobile.getText().toString().trim();
         proDob=infoDob.getText().toString().trim();
         proGender=infoGender.getSelectedItem().toString().trim();


        if(proName.isEmpty() ){
            infoName.setError("Name is empty");
            infoName.requestFocus();
            return false;
        }

        if(proEmail.isEmpty() ){
            infoEmail.setError("Email is empty");
            infoEmail.requestFocus();
            return false;
        }

        if(proMob.isEmpty() ){
            infoMobile.setError("Mobile No is empty");
            infoMobile.requestFocus();
            return false;
        }


        if(proMob.length()!=10 || !(proMob.matches("[0-9]+")) ){
            infoMobile.setError(" Invalid Mobile no");
            infoMobile.requestFocus();
            return false;
        }

        if(proDob.isEmpty() ){

            Toast.makeText(getApplicationContext(),"Please Select your Dob ",Toast.LENGTH_SHORT).show();
            infoDob.requestFocus();
            return false;
        }

        if(proGender.isEmpty() ){

            Toast.makeText(getApplicationContext(),"Please Select your gender ",Toast.LENGTH_SHORT).show();
            infoGender.requestFocus();
            return false;
        }

        return true;
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    private void updateUserData(){

        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put("name",proName);
        taskMap.put("mobile",proMob);
        taskMap.put("dob",proDob);
        taskMap.put("gender",proGender);


        infoUserRef.updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(proName)
                        .build();
                firebaseUser.updateProfile(request);
                hideProgressDialog();
                Toast.makeText(getApplicationContext(),"Profile successfully updated",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditInfoActivity.this,ProfileActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(),"Profile update failed",Toast.LENGTH_SHORT).show();

            }
        });
    }

    protected void onResume() {
        super.onResume();



    }

    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar infoActionBar = getSupportActionBar();
        assert infoActionBar != null;
        infoActionBar.setHomeButtonEnabled(true);
        infoActionBar.setDisplayHomeAsUpEnabled(true);
        infoActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5cae80")));
        infoActionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Update Profile</font>"));
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:

                if(textChanged){
                    backDialogBuilder();
                }else
                    NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;

    }


    public void backDialogBuilder() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // add the buttons
        builder
                .setMessage("You have unsaved changes. Do you want to discard?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        NavUtils.navigateUpFromSameTask(EditInfoActivity.this);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showProgressDialog() {

        if (infoProgress == null) {
            infoProgress = new ProgressDialog(this,R.style.MyAlertDialogStyle);
            infoProgress.setMessage("Updating your profile ....");
            infoProgress.setIndeterminate(true);
            infoProgress.setCanceledOnTouchOutside(false);
        }

        infoProgress.show();
    }

    public void hideProgressDialog() {
        if (infoProgress != null && infoProgress.isShowing()) {
            infoProgress.dismiss();
        }
    }

}
