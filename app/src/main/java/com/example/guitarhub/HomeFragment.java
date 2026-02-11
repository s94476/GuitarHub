package com.example.guitarhub;

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

import com.example.guitarhub.utils.Post;
import com.example.guitarhub.utils.PostsAdapter;
import com.example.guitarhub.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PostsAdapter.OnFavoriteClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fetchPosts();
        fetchFavoritePosts();
    }

    private void initRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnFavoriteClickListener(this);
        recyclerView.setAdapter(postsAdapter);
    }

    private void fetchPosts() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> posts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            posts.add(post);
                        }
                        postsAdapter.setPosts(posts);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchFavoritePosts() {
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getFavoritePosts() != null) {
                        postsAdapter.setFavoritePostIds(user.getFavoritePosts());
                    }
                }
            });
        }
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
}
