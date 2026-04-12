package com.example.guitarhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SearchView searchView;
    private TabLayout tabLayout;
    private String currentUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        initRecyclerView(view);
        initSearchView(view);
        initTabs(view);
        
        fetchCurrentUserData();
        fetchPosts();
    }

    private void initRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        if (currentUser != null) {
            postsAdapter.setCurrentUserId(currentUser.getUid());
        }
        recyclerView.setAdapter(postsAdapter);
    }

    private void initSearchView(@NonNull View view) {
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                postsAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postsAdapter.filter(newText);
                return false;
            }
        });
    }

    private void initTabs(@NonNull View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    postsAdapter.setShowingFavoritesOnly(false);
                } else if (tab.getPosition() == 1) {
                    postsAdapter.setShowingFavoritesOnly(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchCurrentUserData() {
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.addSnapshotListener((documentSnapshot, error) -> {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        currentUsername = user.getUsername();
                        if (user.getFavoritePosts() != null) {
                            postsAdapter.setFavoritePostIds(user.getFavoritePosts());
                        }
                    }
                }
            });
        }
    }

    private void fetchPosts() {
        db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        List<Post> posts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            posts.add(post);
                        }
                        postsAdapter.setPosts(posts);
                    }
                });
    }

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
