package com.example.creator_;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.annotation.Annotation;

public class LoginActivity extends AppCompatActivity {
    private final String TAG ="LoginUser";
    private EditText email;
    private EditText password;
    private Button Sing;
    private Button intentButtonRegistration;
    private boolean checkStartSing;
    private int verInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditLogText);
        Sing=findViewById(R.id.singButton);
        intentButtonRegistration=findViewById(R.id.registrationButton);
        Animation animation=  AnimationUtils.loadAnimation(this,R.anim.fade_in);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();

        checkStartSing=false;
        ErrorEditText();
        Sing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString=email.getText().toString();
                String passwordString=password.getText().toString();
                ErrorStartSing();

                if (checkStartSing){

                    mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                    Log.d(TAG,"GoodSing");
                                    Intent intent=new Intent(LoginActivity.this,GlobalActivity.class);
                                    startActivity(intent);
                            }else { Toast.makeText(LoginActivity.this,"Неправильная почта или пароль",Toast.LENGTH_LONG).show();}


                        }
                    });

                }
            }
        });
        intentButtonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);

            }
        });
    }

    public void  ErrorStartSing(){
        final TextInputLayout emailLogLayout=findViewById(R.id.emailPerfectLog);
        final TextInputLayout passwordLayoutLog=findViewById(R.id.password_layoutLog);
        if (emailLogLayout.getEditText().getText().toString().trim().isEmpty()) emailLogLayout.setError("Это поле надо заполнить");
        else emailLogLayout.setError(null);
        if (passwordLayoutLog.getEditText().getText().toString().trim().isEmpty()) passwordLayoutLog.setError("Это поле надо заполнить");
        else passwordLayoutLog.setError(null);
        checkStartSing = (!emailLogLayout.getEditText().getText().toString().trim().isEmpty()
                && !passwordLayoutLog.getEditText().getText().toString().trim().isEmpty());
    }
    public void ErrorEditText(){
        final TextInputLayout emailLogLayout=findViewById(R.id.emailPerfectLog);
        final TextInputLayout passwordLayoutLog=findViewById(R.id.password_layoutLog);
        emailLogLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                emailLogLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (emailLogLayout.getEditText().getText().toString().trim().isEmpty()){
                    emailLogLayout.setError("Это поле надо заполнить");
                }

            }
        });
        passwordLayoutLog.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordLayoutLog.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordLayoutLog.getEditText().getText().toString().trim().isEmpty()){
                    passwordLayoutLog.setError("Это поле надо заполнить");
                }

            }
        });

    }
}