package com.sonusourav.oppoflex.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sonusourav.oppoflex.Dao.PreLoanDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.activities.NewLoanActivity;
import com.sonusourav.oppoflex.activities.PdfActivity;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class PreLoanAdapter extends RecyclerView.Adapter<PreLoanAdapter.MyViewHolder> {
  private Context mcontext;
  private ArrayList<PreLoanDao> preLoansList;

  class MyViewHolder extends RecyclerView.ViewHolder {
    TextView id, date,userName,email,title,bankName,type;
    ImageView profilePic;
    CardView cardView;

    MyViewHolder(View view) {
      super(view);
      id = view.findViewById(R.id.loan_id);
      date = view.findViewById(R.id.loan_date);
      title = view.findViewById(R.id.loan_title);
      userName = view.findViewById(R.id.loan_username);
      email=view.findViewById(R.id.loan_email);
      bankName=view.findViewById(R.id.loan_bank);
      type=view.findViewById(R.id.loan_type);
      profilePic=view.findViewById(R.id.loan_pic);
      cardView=view.findViewById(R.id.pre_loan_card);
    }
  }


  public PreLoanAdapter(Context context, ArrayList<PreLoanDao> arrayList) {
    this.mcontext = context;
    this.preLoansList = arrayList;
  }

  @NotNull @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.pre_loan_card, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder, final int position) {
    final PreLoanDao loan = preLoansList.get(position);

    holder.id.setText(loan.getLoanId());
    holder.date.setText(loan.getDate());
    holder.userName.setText(loan.getName());
    holder.email.setText(loan.getEmail());
    holder.title.setText(loan.getTitle());
    holder.bankName.setText(loan.getBankName());
    holder.type.setText(loan.getLoanType());

    Glide.with(mcontext)
        .load("" +loan.getImageUrl())
        .into(holder.profilePic);

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        PreferenceManager preferenceManager= new PreferenceManager(mcontext);
        if(position==0 && Integer.parseInt(preferenceManager.getDraftLevel())>0 ){
          Intent intent=new Intent(mcontext,NewLoanActivity.class);
          intent.putExtra("draftLevel",preferenceManager.getDraftLevel());
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          mcontext.startActivity(intent);
        }else{
          Intent intent=new Intent(mcontext, PdfActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          intent.putExtra("filePath",loan.getFileLocation());
          mcontext.startActivity(intent);
        }
      }
    });


  }

  public Object getItem(int pos) {
    return preLoansList.get(pos);
  }

  @Override
  public int getItemCount() {
    return preLoansList.size();
  }

}

