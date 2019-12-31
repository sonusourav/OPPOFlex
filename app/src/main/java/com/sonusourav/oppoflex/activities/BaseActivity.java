package com.sonusourav.oppoflex.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.account.LoginActivity;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

  protected FrameLayout frameLayout;
  private PreferenceManager basePref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base);

    frameLayout=findViewById(R.id.content_frame);
    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_drawer);
    navigationView.setNavigationItemSelectedListener(this);
    basePref=new PreferenceManager(this);

    Toolbar toolbar = findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    getSupportActionBar().setHomeButtonEnabled(true);

    setUser();
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

//    Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
   // getActionBar().setHomeButtonEnabled(true);

  }

  private static long back_pressed;
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      if (back_pressed + 2000 > System.currentTimeMillis()){
        moveTaskToBack(true);            }
      else{
        Toast.makeText(getBaseContext(), "Press twice to exit", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
      }
    }
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
      int id = menuItem.getItemId();

      switch (id) {

        case R.id.nav_home: {
          startActivity(new Intent().setClass(this, MainActivity.class));
          break;
        }

        case R.id.nav_pre_loans: {
          startActivity(new Intent().setClass(this, PreLoanActivity.class));
          break;
        }

        case R.id.nav_new_loans: {
          startActivity(new Intent().setClass(this, NewLoanActivity.class));
          break;
        }

        case R.id.nav_profile: {
          startActivity(new Intent().setClass(this, ProfileActivity.class));
          break;
        }

        case R.id.nav_pass: {
          startActivity(new Intent().setClass(this, UpdatePasswordActivity.class));
          break;
        }



        case R.id.nav_logout: {
          PreferenceManager preferenceManager =
              new PreferenceManager(getApplicationContext());
          preferenceManager.setIsLoggedIn(false);
          preferenceManager.setLoginCredentials("email", "password","authType");
          preferenceManager.setPrefEmail("email");
          preferenceManager.setPrefName("username");

          FirebaseAuth loginAuth = FirebaseAuth.getInstance();
          loginAuth.signOut();

          GoogleSignInOptions gso =
              new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                  .requestIdToken(getResources().getString(R.string.default_web_client_id))
                  .requestEmail()
                  .build();
          GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

          googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              Toast.makeText(getApplicationContext(), "User successfully logged out.",
                  Toast.LENGTH_SHORT).show();
              startActivity(new Intent().setClass(getApplicationContext(), LoginActivity.class));
            }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(getApplicationContext(), "Cannot Log Out, Please try again.",
                  Toast.LENGTH_SHORT).show();
            }
          });
          break;
        }

      }

      DrawerLayout drawer = findViewById(R.id.drawer_layout);
      drawer.closeDrawer(GravityCompat.START);
      return false;
    }

  public void setUser(){
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    if (firebaseUser!=null){
      NavigationView navigationView = findViewById(R.id.nav_drawer);
      LinearLayout drawerHeader = (LinearLayout) navigationView.getHeaderView(0);

      TextView emailTextView = drawerHeader.findViewById(R.id.nav_header_email);
      TextView usernameTextView = drawerHeader.findViewById(R.id.nav_header_username);
      ImageView userImage=drawerHeader.findViewById(R.id.nav_header_imageView);

      emailTextView.setText(firebaseUser.getEmail());


      String displayName=firebaseUser.getDisplayName();

      for (UserInfo userInfo : firebaseUser.getProviderData()) {
        if (displayName == null && userInfo.getDisplayName() != null) {
          displayName = userInfo.getDisplayName();
          Log.d("BaseActivity", userInfo.getDisplayName());

        }
      }

      usernameTextView.setText(basePref.getUserName());

      Uri photoUri = firebaseUser.getPhotoUrl();
      if(photoUri == null){
        String userName = firebaseUser.getDisplayName();
        char ch;
        if(userName != null) {
          ch = userName.charAt(0);
          TextDrawable drawable = TextDrawable.builder()
              .buildRound(String.valueOf(ch), Color.BLUE);
          Bitmap bitmap = drawableToBitmap(drawable);
          Glide.with(getApplicationContext())
              .load(bitmap)
              .into(userImage);
        }
      }
      else{
        Glide.with(getApplicationContext())
            .load(photoUri)
            .into(userImage);
      }
    }
  }

  public static Bitmap drawableToBitmap (Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable)drawable).getBitmap();
    }

    int width = drawable.getIntrinsicWidth();
    width = width > 0 ? width : 96;
    int height = drawable.getIntrinsicHeight();
    height = height > 0 ? height : 96;

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }

}
