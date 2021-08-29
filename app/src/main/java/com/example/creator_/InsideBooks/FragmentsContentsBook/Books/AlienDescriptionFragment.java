package com.example.creator_.InsideBooks.FragmentsContentsBook.Books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.creator_.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AlienDescriptionFragment extends Fragment {
    private TextInputLayout description;
    private String idBook;
    private TextView payText;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.description_fragment_layout,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        description = view.findViewById(R.id.descriptionOwner);
        payText = view.findViewById(R.id.payLink);
    }

    @Override
    public void onResume() {
        super.onResume();
        dsrUpdate();
    }

    protected void dsrUpdate(){
        BookToolsActivity bookToolsActivity = (BookToolsActivity) this.getActivity();
        idBook = Objects.requireNonNull(bookToolsActivity).StringIdBookAndUserID().get(0);
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot =task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    Objects.requireNonNull(description.getEditText()).setText((String) snapshot.get("description"));
                    payText.setText((String) snapshot.get("paySystem"));
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        dsrUpdate();
    }
}
