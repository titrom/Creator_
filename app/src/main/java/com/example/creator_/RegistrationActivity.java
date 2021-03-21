package com.example.creator_;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import UserFirestore.UserClass;

public class RegistrationActivity extends AppCompatActivity {
    private static String TAG="Registration";

    private EditText nickName;
    private EditText email;
    private EditText passwordOne;
    private EditText passwordTwo;
    private Button registrationClose;
    public static String nicknameString;
    private boolean checkErrorStart,checkPasswordError;

    public static String getNicknameString() {
        return nicknameString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nickName=findViewById(R.id.Nickname);
        email=findViewById(R.id.emailRegistration);
        passwordOne=findViewById(R.id.passwordRegistration);
        passwordTwo=findViewById(R.id.passwordRepeat);
        registrationClose=findViewById(R.id.registrationButtonClose);
        checkErrorStart=false;
        ErrorControl();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        registrationClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextInputLayout passwordLayoutOne=findViewById(R.id.Pass);
                final TextInputLayout passwordLayoutTwo=findViewById(R.id.RepeatPassword);
                nicknameString=nickName.getText().toString();
                String emailString=email.getText().toString();
                String passwordOneString=passwordOne.getText().toString();
                String passwordTwoString=passwordTwo.getText().toString();
                if (passwordOneString.equals(passwordTwoString)){
                    passwordLayoutOne.setError(null);
                    passwordLayoutTwo.setError(null);
                    ErrorStartCheck();
                    checkPasswordError=true;
                }else {
                    checkPasswordError=false;
                    passwordLayoutOne.setError("Пароли не совпадают");
                    passwordLayoutTwo.setError("Пароли не совпадают");
                }
                if (checkPasswordError&&checkErrorStart){
                    mAuth.createUserWithEmailAndPassword(emailString,passwordOneString).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user=mAuth.getCurrentUser();
                                if (user!=null){
                                    UserProfileChangeRequest userProfile=new UserProfileChangeRequest.Builder().setDisplayName(nicknameString).build();
                                    user.updateProfile(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                                }else Log.d(TAG,"ErrorSing");
                                UserClass newUser=new UserClass(nicknameString, 0);
                                db.collection("UserProfile").document(nicknameString).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                }) .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }else Toast.makeText(RegistrationActivity.this, "Не правильный адрес электронной почты", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(RegistrationActivity.this,"Ошибка Регистрации",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void ErrorStartCheck(){
        final TextInputLayout passwordLayoutOne=findViewById(R.id.Pass);
        final TextInputLayout passwordLayoutTwo=findViewById(R.id.RepeatPassword);
        final TextInputLayout emailLayout=findViewById(R.id.emailPerfect);
        final TextInputLayout NicknameLayout=findViewById(R.id.NicknameLayout);
        if (NicknameLayout.getEditText().getText().toString().trim().isEmpty()) NicknameLayout.setError("Это поле надо заполнить");
        else NicknameLayout.setError(null);
        if (emailLayout.getEditText().getText().toString().trim().isEmpty()) emailLayout.setError("Это поле надо заполнить");
        else emailLayout.setError(null);
        if (passwordLayoutOne.getEditText().getText().toString().trim().isEmpty()) passwordLayoutOne.setError("Это поле надо заполнить");
        else passwordLayoutOne.setError(null);
        if (passwordLayoutTwo.getEditText().getText().toString().trim().isEmpty()) passwordLayoutTwo.setError("Это поле надо заполнить");
        else passwordLayoutTwo.setError(null);
        checkErrorStart = (!NicknameLayout.getEditText().getText().toString().trim().isEmpty()
                && !emailLayout.getEditText().getText().toString().trim().isEmpty()
                && !passwordLayoutOne.getEditText().getText().toString().trim().isEmpty()
                && !passwordLayoutTwo.getEditText().getText().toString().trim().isEmpty());
    }
    public void ErrorControl(){
        final TextInputLayout passwordLayoutOne=findViewById(R.id.Pass);
        final TextInputLayout passwordLayoutTwo=findViewById(R.id.RepeatPassword);
        final TextInputLayout emailLayout=findViewById(R.id.emailPerfect);
        final TextInputLayout NicknameLayout=findViewById(R.id.NicknameLayout);
        NicknameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                NicknameLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (NicknameLayout.getEditText().getText().toString().trim().isEmpty()){
                    NicknameLayout.setError("Это поле надо заполнить");

                }

            }
        });
        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                emailLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (emailLayout.getEditText().getText().toString().trim().isEmpty()){
                    emailLayout.setError("Это поле надо заполнить");

                }
            }
        });
        passwordLayoutTwo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordLayoutTwo.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordLayoutTwo.getEditText().getText().toString().trim().isEmpty()){
                    passwordLayoutTwo.setError("Это поле надо заполнить");

                }

            }
        });
        passwordLayoutOne.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordLayoutOne.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordLayoutOne.getEditText().getText().toString().trim().isEmpty()){
                    passwordLayoutOne.setError("Это поле надо заполнить");

                }
            }
        });

    }
}