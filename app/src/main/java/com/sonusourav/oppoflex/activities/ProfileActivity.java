package com.sonusourav.oppoflex.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sonusourav.oppoflex.Dao.UserDao;
import com.sonusourav.oppoflex.R;
import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser profileUser;
    private DatabaseReference profileUserRef;
    private StorageReference ref;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST1 = 70;
    private final int PICK_IMAGE_REQUEST2 = 41;

    private ImageButton profileEditBtn;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileMobile;
    private TextView profileDob;
    private TextView profileGender;
    private ProgressDialog profileDialog;
    private ImageView profilePic;
    private ImageView profileCoverPic;
    private DrawerLayout drawerLayout;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        profileInit(contentView);
        drawerLayout.addView(contentView, 0);
        showProgressDialog();
        updateProfile();

        profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialogButtonClicked(profileEditBtn);

            }
        });


    }

    public void showAlertDialogButtonClicked(View view) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Update");

        // add the buttons
        builder
                .setMessage("What do you want to update?")
                .setCancelable(true)
                .setPositiveButton("Profile pic", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        chooseImage(PICK_IMAGE_REQUEST1);
                        dialog.cancel();


                    }
                }).setNeutralButton("Cover pic", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                chooseImage(PICK_IMAGE_REQUEST2);
                dialog.cancel();

            }
        })
                .setNegativeButton("Profile Info", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        startActivity(new Intent(ProfileActivity.this,EditInfoActivity.class));
                        dialog.cancel();
                    }
                });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void profileInit(View contentView){

        profileEditBtn=contentView.findViewById(R.id.profile_edit);
        profileName=contentView.findViewById(R.id.profile_name);
        profileEmail=contentView.findViewById(R.id.profile_email);
        profileDob=contentView.findViewById(R.id.profile_dob);
        profileMobile=contentView.findViewById(R.id.profile_mobile);
        profileGender=contentView.findViewById(R.id.profile_gender);
        profilePic=contentView.findViewById(R.id.profile_pic);
        profileCoverPic=contentView.findViewById(R.id.profile_cover_pic);
        drawerLayout = findViewById(R.id.drawer_layout);

        FirebaseAuth profileAuth = FirebaseAuth.getInstance();
        profileUser = profileAuth.getCurrentUser();
        FirebaseDatabase profileInstance = FirebaseDatabase.getInstance();
        DatabaseReference profileRootRef = profileInstance.getReference().child("Users");
        assert profileUser != null;
        profileUserRef= profileRootRef.child(encodeUserEmail(Objects.requireNonNull(profileUser.getEmail()))).getRef();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child(encodeUserEmail(profileUser.getEmail()));

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            NavUtils.navigateUpFromSameTask(this);

        }
    }

    private void updateProfile(){

        profileUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserDao profileUserClass=dataSnapshot.getValue(UserDao.class);
                if (profileUserClass != null) {


                    String Uname=profileUserClass.getName().trim();
                    String Uemail=profileUserClass.getEmail().trim();
                    String Udob=profileUserClass.getDob().trim();
                    String Umob=profileUserClass.getMobile().trim();
                    String Ugender=profileUserClass.getGender().trim();
                    String UprofilePic=profileUserClass.getProfilePic().trim();
                    String UcoverPic=profileUserClass.getCoverPic().trim();

                    if(!Uname.isEmpty())
                        profileName.setText(Uname);

                    if(!Uemail.isEmpty())
                        profileEmail.setText(Uemail);

                    if(!Umob.isEmpty())
                        profileMobile.setText(Umob);

                    if(!Udob.isEmpty())
                        profileDob.setText(Udob);

                    if(!Ugender.isEmpty())
                        profileGender.setText(Ugender);

                    if(UprofilePic==null)
                        UprofilePic="";

                    if(UcoverPic==null)
                        UcoverPic="";

                    RequestOptions requestOption1 = new RequestOptions()
                    .placeholder(R.drawable.image_profile_pic)
                    .error(R.drawable.image_profile_pic)
                    .fitCenter();


                    RequestOptions requestOption2 = new RequestOptions()
                            .placeholder(R.drawable.image_profile)
                            .error(R.drawable.image_profile)
                            .fitCenter();


                    if(!UprofilePic.isEmpty()){
                        Glide.with(ProfileActivity.this)
                                .load(UprofilePic)
                                .apply(requestOption1)
                                .listener(new RequestListener<Drawable>() {

                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Toast.makeText(getApplicationContext(),"Failed to load profile pic",Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(profilePic);
                    }

                    if(!UcoverPic.isEmpty()){
                        Glide.with(ProfileActivity.this)
                                .load(UcoverPic)
                                .apply(requestOption2)
                                .listener(new RequestListener<Drawable>() {

                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Toast.makeText(getApplicationContext(),"Failed to load cover pic",Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(profileCoverPic);
                    }


                }else
                    Toast.makeText(getApplicationContext(),"User data does not exist",Toast.LENGTH_SHORT).show();

                hideProgressDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });

    }

    private void chooseImage(int imageReq) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imageReq);
    }

    private void uploadImage( int pos) {

        final int code=pos;
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            if(pos==70){
                 ref = storageReference.child("images/profile_pic");


            }else{
                ref = storageReference.child("images/cover_pic");

            }


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                           ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    Log.d("Pic Url Fetching","success");

                                    if(code==70){
                                        profileUserRef.child("profilePic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                UserProfileChangeRequest
                                                    request = new UserProfileChangeRequest.Builder()
                                                        .setPhotoUri(uri)
                                                        .build();
                                                profileUser.updateProfile(request);
                                                Log.d("Pic Url Uploading","success");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Pic Url Uploading","failed");
                                                Toast.makeText(getApplicationContext(),"Network error!\nFailed to upload pic.",Toast.LENGTH_SHORT).show();


                                            }
                                        });
                                    }else
                                        profileUserRef.child("coverPic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Pic Url Uploading","success");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Pic Url Uploading","failed");

                                            }
                                        });                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    Log.d("Pic Url Fetching","failure");
                                    Toast.makeText(getApplicationContext(),"Network error!\n Failed to upload pic.",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(((requestCode == PICK_IMAGE_REQUEST1) || (requestCode==PICK_IMAGE_REQUEST2)) && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                if(requestCode==PICK_IMAGE_REQUEST1){
                    Glide.with(this)
                            .load(bitmap)
                            .into(profilePic);

                    uploadImage(PICK_IMAGE_REQUEST1);


                }else {
                    Glide.with(this)
                            .load(bitmap)
                            .into(profileCoverPic);

                    uploadImage(PICK_IMAGE_REQUEST2);

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showProgressDialog() {

        if (profileDialog == null) {
            profileDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
            profileDialog.setMessage("Fetching details ....");
            profileDialog.setIndeterminate(true);
            profileDialog.setCanceledOnTouchOutside(false);
        }

        profileDialog.show();
    }

    public void hideProgressDialog() {
        if (profileDialog != null && profileDialog.isShowing()) {
            profileDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

}
