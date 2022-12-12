package edu.uncc.hw07;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import edu.uncc.hw07.databinding.CommentRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumBinding;

public class ForumFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentForumBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String forum_title;
    private String created_by;
    private String forum_text;
    private String forum_id;

    public ForumFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance(String param1, String param2, String param3, String param4) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forum_title = getArguments().getString(ARG_PARAM1);
            created_by = getArguments().getString(ARG_PARAM2);
            forum_text = getArguments().getString(ARG_PARAM3);
            forum_id = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        forumAdapter = new ForumAdapter();
        binding.recyclerView.setAdapter(forumAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forum");
        binding.textViewForumTitle.setText(forum_title);
        binding.textViewForumCreatedBy.setText(created_by);
        binding.textViewForumText.setText(forum_text);

        db.collection("forums").document(forum_id).collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mForumComment.clear();
                if (value.size() < 0) {
                } else {
                    for (QueryDocumentSnapshot document : value) {
                        mForumComment.add(new ForumComment(document.getString("comment_created_by"), document.getString("comment_uid"), document.getString("comment_description"), document.getString("comment_datetime"), document.getString("forum_id"), document.getString("comment_id")));
                    }
                    binding.textViewCommentsCount.setText(mForumComment.size() + " Comments");
                }
                forumAdapter.notifyDataSetChanged();
            }
        });

        binding.buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.editTextComment.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Please enter a comment!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.create().show();
                } else {
                    HashMap<String, String> created_post = new HashMap<>();

                    created_post.put("comment_created_by", created_by);
                    created_post.put("comment_uid", FirebaseAuth.getInstance().getUid());
                    created_post.put("comment_description", binding.editTextComment.getText().toString());

                    SimpleDateFormat simpledate = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                    String dateString = simpledate.format(new Date());
                    created_post.put("comment_datetime", dateString);

                    Random rnd = new Random();
                    int n = 100000 + rnd.nextInt(900000);

                    created_post.put("comment_id", String.valueOf(n));
                    created_post.put("forum_id", forum_id);

                    db.collection("forums").document(forum_id).collection("comments").document(String.valueOf(n)).set(created_post).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            forumAdapter.notifyDataSetChanged();
                        }
                    });
                    binding.editTextComment.setText("");
                }
            }
        });
    }

    ForumAdapter forumAdapter;
    ArrayList<ForumComment> mForumComment = new ArrayList<>();

    class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {
        //
        @NonNull
        @Override
        public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CommentRowItemBinding binding = CommentRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
            ForumComment forumComment = mForumComment.get(position);
            holder.setupUI(forumComment);
        }

        @Override
        public int getItemCount() {
            return mForumComment.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {
            CommentRowItemBinding commentRowItemBinding;
            ForumComment mforumComment;

            public ForumViewHolder(CommentRowItemBinding binding) {
                super(binding.getRoot());
                commentRowItemBinding = binding;
            }

            public void setupUI(ForumComment forumComment) {
                mforumComment = forumComment;
                commentRowItemBinding.textViewCommentCreatedBy.setText(forumComment.getCreated_by());
                commentRowItemBinding.textViewCommentText.setText(forumComment.getComment_description());
                commentRowItemBinding.textViewCommentCreatedAt.setText(forumComment.getComment_datetime());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (forumComment.getComment_uid().matches(user.getUid())) {
                    commentRowItemBinding.imageViewDelete.setImageResource(R.drawable.rubbish_bin);
                    commentRowItemBinding.imageViewDelete.setVisibility(View.VISIBLE);

                    commentRowItemBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.collection("forums").document(forum_id).collection("comments").document(forumComment.getComment_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Comment successfully deleted!");
                                }
                            });
                        }
                    });

                } else {
                    commentRowItemBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}