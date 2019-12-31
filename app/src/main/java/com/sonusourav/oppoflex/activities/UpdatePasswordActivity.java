package com.sonusourav.oppoflex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import java.util.Objects;

public class UpdatePasswordActivity extends BaseActivity {

    private EditText newPassword;
    private FirebaseAuth updatePassAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference rootRef;
    private EditText oldPassword;
    private PreferenceManager updatePref;

    private ProgressBar updateProgressBar;
    private DrawerLayout drawerLayout;
    private Button updateButton;
    private FrameLayout updateFrameLayout;
    DatabaseReference userRef;
    private String email;
    private String TAG= UpdatePasswordActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_password, null, false);
        updatePassInit(contentView);
        drawerLayout.addView(contentView, 0);

        if(updatePref.isPassUpdated()){
            updateFrameLayout.setVisibility(View.VISIBLE);
            Log.d(TAG+"  IsPasswordUpdated","Updated");
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProgressBar.setVisibility(View.VISIBLE);
                updateButton.setEnabled(false);
                final String newPass =newPassword.getText().toString().trim();


                    if(newPass.isEmpty() ){

                        updateProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"New Password is Empty .",Toast.LENGTH_SHORT).show();
                        updateButton.setEnabled(true);
                    }
                    else if(newPass.length()<7){
                        updateProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Password should not contain less than 6 characters .",Toast.LENGTH_SHORT).show();
                        updateButton.setEnabled(true);

                    }else
                        if(!updatePref.isPassUpdated()){

                            Log.d(TAG+"  FirstPassChange","Reaching");

                            AuthCredential
                                credential = EmailAuthProvider.getCredential(email, newPass);
                            assert updatePassAuth.getCurrentUser()!=null;
                            updatePassAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "linkWithCredential:success");

                                        userRef=rootRef.child(encodeUserEmail(Objects.requireNonNull(firebaseUser.getEmail()))).child("pass");
                                        userRef.setValue(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                updatePref.setIsPassUpdated(true);
                                                updateProgressBar.setVisibility(View.GONE);
                                                Log.d(TAG + "UpdatePassword", "First Time Password updated");
                                                startActivity(new Intent(UpdatePasswordActivity.this,MainActivity.class));


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG+"OnFailure", e.getLocalizedMessage());
                                                Toast.makeText(getApplicationContext(),"Failed to update password.\nPlease try again.",Toast.LENGTH_SHORT).show();
                                                updateProgressBar.setVisibility(View.GONE);
                                                updateButton.setEnabled(true);

                                            }
                                        });

                                    } else {
                                        Log.w(TAG, "linkWithCredential:failure", task.getException());

                                        Toast.makeText(getApplicationContext(),"Failed to update password.\nPlease try again.",Toast.LENGTH_SHORT).show();
                                        updateProgressBar.setVisibility(View.GONE);
                                        updateButton.setEnabled(true);

                                    }

                                }
                            });

                    }
                else {

                    String oldPass =oldPassword.getText().toString().trim();

                    if(oldPass.isEmpty() ){

                        updateProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Old Password is Empty .",Toast.LENGTH_SHORT).show();
                        updateButton.setEnabled(true);
                    }
                    assert email != null;
                    AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    updatePref.setLoginCredentials(firebaseUser.getEmail(),newPass,"emailAuth");


                                                    userRef=rootRef.child(encodeUserEmail(email)).getRef();
                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull
                                                            DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                dataSnapshot.getRef().child("pass").setValue(newPass);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull
                                                            DatabaseError databaseError) {

                                                            Toast.makeText(getApplicationContext(),"Database update failed",Toast.LENGTH_SHORT).show();
                                                            updateButton.setEnabled(true);
                                                            finish();
                                                        }
                                                    });


                                                    Toast.makeText(getApplicationContext(),"Password successfully updated",Toast.LENGTH_SHORT).show();
                                                    Log.d("Update Password", "Password updated");
                                                    startActivity(new Intent(UpdatePasswordActivity.this,MainActivity.class));
                                                } else {
                                                    Toast.makeText(getApplicationContext(),"Password update failed. Try Again!",Toast.LENGTH_SHORT).show();
                                                    updateButton.setEnabled(true);
                                                    Log.d("Update Password", "Error password not updated");
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Error : Wrong old password.",Toast.LENGTH_SHORT).show();
                                        updateButton.setEnabled(true);
                                        Log.d("Update Password", "Error auth failed");
                                    }

                                    updateProgressBar.setVisibility(View.GONE);}
                            });
                }
            }
        });

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateButton.setEnabled(true);
    }

    private void updatePassInit(View contentView){
        updateButton = contentView.findViewById(R.id.btnUpdatePassword);
        newPassword =  contentView.findViewById(R.id.etNewPassword);
        oldPassword =  contentView.findViewById(R.id.etOldPassword);
        drawerLayout= findViewById(R.id.drawer_layout);
        updateProgressBar= contentView.findViewById(R.id.update_progress_bar);
        updateFrameLayout= contentView.findViewById(R.id.update_frame1);

        updatePassAuth= FirebaseAuth.getInstance();
        firebaseUser =updatePassAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference().child("Users");
        assert firebaseUser != null;
        email = firebaseUser.getEmail();
        updatePref = new PreferenceManager(this);

    }

}
