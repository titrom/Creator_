package com.example.creator_.FragmentBar;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.creator_.UserArchive.ArchivivesActivity;
import com.example.creator_.Auth.LoginActivity;
import com.example.creator_.R;
import com.example.creator_.RecyclerButtonProfile.AdapterRecyclerProfileButton;
import com.example.creator_.RecyclerButtonProfile.ButtonProfileRecycler;
import com.example.creator_.StatisticsActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
    private final int GALLERY_REQUEST = 2;
    private RecyclerView rv;
    private TextView xpPoint;
    private TextView levelTv;
    private TextView nameText;
    private LinearProgressIndicator LineXp;
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
            String nickname = user.getDisplayName();
            nameText.setText(nickname);
            XpUpdate();
            LoadImageProfile();
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                XpUpdate();
                LoadImageProfile();
            });
            swipeRefreshLayout.setColorSchemeResources(color.ItemColor);
        }
    }


    private void profileButton(){
        ArrayList<ButtonProfileRecycler> bPRlist = new ArrayList<>();
        bPRlist.add(new ButtonProfileRecycler("Мой Архив",
                drawable.ic_action_name_archive));
        bPRlist.add(new ButtonProfileRecycler("Статистика",
                drawable.ic_action_name_statistic));
        bPRlist.add(new ButtonProfileRecycler("Настройки",R.drawable.ic_setings));
        bPRlist.add(new ButtonProfileRecycler("Выйти", drawable.ic_action_exit));

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterRecyclerProfileButton.OnClickListenerBPR onClickListenerBPR= (bPR, position) -> {
            Intent intentArchivesAndStatistics;
            if (position==0){
                intentArchivesAndStatistics=
                        new Intent(requireView().getContext(), ArchivivesActivity.class);
                startActivity(intentArchivesAndStatistics);
            }else if (position==1){
                intentArchivesAndStatistics=
                        new Intent(requireView().getContext(), StatisticsActivity.class);
                startActivity(intentArchivesAndStatistics);
            }else if (position==3){
                new MaterialAlertDialogBuilder(requireView().getContext())
                        .setTitle(getResources().getString(string.out))
                        .setMessage(getResources().getString(string.WantSome))
                        .setNegativeButton(getResources().getString(string.No), (dialog, which) ->
                                dialog.cancel()).setPositiveButton(getResources()
                        .getString(string.Yes), (dialog, which) -> { FirebaseAuth.getInstance()
                        .signOut();
                    Intent intent=new Intent(requireView().getContext(), LoginActivity.class);
                    startActivity(intent); }).show();
            }
        };
        AdapterRecyclerProfileButton adapterRPB=
                new AdapterRecyclerProfileButton(bPRlist, requireView().getContext(),
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
        xpPoint = requireView().findViewById(id.expBar);
        LineXp = requireView().findViewById(id.Xp_progress);
        nameText = requireView().findViewById(id.nameTextView);
        buttonImageUser=requireView().findViewById(id.buttonImageUser);
        rv = requireView().findViewById(id.list_button1);
        LineXp = requireView().findViewById(id.Xp_progress);
        xpPoint = requireView().findViewById(id.expBar);
        levelTv = requireView().findViewById(id.Level);
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
        UCrop uCrop=UCrop.of(uri,Uri.fromFile
                (new File(requireContext().getCacheDir(),destinationFileName)));
        uCrop.withAspectRatio(1f,1f);
        uCrop.withOptions(getUCropOptions());
        uCrop.start(requireContext(),ProfileFragment.this);
    }


    private UCrop.Options getUCropOptions(){
        UCrop.Options options=new UCrop.Options();
        //options.setStatusBarColor(bar);
        options.setCircleDimmedLayer(true);
        options.withMaxResultSize
                (requireView().getWidth()/3, requireView().getHeight()/3);
        //options.setToolbarColor(bar);
        return options;
    }


    @SuppressLint("SetTextI18n")
    private void XpUpdate(){
        if (user!=null){
            LineXp= requireView().findViewById(id.Xp_progress);
            xpPoint= requireView().findViewById(id.expBar);
            levelTv= requireView().findViewById(id.Level);
            DocumentReference docRef= db.collection("UserProfile")
                    .document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        int levelUser=Integer.parseInt(Objects.requireNonNull
                                (Objects.requireNonNull(document.getData())
                                        .put("level", 0)).toString());
                        int xpUser=Integer.parseInt(Objects.requireNonNull(document.getData()
                                .put("xpUser", 0)).toString());
                        int xpMax=Integer.parseInt(Objects.requireNonNull(document.getData()
                                .put("xpMax", 0)).toString());
                        if (xpUser<xpMax){
                            LineXp.setProgress(xpUser);
                            LineXp.setMax(xpMax);
                            xpPoint.setText(""+xpUser+"/"+xpMax+"Xp");
                            levelTv.setText("Уровень: "+levelUser);
                        }
                        if (xpUser>xpMax){
                            while (xpUser>xpMax){
                                docRef.update("xpUser",xpUser-xpMax)
                                        .addOnSuccessListener(aVoid ->
                                        Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                        .addOnFailureListener(e ->
                                                Log.w(TAG, "Error updating document", e));
                                xpMax+=200*(levelUser+1);

                                docRef.update("xpMax",xpMax).addOnSuccessListener(aVoid ->
                                        Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                        .addOnFailureListener(e ->
                                                Log.w(TAG, "Error updating document", e));
                                LineXp.setMax(xpMax);
                                levelUser+=1;
                                int xpUserNew=Integer
                                        .parseInt(Objects.requireNonNull
                                                (document.getData().put("xpUser", 0)).toString());
                                levelTv.setText("Уровень: "+levelUser);
                                xpPoint.setText(""+xpUserNew+"/"+xpMax+"Xp");
                                levelTv.setText("Уровень: "+levelUser);
                                XpUpdate();

                            }
                        }else {
                            levelTv.setText("Уровень: "+levelUser);
                            xpPoint.setText(""+xpUser+"/"+xpMax+"Xp");
                            levelTv.setText("Уровень: "+levelUser);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        docRef.update("level",levelUser).addOnSuccessListener(aVoid ->
                                Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(e ->
                                        Log.w(TAG, "Error updating document", e));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipeRefreshLayout.setRefreshing(true);
            XpUpdate();
            LoadImageProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

