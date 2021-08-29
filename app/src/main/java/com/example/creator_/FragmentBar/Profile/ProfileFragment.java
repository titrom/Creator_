package com.example.creator_.FragmentBar.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.creator_.Adapters.RecyclerButtonProfile.AdapterRecyclerProfileButton;
import com.example.creator_.Adapters.RecyclerButtonProfile.ButtonProfileRecycler;
import com.example.creator_.Auth.LoginActivity;
import com.example.creator_.R;
import com.example.creator_.UserArchive.FragmentArchive.ArchivivesActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.creator_.R.*;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ProfileFragment extends Fragment{
    private ImageButton buttonImageUser;
//
    private final int GALLERY_REQUEST = 2;
    private RecyclerView rv;
    private TextView subColl;
    private TextView xpPointTv;
    private TextView levelTv;
    private TextView nameText;
    private LinearProgressIndicator lineXp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final static String  SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private final static String TAG="Profile";
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layout.profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        update();
        profileButton();
        buttonImageUser.setOnClickListener(v -> {
            Intent galUserImage=new Intent(Intent.ACTION_PICK);
            galUserImage.setType("image/*");
            startActivityForResult(galUserImage,GALLERY_REQUEST);
        });
    }


    private void update(){
        if (user!=null){
            information();
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                information();
            });
            swipeRefreshLayout.setColorSchemeResources(color.itemColor);
        }
    }


    private void profileButton(){
        ArrayList<ButtonProfileRecycler> bPRList = new ArrayList<>();
        bPRList.add(new ButtonProfileRecycler("Мой Архив",
                drawable.ic_action_name_archive));
//        bPRlist.add(new ButtonProfileRecycler("Статистика",
//                drawable.ic_action_name_statistic));
        bPRList.add(new ButtonProfileRecycler("Настройки",R.drawable.ic_setings));
        bPRList.add(new ButtonProfileRecycler("Выйти", drawable.ic_action_exit));

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterRecyclerProfileButton.OnClickListenerBPR onClickListenerBPR= (bPR, position) -> {
            if (position==0){
                Intent intent = new Intent(requireView().getContext(), ArchivivesActivity.class);
                startActivity(intent);
            }else if (position==1){
                Intent intent = new Intent(requireView().getContext(), SettingsActivity.class);
                startActivity(intent);
            }else if (position==2){
                new MaterialAlertDialogBuilder(requireView().getContext())
                        .setTitle(getResources().getString(string.out))
                        .setMessage(getResources().getString(string.want_some))
                        .setNegativeButton(getResources().getString(string.no), (dialog, which) ->
                                dialog.cancel()).setPositiveButton(getResources()
                        .getString(string.yes), (dialog, which) -> { FirebaseAuth.getInstance()
                        .signOut();
                        Intent intent = new Intent(requireView().getContext(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();}).show();
            }
        };
        AdapterRecyclerProfileButton adapterRPB=
                new AdapterRecyclerProfileButton(bPRList, requireView().getContext(),
                        onClickListenerBPR);
        rv.setAdapter(adapterRPB);
    }


    private void LoadImageProfile(){
        if (user!=null){
            Log.d(TAG,"good");
            StorageReference storageRef=storage.getReference();
            storageRef.child(user.getUid()+"/"+"Avatar"+".jpg").getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.with(getContext()).
                            load(uri).into(buttonImageUser)).addOnFailureListener(e -> {
            });
        }
    }


    private void init(){
        xpPointTv = requireView().findViewById(id.xp_point);
        nameText = requireView().findViewById(id.nameTextView);
        buttonImageUser=requireView().findViewById(id.buttonImageUser);
        rv = requireView().findViewById(id.rv);
        lineXp = requireView().findViewById(id.Xp_progress);
        levelTv = requireView().findViewById(id.Level);
        subColl = requireView().findViewById(id.subColl);
        swipeRefreshLayout=requireView().findViewById(R.id.restartSwipe);
    }


    private void newImageUser(final Uri uri){
        if (user!=null){
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference storageRef=storage.getReference();
            StorageReference ImageRef=storageRef.child(user.getUid()+"/"+"Avatar"+".jpg");
            UploadTask uploadTask=ImageRef.putFile(uri);
            uploadTask.addOnFailureListener(e -> {
            }).addOnSuccessListener(taskSnapshot -> LoadImageProfile());
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST){
            Uri uriImage;
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


    private void Start_uCrop(Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName +=".jpg";
        UCrop uCrop=UCrop.of(uri, Uri.fromFile
                (new File(requireContext().getCacheDir(),destinationFileName)));
        uCrop.withAspectRatio(1f,1f);
        uCrop.withOptions(getUCropOptions());
        uCrop.start(requireContext(),ProfileFragment.this);
    }


    private UCrop.Options getUCropOptions(){
        UCrop.Options options=new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.withMaxResultSize
                (requireView().getWidth()/3, requireView().getHeight()/3);
        return options;
    }


    private void progressShow(){
        if (user != null){
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        String level = "Уровень: " + snapshot.getData().get("level").toString();
                        String xpPoint = snapshot.getData().get("xpUser") + "/100 XP";
                        lineXp.setMax(100);
                        lineXp.setProgress(Integer.parseInt(snapshot.getData().get("xpUser").toString()));
                        levelTv.setText(level);
                        xpPointTv.setText(xpPoint);
                    }
                }
            });
        }
    }
    private void information(){
        if (user != null) {
            LoadImageProfile();
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
               if (task.isSuccessful()){
                   DocumentSnapshot snapshot = task.getResult();
                   if (snapshot.exists()){
                       nameText.setText(snapshot.getData().get("nickname").toString());
                       progressShow();
                       swipeRefreshLayout.setRefreshing(false);
                   }
               }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipeRefreshLayout.setRefreshing(true);
            information();
            LoadImageProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

