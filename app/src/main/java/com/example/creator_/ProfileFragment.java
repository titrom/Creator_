package com.example.creator_;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.RecyclerButtonProfile.AdapterRecyclerProfileButton;
import com.example.creator_.RecyclerButtonProfile.ButtonProfileRecycler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import static com.example.creator_.R.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProfileFragment extends Fragment {
    private ImageButton buttonImageUser;
    private final int GALLERY_REQUEST = 2;
    private TextView nameText;
    private ArrayList<ButtonProfileRecycler> bPR;
    private RecyclerView rv;
    private final String  SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";


    private final static String TAG="Profile";
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user= mAuth.getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layout.profile_fragmet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bPR=new ArrayList<ButtonProfileRecycler>();
        bPR.add(new ButtonProfileRecycler("Мастерская", R.drawable.ic_action_name_pluss));
        bPR.add(new ButtonProfileRecycler("Статистика", R.drawable.ic_action_name));
        rv=view.findViewById(id.list_button1);
        AdapterRecyclerProfileButton adapterRPB= new AdapterRecyclerProfileButton(bPR,getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapterRPB);


        buttonImageUser=view.findViewById(id.buttonImageUser);
        nameText=view.findViewById(id.nameTextView);
        if (user!=null){
            String nickname=user.getDisplayName().toString();
            nameText.setText(nickname);
        }
        LoadImageProfile();
        buttonImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galUserImage=new Intent(Intent.ACTION_PICK);
                galUserImage.setType("image/*");
                startActivityForResult(galUserImage,GALLERY_REQUEST);
            }
        });
    }
    public void LoadImageProfile(){
        if (user!=null){
            Log.d(TAG,"good");
            FirebaseStorage storage=FirebaseStorage.getInstance();
            String nickname=user.getDisplayName().toString();
            StorageReference storageRef=storage.getReference();
            storageRef.child(nickname+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext()).load(uri).into(buttonImageUser);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Error
                }
            });

        }
    }
    private void newImageUser(final Uri uri){
        if (user!=null){
            FirebaseStorage storage=FirebaseStorage.getInstance();
            String nickname=user.getDisplayName().toString();
            StorageReference storageRef=storage.getReference();
            StorageReference ImageRef=storageRef.child(nickname+".jpg");
            UploadTask uploadTask=ImageRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    LoadImageProfile();

                }
            });


        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST){
            Uri uriImage= null;
            if (data != null) {
                uriImage = data.getData();
                if (uriImage!=null){
                    Start_uCrop(uriImage);
                }
            }
        }
         else if (requestCode==UCrop.REQUEST_CROP){
            if (data!=null){
                Uri uriUCrop=UCrop.getOutput(data);
                if (uriUCrop!=null){
                    newImageUser(uriUCrop);
                }
            }
         }
    }
    public void Start_uCrop(Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName +=".jpg";
        UCrop uCrop=UCrop.of(uri,Uri.fromFile(new File(getContext().getCacheDir(),destinationFileName)));
        uCrop.withAspectRatio(5f,5f);
        uCrop.withOptions(getUCropOptions());
        uCrop.start(getContext(),ProfileFragment.this);
    }
    private UCrop.Options getUCropOptions(){
        UCrop.Options options=new UCrop.Options();
        options.setStatusBarColor(getResources(color.bar));
        options.setCircleDimmedLayer(true);
        options.withMaxResultSize(350,350);
        options.setToolbarColor(getResources(color.bar));
        return options;

    }

    private int getResources(int bar) {
        return color.bar;
    }
}
