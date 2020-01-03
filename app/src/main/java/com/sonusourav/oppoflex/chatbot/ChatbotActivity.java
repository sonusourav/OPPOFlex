package com.sonusourav.oppoflex.chatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.activities.MainActivity;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KMLoginHandler;

public class ChatbotActivity extends AppCompatActivity {

    LinearLayout layout;
    boolean exit = false;
    public final String APP_ID = "71c9bd769a05623aae8bbfc2c16cca92";
    private static final String INVALID_APP_ID = "INVALID_APPLICATION_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Kommunicate.init(this, APP_ID);
        final ProgressDialog progressDialog = new ProgressDialog(ChatbotActivity.this);
        progressDialog.setTitle("Launching OPPOBOT..");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Kommunicate.loginAsVisitor(ChatbotActivity.this, new KMLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                progressDialog.dismiss();
                Kommunicate.openConversation(context, null);
            }

            @Override
            public void onFailure(
                RegistrationResponse registrationResponse, Exception exception) {
                progressDialog.dismiss();
                createLoginErrorDialog(registrationResponse, exception);
            }
        });
    }

    public String getInvalidAppIdError(RegistrationResponse registrationResponse) {
        if (registrationResponse != null) {
            if (registrationResponse.getMessage() != null && INVALID_APP_ID.equals(registrationResponse.getMessage())) {
                return getString(R.string.inavild_app_id_error);
            } else {
                return registrationResponse.getMessage();
            }
        }
        return "";
    }

    public void createLoginErrorDialog(
        RegistrationResponse registrationResponse, Exception exception) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true);
        StringBuilder message = new StringBuilder(getString(R.string.some_error_occured));
        if (registrationResponse != null) {
            if (!TextUtils.isEmpty(getInvalidAppIdError(registrationResponse))) {
                message.append(" : ");
                message.append(getInvalidAppIdError(registrationResponse));
            }
        } else if (exception != null) {
            message.append(" : ");
            message.append(exception.getMessage());
        }

        dialogBuilder.setMessage(message.toString());
        dialogBuilder.show();
    }


    @Override
    public void onBackPressed() {

        if (exit) {
            startActivity(new Intent(ChatbotActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.press_back_exit, Toast.LENGTH_SHORT).show();
            exit = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);
        }
    }
}
