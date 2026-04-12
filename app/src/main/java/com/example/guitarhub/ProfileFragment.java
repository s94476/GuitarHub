package com.example.guitarhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.utils.Post;
import com.example.guitarhub.utils.PostsAdapter;
import com.example.guitarhub.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.guitarhub.utils.Comment;
import com.example.guitarhub.utils.Post;
import com.example.guitarhub.utils.PostsAdapter;
import com.example.guitarhub.utils.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fragment that displays the user's profile information.
 * It shows the user's email, username, popularity stats, and provides tabs for their posts, likes, and comments.
 */
public class ProfileFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private static final String TAG = "ProfileFragment";

    // UI Components
    private TextView tvEmail, tvUsername, tvTotalLikes, tvPostsCount, tvTotalComments;
    private Button btnChangePassword, btnLogout;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String currentUsername;
    private ListenerRegistration userListener, postsListener;

    // Data
    private List<Post> allPosts = new ArrayList<>();
    private List<String> favoritePostIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI component references
        tvEmail = view.findViewById(R.id.tv_email);
        tvUsername = view.findViewById(R.id.tv_username);
        tvTotalLikes = view.findViewById(R.id.tv_total_likes);
        tvPostsCount = view.findViewById(R.id.tv_posts_count);
        tvTotalComments = view.findViewById(R.id.tv_total_comments);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);
        tabLayout = view.findViewById(R.id.profile_tab_layout);
        recyclerView = view.findViewById(R.id.recycler_profile_posts);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        setupRecyclerView();
        setupTabs();

        if (currentUser != null) {
            loadUserProfile();
            listenToPosts();
        }

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        if (currentUser != null) {
            postsAdapter.setCurrentUserId(currentUser.getUid());
        }
        recyclerView.setAdapter(postsAdapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateFilter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadUserProfile() {
        tvEmail.setText(currentUser.getEmail());
        
        userListener = db.collection("users").document(currentUser.getUid())
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            currentUsername = user.getUsername();
                            tvUsername.setText(currentUsername);
                            favoritePostIds = user.getFavoritePosts() != null ? user.getFavoritePosts() : new ArrayList<>();
                            postsAdapter.setFavoritePostIds(favoritePostIds);
                            updateFilter();
                        }
                    }
                });
    }

    private void listenToPosts() {
        postsListener = db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        allPosts.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            allPosts.add(post);
                        }
                        updatePopularityMeter();
                        updateFilter();
                    }
                });
    }

    private void updatePopularityMeter() {
        int totalLikes = 0;
        int myPostsCount = 0;
        int totalComments = 0;

        for (Post post : allPosts) {
            if (post.getOwnerUid() != null && post.getOwnerUid().equals(currentUser.getUid())) {
                myPostsCount++;
                if (post.getLikedBy() != null) {
                    totalLikes += post.getLikedBy().size();
                }
                if (post.getComments() != null) {
                    totalComments += post.getComments().size();
                }
            }
        }

        tvTotalLikes.setText(String.valueOf(totalLikes));
        tvPostsCount.setText(String.valueOf(myPostsCount));
        tvTotalComments.setText(String.valueOf(totalComments));
    }

    private void updateFilter() {
        if (allPosts.isEmpty()) return;

        List<Post> filtered;
        int selectedTab = tabLayout.getSelectedTabPosition();

        switch (selectedTab) {
            case 0: // My Posts
                filtered = allPosts.stream()
                        .filter(p -> p.getOwnerUid() != null && p.getOwnerUid().equals(currentUser.getUid()))
                        .collect(Collectors.toList());
                break;
            case 1: // Liked (Posts the user liked)
                filtered = allPosts.stream()
                        .filter(p -> p.getLikedBy() != null && p.getLikedBy().contains(currentUser.getUid()))
                        .collect(Collectors.toList());
                break;
            case 2: // Commented (Posts the user commented on)
                filtered = allPosts.stream()
                        .filter(p -> p.getComments() != null && p.getComments().stream()
                                .anyMatch(c -> c.getUsername() != null && c.getUsername().equals(currentUsername)))
                        .collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>();
        }

        postsAdapter.setPosts(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) userListener.remove();
        if (postsListener != null) postsListener.remove();
    }

    // OnPostInteractionListener implementation (same as HomeFragment)
    @Override
    public void onFavoriteClick(String postId, boolean isFavorite) {
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            if (isFavorite) {
                userRef.update("favoritePosts", FieldValue.arrayUnion(postId));
            } else {
                userRef.update("favoritePosts", FieldValue.arrayRemove(postId));
            }
        }
    }

    @Override
    public void onLikeClick(String postId, boolean isLiked) {
        if (currentUser != null) {
            DocumentReference postRef = db.collection("posts").document(postId);
            if (isLiked) {
                postRef.update("likedBy", FieldValue.arrayUnion(currentUser.getUid()));
            } else {
                postRef.update("likedBy", FieldValue.arrayRemove(currentUser.getUid()));
            }
        }
    }

    @Override
    public void onCommentSend(String postId, String commentText) {
        if (currentUser != null && currentUsername != null) {
            DocumentReference postRef = db.collection("posts").document(postId);
            Comment newComment = new Comment(currentUsername, commentText);
            postRef.update("comments", FieldValue.arrayUnion(newComment));
        }
    }
}

