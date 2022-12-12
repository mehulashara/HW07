package edu.uncc.hw07;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.hw07.databinding.ForumRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumsBinding;

public class ForumsFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentForumsBinding binding;

    String TAG = "demo";

    // wsx@gmail.com 123456

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        forumsAdapter = new ForumsAdapter();
        binding.recyclerView.setAdapter(forumsAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forums");

        db.collection("forums").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mForums.clear();
                if (value.size() < 0) {
                }else {
                    for (QueryDocumentSnapshot document : value) {
                        mForums.add(new Forum(document.getString("forum_title"),document.getString("forum_description"), document.getString("forum_user_id"), document.getString("created_at"), document.getString("created_by_name"), document.getString("forum_id"), (ArrayList<String>) document.get("likes")));
                    }
                }
                Log.d(TAG, "Size:" + mForums.size());
                forumsAdapter.notifyDataSetChanged();
            }
        });

        binding.buttonCreateForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.buttonNewForum();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });
    }

    ForumsAdapter forumsAdapter;
    ArrayList<Forum> mForums = new ArrayList<>();

    public class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapter.ForumsViewHolder> {
        @NonNull
        @Override
        public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForumRowItemBinding binding = ForumRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumsViewHolder holder, int position) {
            Forum forum = mForums.get(position);
            holder.setupUI(forum);
        }

        @Override
        public int getItemCount() {
            return mForums.size();
        }

        class ForumsViewHolder extends RecyclerView.ViewHolder {
            ForumRowItemBinding forumRowItemBinding;
            Forum mForums;

            public ForumsViewHolder(ForumRowItemBinding binding) {
                super(binding.getRoot());
                forumRowItemBinding = binding;
            }

            public void setupUI(Forum forums) {
                mForums = forums;
                forumRowItemBinding.textViewForumTitle.setText(forums.getForum_title());
                forumRowItemBinding.textViewForumCreatedBy.setText(forums.getCreated_by());
                forumRowItemBinding.textViewForumText.setText(forums.getForum_description());
                forumRowItemBinding.textViewForumLikesDate.setText(forums.getLikes().size()+ " Likes | "+ forums.getCreated_at());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(forums.getLikes().contains(user.getUid())) {
                    forumRowItemBinding.imageViewLike.setImageResource(R.drawable.like_favorite);
                }else {
                    forumRowItemBinding.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                }

                forumRowItemBinding.imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(forumRowItemBinding.imageViewLike.getDrawable().getConstantState() == ContextCompat.getDrawable(getContext(), R.drawable.like_not_favorite).getConstantState()) {
                            forumRowItemBinding.imageViewLike.setImageResource(R.drawable.like_favorite);
                            forums.likes.add(user.getUid());
                        }
                        else {
                            forumRowItemBinding.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                            forums.likes.remove(user.getUid());
                        }

                        db.collection("forums")
                                .document(forums.getForum_id())
                                .update("likes", forums.likes)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Likes updated");
                                    }
                                });
                    }
                });

                if (forums.getForum_user_id().matches(user.getUid())) {
                    forumRowItemBinding.imageViewDelete.setImageResource(R.drawable.rubbish_bin);
                    forumRowItemBinding.imageViewDelete.setVisibility(View.VISIBLE);

                    forumRowItemBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.collection("forums").document(forums.getForum_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    db.collection("forums").document(forums.getForum_id()).collection("comments").document().delete();
                                    Log.d(TAG, "Forum successfully deleted!");
                                }
                            });
                        }
                    });

                } else {
                    forumRowItemBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoForumFragment(forumRowItemBinding.textViewForumTitle.getText().toString(),forumRowItemBinding.textViewForumCreatedBy.getText().toString(), forumRowItemBinding.textViewForumText.getText().toString(), forums.forum_id);
                    }
                });
            }
        }
    }

    public ForumsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) context;
    }

    interface ForumsListener {
        void buttonNewForum();
        void logout();
        void gotoForumFragment(String forum_title, String created_by, String forum_description, String forum_id);
    }
}