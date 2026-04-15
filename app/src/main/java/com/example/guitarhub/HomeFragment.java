package com.example.guitarhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.utils.Comment;
import com.example.guitarhub.utils.GeminiManager;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SearchView searchView;
    private TabLayout tabLayout;
    private String currentUsername;
    private User userData;
    private ImageButton aiSearchButton;
    private List<Post> allPosts = new ArrayList<>();

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
        
        // AI Search Button initialization
        aiSearchButton = view.findViewById(R.id.ai_search_button);
        aiSearchButton.setOnClickListener(v -> handleAiSearch());
        
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

    /**
     * Handles the AI Search logic. 
     * Analyzes user preferences and favorites to find the best matching post.
     */
    private void handleAiSearch() {
        if (userData == null || allPosts.isEmpty()) {
            Toast.makeText(getContext(), "Please wait for data to load...", Toast.LENGTH_SHORT).show();
            return;
        }

        aiSearchButton.setEnabled(false);
        Toast.makeText(getContext(), "AI is finding the best song for you...", Toast.LENGTH_SHORT).show();

        // Collect information about user's favorites to understand their taste
        List<Post> favoritePosts = allPosts.stream()
                .filter(p -> (userData.getFavoritePosts() != null && userData.getFavoritePosts().contains(p.getPostId())) || 
                            (p.getLikedBy() != null && p.getLikedBy().contains(currentUser.getUid())))
                .collect(Collectors.toList());

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Based on the following user profile, find the single best song post from the provided list for them.\n\n");
        promptBuilder.append("User Skill Level: ").append(userData.getGuitarLevel()).append("\n");
        promptBuilder.append("User Preferred Genres: ").append(userData.getMusicTastes() != null ? userData.getMusicTastes().toString() : "None").append("\n\n");
        
        if (!favoritePosts.isEmpty()) {
            promptBuilder.append("The user liked/favorited these types of posts recently:\n");
            for (Post p : favoritePosts) {
                promptBuilder.append("- ").append(p.getSongTitle()).append(" (Genre: ").append(p.getGenre()).append(")\n");
            }
            promptBuilder.append("\n");
        }

        promptBuilder.append("Available Posts to choose from:\n");
        for (Post p : allPosts) {
            promptBuilder.append("- Title: ").append(p.getSongTitle()).append(", Artist: ").append(p.getArtist()).append(", Genre: ").append(p.getGenre()).append(", Difficulty: ").append(p.getRecommendedLevel()).append("\n");
        }

        promptBuilder.append("\nGive high priority to Skill Level and Preferred Genres. Decision should be based on similarity to their favorite genres and appropriate difficulty.");
        promptBuilder.append("\nReturn ONLY a JSON object with keys: 'songTitle' and 'artist'. No other text.");

        GeminiManager.getInstance().sendText(promptBuilder.toString(), getContext(), new GeminiManager.GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                if (!isAdded()) return;
                aiSearchButton.setEnabled(true);
                try {
                    String jsonStr = result.trim();
                    if (jsonStr.startsWith("```json")) jsonStr = jsonStr.substring(7, jsonStr.length() - 3).trim();
                    else if (jsonStr.startsWith("```")) jsonStr = jsonStr.substring(3, jsonStr.length() - 3).trim();
                    
                    JSONObject json = new JSONObject(jsonStr);
                    String song = json.getString("songTitle");
                    String artist = json.getString("artist");

                    // Filter the feed by the AI's recommendation
                    // As requested: Show the song name exactly as it is in Firebase (keeping spaces)
                    searchView.setQuery(song, true);
                    
                    Toast.makeText(getContext(), "AI recommends: " + song + " by " + artist, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.e(TAG, "AI Search failed", e);
                    Toast.makeText(getContext(), "AI couldn't find a match this time.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable error) {
                if (!isAdded()) return;
                aiSearchButton.setEnabled(true);
                Toast.makeText(getContext(), "AI Search error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                    userData = documentSnapshot.toObject(User.class);
                    if (userData != null) {
                        currentUsername = userData.getUsername();
                        if (userData.getFavoritePosts() != null) {
                            postsAdapter.setFavoritePostIds(userData.getFavoritePosts());
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
                        allPosts.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            allPosts.add(post);
                        }
                        postsAdapter.setPosts(allPosts);
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
