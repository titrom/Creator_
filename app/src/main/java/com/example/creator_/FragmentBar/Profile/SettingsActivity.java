package com.example.creator_.FragmentBar.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.creator_.Adapters.RecyclerButtonProfile.AdapterRecyclerProfileButton;
import com.example.creator_.Adapters.RecyclerButtonProfile.ButtonProfileRecycler;
import com.example.creator_.Auth.LoginActivity;
import com.example.creator_.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private final static String TAG = "SettingsActivity";
    private RecyclerView rv;
    private EditText inputEditTextUpdate;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rv = findViewById(R.id.settingsButtonRecycler);
        settingsButton();
    }
    private void settingsButton(){
        ArrayList<ButtonProfileRecycler> bPRList = new ArrayList<>();
        bPRList.add(new ButtonProfileRecycler("Сменить псевдоним",R.drawable.nickname));
        bPRList.add(new ButtonProfileRecycler("Сменить почту", R.drawable.email));
        bPRList.add(new ButtonProfileRecycler("Сменить пароль", R.drawable.update_password));
        bPRList.add(new ButtonProfileRecycler("Удалить аккаунт", R.drawable.ic_action_clear));

        rv.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));
        AdapterRecyclerProfileButton.OnClickListenerBPR onClickListenerBPR= (bPR, position) -> {
            switch (bPR.getNameButton()){
                case "Сменить псевдоним": {
                    inputEditTextUpdate = new EditText(SettingsActivity.this);
                    inputEditTextUpdate.setPadding(15,0,15,0);
                    inputEditTextUpdate.setTextSize(20);
                    inputEditTextUpdate.setMaxLines(50);
                    inputEditTextUpdate.setHint("Введите новый псевдоним");
                    inputEditTextUpdate.setBackgroundResource(R.color.white);
                    new MaterialAlertDialogBuilder(SettingsActivity.this).setTitle("Смена псевдонима")
                            .setMessage(" Вы действительно хотите сменить псевдоним ?")
                            .setView(inputEditTextUpdate)
                            .setPositiveButton(R.string.yes , (dialog, which) -> {
                                if (!inputEditTextUpdate.getText().toString().trim().isEmpty()&& user!=null){
                                    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder()
                                            .setDisplayName(inputEditTextUpdate.getText().toString()).build();
                                    Objects.requireNonNull(firebaseUser).updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SettingsActivity.this,"Псевдоним обнавлён",Toast.LENGTH_LONG).show();
                                                }});
                                    db.collection("UserProfile").document(user.getUid()).update("nickname",inputEditTextUpdate.getText().toString())
                                            .addOnSuccessListener(aVoid -> Log.d(TAG,"Good Update"))
                                            .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                }}).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                }break;
                case "Сменить почту": {
                    inputEditTextUpdate = new EditText(SettingsActivity.this);
                    inputEditTextUpdate.setPadding(15,0,15,0);
                    inputEditTextUpdate.setTextSize(20);
                    inputEditTextUpdate.setHint("Введите новую почту");
                    inputEditTextUpdate.setBackgroundResource(R.color.white);
                    new MaterialAlertDialogBuilder(SettingsActivity.this).setTitle("Смена почты")
                            .setMessage(" Вы действительно хотите сменить почту ?")
                            .setView(inputEditTextUpdate)
                            .setPositiveButton(R.string.yes , (dialog, which) -> {
                                if (!inputEditTextUpdate.getText().toString().trim().isEmpty()&& user!=null){
                                    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                                    Objects.requireNonNull(firebaseUser).updateEmail(inputEditTextUpdate.getText().toString())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SettingsActivity.this,"Почта обнавлёна",Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }}).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                }break;
                case "Сменить пароль":{
                    inputEditTextUpdate = new EditText(SettingsActivity.this);
                    inputEditTextUpdate.setPadding(15,0,15,0);
                    inputEditTextUpdate.setTextSize(20);
                    inputEditTextUpdate.setHint("Введите новый пароль");
                    inputEditTextUpdate.setBackgroundResource(R.color.white);
                    new MaterialAlertDialogBuilder(SettingsActivity.this).setTitle("Смена пороля")
                            .setMessage(" Вы действительно хотите сменить пароль ?")
                            .setView(inputEditTextUpdate)
                            .setPositiveButton(R.string.yes , (dialog, which) -> {
                                if (!inputEditTextUpdate.getText().toString().trim().isEmpty()&& user!=null){
                                    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                                    Objects.requireNonNull(firebaseUser).updatePassword(inputEditTextUpdate.getText().toString())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SettingsActivity.this,"Пороль обнавлён",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }}).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                }break;
                case "Удалить аккаунт":{
                    new MaterialAlertDialogBuilder(SettingsActivity.this).setTitle("Удаление аккаунта")
                            .setMessage(" Вы действительно хотите удалить аккаунт ?")
                            .setPositiveButton(R.string.yes , (dialog, which) -> {
                                if (user!=null){
                                    db.collection("UserProfile").document(user.getUid()).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG,"Good delete!!!");
                                                FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                                                Objects.requireNonNull(firebaseUser).delete().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()){
                                                        FirebaseAuth.getInstance().signOut();
                                                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                }}).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                }
            }
        };
        AdapterRecyclerProfileButton adapterRPB=
                new AdapterRecyclerProfileButton(bPRList, SettingsActivity.this,
                        onClickListenerBPR);
        rv.setAdapter(adapterRPB);
    }
}