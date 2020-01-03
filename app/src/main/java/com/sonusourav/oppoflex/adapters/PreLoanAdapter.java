package com.sonusourav.oppoflex.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.print.PdfConverter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sonusourav.oppoflex.Dao.PreLoanDao;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.DialogUtils;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.activities.NewLoanActivity;
import com.sonusourav.oppoflex.activities.PdfActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PreLoanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private Context mcontext;
  private ArrayList<PreLoanDao> preLoansList;
  private final int VIEW_TYPE_DRAFT = 0;
  private final int VIEW_TYPE_ITEM = 1;
  private DialogUtils dialogUtils;
  private PreferenceManager preferenceManager;


  class MyViewHolder extends RecyclerView.ViewHolder {
    TextView id, date,userName,email,title,bankName,type;
    ImageView profilePic,edit,download,delete;
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
      edit=view.findViewById(R.id.pre_loan_card_edit);
      download=view.findViewById(R.id.pre_loan_card_download);
      delete=view.findViewById(R.id.pre_loan_card_delete);

    }
  }

  public PreLoanAdapter(Context context, ArrayList<PreLoanDao> arrayList) {
    this.mcontext = context;
    this.preLoansList = arrayList;
    preferenceManager = new PreferenceManager(mcontext);
  }

  @NotNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

    switch (viewType) {
      case VIEW_TYPE_ITEM:
        return new MyViewHolder(
            LayoutInflater.from(mcontext)
                .inflate(R.layout.pre_loan_card, parent, false));
      case VIEW_TYPE_DRAFT:
        return new DraftHolder(
            LayoutInflater.from(mcontext).inflate(R.layout.pre_loan_card, parent, false));
      default:
        return null;
    }
  }

  @Override
  public void onBindViewHolder( @NotNull RecyclerView.ViewHolder myViewHolder, final int position) {

    if (getItemViewType(position) == VIEW_TYPE_ITEM) {

      MyViewHolder holder = (MyViewHolder) myViewHolder;

      final PreLoanDao loan = preLoansList.get(position);

      holder.id.setText(loan.getLoanId());
      holder.date.setText(loan.getDate());
      holder.userName.setText(loan.getName());
      holder.email.setText(loan.getEmail());
      holder.title.setText(loan.getTitle());
      holder.bankName.setText(loan.getBankName());
      holder.type.setText(loan.getLoanType());
      dialogUtils=new DialogUtils(mcontext,"File is being downloaded");


      holder.edit.setVisibility(View.GONE);

      Glide.with(mcontext)
          .load("" +loan.getImageUrl())
          .into(holder.profilePic);

      holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          if(position==0 && Integer.parseInt(preferenceManager.getDraftLevel())>0 ){
            Intent intent=new Intent(mcontext,NewLoanActivity.class);
            intent.putExtra("draftLevel",preferenceManager.getDraftLevel());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mcontext.startActivity(intent);
          }else{
            File file = new File(loan.getFileLocation());
            if(file.exists()){
              Intent intent=new Intent(mcontext, PdfActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("filePath",loan.getFileLocation());
              mcontext.startActivity(intent);
            }else{
              Toast.makeText(mcontext,"File Does not exist",Toast.LENGTH_SHORT).show();
            }
          }
        }
      });

      holder.download.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          String perDetails=mcontext.getResources().getString(R.string.html_template);
          dialogUtils.showProgressDialog();
          convertHtml2PDF(replaceLoan(perDetails,preLoansList.get(position)),preLoansList.get(position));
        }
      });

      holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          FirebaseDatabase database= FirebaseDatabase.getInstance();
          FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
          if(user!=null){
            DatabaseReference baseRef=database.getReference().child("PreviousLoans").child(encodeUserEmail(
                Objects.requireNonNull(user.getEmail())));
            DatabaseReference childRef=baseRef.child(loan.getLoanId());

            childRef.setValue(null);
            preLoansList.remove(position);
            notifyDataSetChanged();
          }

        }
      });
    }else{

      DraftHolder draftHolder = (DraftHolder) myViewHolder;

      final PreLoanDao loan = preLoansList.get(position);

      draftHolder.id.setText(loan.getLoanId());
      draftHolder.date.setText(loan.getDate());
      draftHolder.userName.setText(loan.getName());
      draftHolder.email.setText(loan.getEmail());
      draftHolder.title.setText(loan.getTitle());
      draftHolder.bankName.setText(loan.getBankName());
      draftHolder.type.setText(loan.getLoanType());

      draftHolder.relativeLayout.setBackground(null);
      draftHolder.relativeLayout.setBackground(mcontext.getDrawable(R.drawable.draft_gradient));

      draftHolder.download.setVisibility(View.GONE);
      draftHolder.relativeLayout.setBackground(null);
      draftHolder.id.setTextColor(mcontext.getResources().getColor(R.color.light_black));
      draftHolder.date.setTextColor(mcontext.getResources().getColor(R.color.light_black));
      draftHolder.userName.setTextColor(mcontext.getResources().getColor(R.color.black));
      draftHolder.email.setTextColor(mcontext.getResources().getColor(R.color.light_black));
      draftHolder.title.setTextColor(mcontext.getResources().getColor(R.color.light_black));
      draftHolder.bankName.setTextColor(mcontext.getResources().getColor(R.color.light_black));
      draftHolder.type.setTextColor(mcontext.getResources().getColor(R.color.light_black));

      Glide.with(mcontext)
          .load("" +loan.getImageUrl())
          .into(draftHolder.profilePic);

      draftHolder.edit.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          PreferenceManager preferenceManager= new PreferenceManager(mcontext);
          if(position==0 && Integer.parseInt(preferenceManager.getDraftLevel())>0 ){
            Intent intent=new Intent(mcontext,NewLoanActivity.class);
            intent.putExtra("draftLevel",preferenceManager.getDraftLevel());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mcontext.startActivity(intent);
          }else{
            File file = new File(loan.getFileLocation());
            if(file.exists()){
              Intent intent=new Intent(mcontext, PdfActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("filePath",loan.getFileLocation());
              mcontext.startActivity(intent);
            }else{
              Toast.makeText(mcontext,"File Does not exist",Toast.LENGTH_SHORT).show();
            }
          }
        }
      });

      draftHolder.delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          FirebaseDatabase database= FirebaseDatabase.getInstance();
          FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
          if(user!=null){
            DatabaseReference baseRef=database.getReference().child("PreviousLoans").child(encodeUserEmail(
                Objects.requireNonNull(user.getEmail())));
            DatabaseReference childRef=baseRef.child(loan.getLoanId());

            childRef.setValue(null);
            preLoansList.remove(position);
            notifyDataSetChanged();
            preferenceManager.setDraftLevel("0");
            preferenceManager.setPerDetails(null);
            preferenceManager.setFinDetails(null);
            preferenceManager.setLoanDetails(null);
          }
        }
      });

    }
  }

  public class DraftHolder extends RecyclerView.ViewHolder {

    TextView id, date,userName,email,title,bankName,type;
    ImageView profilePic,edit,download,delete;
    CardView cardView;
    RelativeLayout relativeLayout;

    DraftHolder(View view) {
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
      edit=view.findViewById(R.id.pre_loan_card_edit);
      download=view.findViewById(R.id.pre_loan_card_download);
      delete=view.findViewById(R.id.pre_loan_card_delete);
      relativeLayout=view.findViewById(R.id.pre_loan_card_rl);
    }
  }

  public Object getItem(int pos) {
    return preLoansList.get(pos);
  }

  @Override
  public int getItemCount() {
    return preLoansList.size();
  }

  @Override
  public int getItemViewType(int position)
  {
    PreferenceManager pref= new PreferenceManager(mcontext);
    if(Integer.parseInt(pref.getDraftLevel())>0 && position==0)
      return VIEW_TYPE_DRAFT;
    else
      return VIEW_TYPE_ITEM;
  }

  private static String encodeUserEmail(String userEmail) {
    return userEmail.replace(".", ",");
  }

  private void convertHtml2PDF(String src, final PreLoanDao loan){

    File root = new File(Environment.getExternalStorageDirectory(), "OPPOFLEX");
    if (!root.exists()) {
      root.mkdirs();
    }
    if (root.exists()) {

      PdfConverter converter = PdfConverter.getInstance();
      File file = new File(root, "loanNo"+loan.getLoanId()+".pdf");
      converter.convert(mcontext, src, file);
      //dialogUtils.showProgressDialog();

      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          dialogUtils.hideProgressDialog();
          Toast.makeText(mcontext, "The loan pdf is downloaded at " +loan.getFileLocation(), Toast.LENGTH_SHORT).show();
        }
      }, 4000);

    }
  }

  private String replaceLoan(String personalDet, PreLoanDao loan){

    personalDet=personalDet.replace("applicant_name","value="+loan.getPerDetailsDao().getFirstName() +" "+loan.getPerDetailsDao().getLastName());
    personalDet=personalDet.replace("email_address","value="+loan.getPerDetailsDao().getEmail());
    personalDet=personalDet.replace("date_of_birth","value="+loan.getPerDetailsDao().getDob());
    personalDet=personalDet.replace("mobile_no","value="+loan.getPerDetailsDao().getMobNo());
    personalDet=personalDet.replace("gender","value="+loan.getPerDetailsDao().getGender());
    personalDet=personalDet.replace("address","value="+loan.getPerDetailsDao().getAddress());

    personalDet=personalDet.replace("employer_name","value="+loan.getFinDetailsDao().getEmpFirstName()+" "+loan.getFinDetailsDao().getEmpLastName());
    personalDet=personalDet.replace("department_name","value="+loan.getFinDetailsDao().getDept());
    personalDet=personalDet.replace("department_name","value="+loan.getFinDetailsDao().getDept());
    personalDet=personalDet.replace("date_of_retirement","value="+loan.getFinDetailsDao().getDor());
    personalDet=personalDet.replace("designation","value="+loan.getFinDetailsDao().getDesignation());
    personalDet=personalDet.replace("experience","value="+loan.getFinDetailsDao().getExp());
    personalDet=personalDet.replace("net_salary","value="+loan.getFinDetailsDao().getSalary());

    personalDet=personalDet.replace("property_cost","value="+loan.getLoanDetailsDao().getPropertyCost());
    personalDet=personalDet.replace("loan_amount","value="+loan.getLoanDetailsDao().getLoanAmount());
    personalDet=personalDet.replace("loan_purpose","value="+loan.getLoanDetailsDao().getLoanPurpose());
    personalDet=personalDet.replace("repayment","value="+loan.getLoanDetailsDao().getRepayment());
    personalDet=personalDet.replace("tenure","value="+loan.getLoanDetailsDao().getTenure());

    return personalDet;
  }
}

