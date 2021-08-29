package com.example.creator_.FragmentBar.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.creator_.Adapters.RecyclerButtonProfile.AdapterRecyclerProfileButton;
import com.example.creator_.Adapters.RecyclerButtonProfile.ButtonProfileRecycler;
import com.example.creator_.Auth.LoginActivity;
import com.example.creator_.Auth.MainActivity;
import com.example.creator_.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private final static String TAG = "SettingsActivity";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private TextInputLayout newNickname;
    private MaterialButton saveNickname;


    private TextInputLayout newEmail;
    private MaterialButton saveEmail;


    private MaterialButton savePassword;


    private MaterialButton verifiedEmail;


    private ImageView backBt;


    private MaterialButton deleteBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        isEmpty();
        loadInf();

        saveNickname.setOnClickListener(v -> saveSettings(0));
        saveEmail.setOnClickListener(v -> saveSettings(1));
        savePassword.setOnClickListener(v -> saveSettings(2));
        verifiedEmail.setOnClickListener(v -> saveSettings(3));

        backBt.setOnClickListener(v -> finish());

        deleteBt.setOnClickListener(v -> deleteUser());
    }


    private void init(){
        newNickname = findViewById(R.id.new_nickname_layout);
        saveNickname = findViewById(R.id.save_nickname);

        newEmail = findViewById(R.id.new_email_layout);
        saveEmail = findViewById(R.id.save_email);

        savePassword = findViewById(R.id.save_password);

        verifiedEmail = findViewById(R.id.verified_email);

        backBt = findViewById(R.id.back_button);

        deleteBt = findViewById(R.id.delete_bt);
    }


    private void saveSettings(int save_id){
        switch (save_id){
            case 0: {
                if (!Objects.requireNonNull(newNickname.getEditText()).getText().toString().trim().isEmpty()){
                    db.collection("UserProfile").document(user.getUid())
                            .update("nickname", newNickname.getEditText().getText().toString())
                            .addOnSuccessListener(command -> {
                                Log.d(TAG, "Good");
                                Toast.makeText(SettingsActivity.this, "Новый никнейм сохранён", Toast.LENGTH_LONG).show();
                            }).addOnFailureListener(e -> Log.e(TAG, e.getMessage()));

                    UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Objects.requireNonNull(newNickname.getEditText()).getText().toString()).build();
                    user.updateProfile(profileChange).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) Log.d(TAG, "User profile updated.");
                    });
                }
            }break;
            case 1:{
                if (!Objects.requireNonNull(newEmail.getEditText()).getText().toString().trim().isEmpty()){
                    user.updateEmail(newEmail.getEditText().getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Log.d(TAG, "User email address updated.");
                            Toast.makeText(SettingsActivity.this, "Установлен новый адрес электронной почты. Для отмены действия отправленно письмо на почту.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });
                }else {
                    Toast.makeText(SettingsActivity.this, "Не правельный адрес электронной почты", Toast.LENGTH_LONG).show();
                }
            }break;
            case 2:{
                if (user.isEmailVerified()){
                    auth.sendPasswordResetEmail(Objects.requireNonNull(user.getEmail()))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    Toast.makeText(SettingsActivity.this, "Для сброса пароля прочитайте письмо, отправленое на почту"+ user.getEmail()
                                            + ".", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });
                }else{
                    Toast.makeText(SettingsActivity.this, "Адрес электронной почты не потверждён.", Toast.LENGTH_LONG).show();
                }
            }break;
            case 3:{
                if (user != null){
                    user.sendEmailVerification().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "На адрес электронной почты" + user.getEmail()+" выслано письмо с потверждением.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });
                }
            }break;
            default:break;
        }
    }


    private void loadInf(){
        if (user != null){
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        String nickname = Objects.requireNonNull(Objects.requireNonNull(snapshot.getData()).get("nickname")).toString();
                        Objects.requireNonNull(newNickname.getEditText()).setText(nickname);
                    }
                }
            });
        }
    }

    private void deleteUser(){
        new MaterialAlertDialogBuilder(this).setTitle("Удаление аккаунта").setMessage("Вы действительно хотете удалить аккаунт?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (Objects.requireNonNull(snapshot).exists()) {
                                ArrayList<String> listIdBook = (ArrayList<String>) Objects.requireNonNull(snapshot.getData()).get("listIdBook");
                                for (String bookId: Objects.requireNonNull(listIdBook)) {
                                    db.collection("Book").document(bookId).delete()
                                            .addOnSuccessListener(command -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                            .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                                    db.collection("UserProfile").document(user.getUid()).delete().addOnSuccessListener(command -> {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                                        user.delete().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "User account deleted.");
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                        });
                                    }).addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                                }
                            }
                        }
                    });

                }).setNegativeButton(R.string.no, (dialog, which) -> {
                    dialog.cancel();
        }).show();
    }


    private void isEmpty(){
        Objects.requireNonNull(newNickname.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                newNickname.setError(null);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (newNickname.getEditText().getText().toString().trim().isEmpty()){
                    newNickname.setError("Это поле надо заполнить");
                }
            }
        });
        Objects.requireNonNull(newEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                newEmail.setError(null);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (newEmail.getEditText().getText().toString().trim().isEmpty()){
                    newEmail.setError("Это поле надо заполнить");
                }
            }
        });
    }
}