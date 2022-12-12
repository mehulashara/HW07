package edu.uncc.hw07;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import edu.uncc.hw07.databinding.FragmentCreateForumBinding;

public class CreateForumFragment extends Fragment {
    final private String TAG = "demo";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentCreateForumBinding binding;

    public CreateForumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Forum");

        binding.buttonSubmitForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.editTextForumTitle.getText().toString();
                String description = binding.editTextForumDescript.getText().toString();
                if (description.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Missing Description !")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ;
                                }
                            });
                    builder.create().show();
                } else if (title.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Missing Title !")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.create().show();

                } else {
                    setData();
                    mListener.buttonCancel();
                }

            }
        });

        binding.buttonCancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.buttonCancel();
            }
        });
    }

    private void setData() {
        HashMap<String, Object> created_forum = new HashMap<>();
        created_forum.put("forum_title", binding.editTextForumTitle.getText().toString());
        created_forum.put("forum_description", binding.editTextForumDescript.getText().toString());
        created_forum.put("forum_user_id", FirebaseAuth.getInstance().getUid());
        created_forum.put("likes", new ArrayList<String>());

        SimpleDateFormat simpledate = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        String dateString = simpledate.format(new Date());
        created_forum.put("created_at", dateString);

        created_forum.put("created_by_name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);

        created_forum.put("forum_id", String.valueOf(n));

        db.collection("forums").document(String.valueOf(n)).set(created_forum).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public CreateForumsListner mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateForumsListner) context;
    }

    interface CreateForumsListner {
        void buttonCancel();
    }
}