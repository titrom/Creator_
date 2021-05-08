package com.example.creator_.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.creator_.GlobalActivity;
import com.example.creator_.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import UserFirestore.UserClass;

public class LoginActivity extends AppCompatActivity {
    private final String TAG ="LoginUser";
    private EditText email;
    private EditText password;
    private Button Sing;
    private Button intentButtonRegistration;
    private boolean checkStartSing;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user=mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditLogText);
        Sing=findViewById(R.id.singButton);
        intentButtonRegistration=findViewById(R.id.registrationButton);
        checkStartSing=false;
        ErrorEditText();
        Sing.setOnClickListener(v -> {
            String emailString=email.getText().toString();
            String passwordString=password.getText().toString();
            ErrorStartSing();
            if (checkStartSing){
                mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG,"GoodSing");
                        Intent intent=new Intent(LoginActivity.this, GlobalActivity.class);
                        startActivity(intent);
                        finish();


                    }else { Toast.makeText(LoginActivity.this,"Неправильная почта или пароль",Toast.LENGTH_LONG).show();}
                });
            }
        });
        intentButtonRegistration.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void  ErrorStartSing(){
        final TextInputLayout emailLogLayout=findViewById(R.id.emailPerfectLog);
        final TextInputLayout passwordLayoutLog=findViewById(R.id.password_layoutLog);
        if (emailLogLayout.getEditText().getText().toString().trim().isEmpty()) emailLogLayout.setError("Это поле надо заполнить");
        else emailLogLayout.setError(null);
        if (passwordLayoutLog.getEditText().getText().toString().trim().isEmpty()) passwordLayoutLog.setError("Это поле надо заполнить");
        else passwordLayoutLog.setError(null);
        checkStartSing = (!emailLogLayout.getEditText().getText().toString().trim().isEmpty()
                && !passwordLayoutLog.getEditText().getText().toString().trim().isEmpty());
    }
    private void ErrorEditText(){
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