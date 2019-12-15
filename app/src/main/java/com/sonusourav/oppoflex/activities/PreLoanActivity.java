package com.sonusourav.oppoflex.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sonusourav.oppoflex.Dao.PreLoanDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.adapters.PreLoanAdapter;
import java.util.ArrayList;
import java.util.Objects;

public class PreLoanActivity extends BaseActivity {

  private ShimmerFrameLayout mShimmerViewContainer;
  private ArrayList<PreLoanDao> preLoanList;
  private String TAG=PreLoanActivity.class.getSimpleName();
  DatabaseReference childRef;
  private PreLoanAdapter preLoanAdapter;
  private DrawerLayout drawerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();

    LayoutInflater inflater = (LayoutInflater) this
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View contentView = inflater.inflate(R.layout.activity_pre_loan, null, false);
    drawerLayout.addView(contentView, 0);

    mShimmerViewContainer = contentView.findViewById(R.id.shimmer_view_container);
    mShimmerViewContainer.startShimmer();

    RecyclerView recyclerView = contentView.findViewById(R.id.pre_loan_recycler_view);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(preLoanAdapter);

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase feedbackInstance = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = feedbackInstance.getReference("PreviousLoans");
    final FirebaseUser user = firebaseAuth.getCurrentUser();

    if (user != null) {

    childRef=rootRef.child(encodeUserEmail(Objects.requireNonNull(user.getEmail())));

    Log.d(TAG,encodeUserEmail(user.getEmail()));
    childRef.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d(TAG," reached");

        if (dataSnapshot.exists()) {
          preLoanList.clear();
          int i=0;
          Log.d(TAG,Integer.toString(++i));
          Log.d(TAG, "dataSnapshot exists");

          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Log.d(TAG, "onDataChange: reached");
            PreLoanDao loan = snapshot.getValue(PreLoanDao.class);

            if (loan != null) {
              preLoanList.add(loan);
              Log.d(TAG,loan.getTitle());
            }
          }

          PreferenceManager preLoanPref= new PreferenceManager(PreLoanActivity.this);
          if(Integer.parseInt(preLoanPref.getDraftLevel())>0){
            PreLoanDao draftLoan=preLoanPref.getDraftLoan();
            preLoanList.add(0,draftLoan);
          }

          preLoanAdapter.notifyDataSetChanged();
          mShimmerViewContainer.setVisibility(View.GONE);
        } else {
          Log.d(TAG,"else");
          mShimmerViewContainer.setVisibility(View.GONE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

        Log.e(TAG, "Failed to read value.", databaseError.toException());
        mShimmerViewContainer.setVisibility(View.GONE);
      }
    });
  }
  }

  @Override
  public void onBackPressed() {
    NavUtils.navigateUpFromSameTask(this);
  }

  private static String encodeUserEmail(String userEmail) {
    return userEmail.replace(".", ",");
  }

  private void init(){
    preLoanList = new ArrayList<>();
    preLoanAdapter = new PreLoanAdapter(this, preLoanList);
    drawerLayout=findViewById(R.id.drawer_layout);

  }
}
