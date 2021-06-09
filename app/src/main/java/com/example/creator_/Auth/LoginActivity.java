package com.example.creator_.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.creator_.FragmentBar.GlobalActivity;
import com.example.creator_.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private final String TAG ="LoginUser";
    private EditText email;
    private EditText password;
    private Button Sing;
    private Button intentButtonRegistration;
    private TextView updatePassword;
    private boolean checkStartSing;
    private TextInputEditText inputEditTextEmailUpdatePassword;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditLogText);
        Sing=findViewById(R.id.singButton);
        updatePassword = findViewById(R.id.updatePassword);
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

        updatePassword.setOnClickListener(v -> {
            inputEditTextEmailUpdatePassword = new TextInputEditText(LoginActivity.this);
            LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            inputEditTextEmailUpdatePassword.setLayoutParams(params);
            inputEditTextEmailUpdatePassword.setTextSize(20);
            inputEditTextEmailUpdatePassword.setTextColor(getResources().getColor(R.color.text_color, getResources().newTheme()));
            inputEditTextEmailUpdatePassword.setHint("Введите вашу почту");
            inputEditTextEmailUpdatePassword.setBackgroundResource(R.color.background);
            new MaterialAlertDialogBuilder(LoginActivity.this).setTitle("Востановить пароль")
                    .setMessage(" ")
                    .setView(inputEditTextEmailUpdatePassword)
                    .setPositiveButton(R.string.complete , (dialog, which) -> {
                        if (!inputEditTextEmailUpdatePassword.getText().toString().trim().isEmpty()){
                            mAuth.sendPasswordResetEmail(inputEditTextEmailUpdatePassword.getText().toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this,"Для востоновления пароля проверти вашу почту.",Toast.LENGTH_LONG).show();
                                            dialog.cancel();
                                        }
                                    });
                        }
                    } ).setNegativeButton(R.string.close, (dialog, which) -> dialog.cancel()).show();
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