package com.example.amelia.uiucforum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Citation: https://www.youtube.com/watch?v=9ARoMRd1kXo
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private TextView btnSignup,btnForgotPass;
    private EditText input_email,input_password;

    private RelativeLayout activity_login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnSignup = (TextView) findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView) findViewById(R.id.login_btn_forgot_password);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        activity_login = (RelativeLayout) findViewById(R.id.activity_login);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Check already session , if ok-> DashBoard
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this,DashboardActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_forgot_password: {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                finish();
                break;
            }
            case R.id.login_btn_signup: {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
                break;
            }
            case R.id.login_btn_login: {
                loginUser(input_email.getText().toString(),input_password.getText().toString());
                break;
            }
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                Snackbar snackBar = Snackbar.make(activity_login,
                                        "Password length must be over 6",Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                        } else {
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                        }
                    }
                });
    }
}
