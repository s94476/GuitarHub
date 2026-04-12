package com.example.guitarhub.utils;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> allPosts = new ArrayList<>();
    private List<Post> filteredPosts = new ArrayList<>();
    private Set<String> favoritePostIds = new HashSet<>();
    private OnPostInteractionListener onPostInteractionListener;
    private boolean showingFavoritesOnly = false;
    private String currentQuery = "";
    private String currentUserId;

    public void setPosts(List<Post> posts) {
        this.allPosts = new ArrayList<>(posts);
        applyFilter();
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
        notifyDataSetChanged();
    }

    public void setFavoritePostIds(List<String> favoritePostIds) {
        this.favoritePostIds.clear();
        if (favoritePostIds != null) {
            this.favoritePostIds.addAll(favoritePostIds);
        }
        applyFilter();
    }

    public void setShowingFavoritesOnly(boolean favoritesOnly) {
        this.showingFavoritesOnly = favoritesOnly;
        applyFilter();
    }

    public void filter(String query) {
        this.currentQuery = query != null ? query.toLowerCase() : "";
        applyFilter();
    }

    private void applyFilter() {
        filteredPosts = allPosts.stream()
                .filter(post -> {
                    if (showingFavoritesOnly && !favoritePostIds.contains(post.getPostId())) {
                        return false;
                    }
                    if (currentQuery.isEmpty()) {
                        return true;
                    }
                    boolean matchSong = post.getSongTitle() != null && post.getSongTitle().toLowerCase().contains(currentQuery);
                    boolean matchArtist = post.getArtist() != null && post.getArtist().toLowerCase().contains(currentQuery);
                    boolean matchAmp = post.getAmpName() != null && post.getAmpName().toLowerCase().contains(currentQuery);
                    boolean matchUser = post.getOwnerNickname() != null && post.getOwnerNickname().toLowerCase().contains(currentQuery);
                    boolean matchGenre = post.getGenre() != null && post.getGenre().toLowerCase().contains(currentQuery);
                    return matchSong || matchArtist || matchAmp || matchUser || matchGenre;
                })
                .sorted((p1, p2) -> {
                    int likes1 = p1.getLikedBy() != null ? p1.getLikedBy().size() : 0;
                    int likes2 = p2.getLikedBy() != null ? p2.getLikedBy().size() : 0;
                    return Integer.compare(likes2, likes1);
                })
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public interface OnPostInteractionListener {
        void onFavoriteClick(String postId, boolean isFavorite);
        void onLikeClick(String postId, boolean isLiked);
        void onCommentSend(String postId, String commentText);
    }

    public void setOnPostInteractionListener(OnPostInteractionListener listener) {
        this.onPostInteractionListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = filteredPosts.get(position);

        holder.songTextView.setText(post.getSongTitle());
        holder.postedByTextView.setText("Posted by: " + post.getOwnerNickname());
        holder.artistTextView.setText(post.getArtist());
        holder.genreTextView.setText("Genre: " + (post.getGenre() != null ? post.getGenre() : "N/A"));
        holder.recommendedLevelTextView.setText("Recommended Level: " + post.getRecommendedLevel());
        holder.gainTextView.setText("Gain: " + post.getGain().getIntensity() + "/10");
        holder.trebleTextView.setText("Treble: " + post.getTreble().getIntensity() + "/10");
        holder.bassTextView.setText("Bass: " + post.getBass().getIntensity() + "/10");
        holder.middleTextView.setText("Middle: " + post.getMiddle().getIntensity() + "/10");
        holder.ampNameTextView.setText("Amp: " + post.getAmpName());
        holder.ampPositionTextView.setText("Position: " + post.getAmpPosition());
        holder.toneTextView.setText("Tone: " + post.getTone() + "/10");

        holder.effect1.setVisibility(View.GONE);
        holder.effect2.setVisibility(View.GONE);
        holder.effect3.setVisibility(View.GONE);
        holder.effect4.setVisibility(View.GONE);

        List<Effect> effects = post.getEffects();
        if (effects != null) {
            if (effects.size() > 0) {
                holder.effect1.setText(effects.get(0).getName() + ": " + effects.get(0).getIntensity() + "/10");
                holder.effect1.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 1) {
                holder.effect2.setText(effects.get(1).getName() + ": " + effects.get(1).getIntensity() + "/10");
                holder.effect2.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 2) {
                holder.effect3.setText(effects.get(2).getName() + ": " + effects.get(2).getIntensity() + "/10");
                holder.effect3.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 3) {
                holder.effect4.setText(effects.get(3).getName() + ": " + effects.get(3).getIntensity() + "/10");
                holder.effect4.setVisibility(View.VISIBLE);
            }
        }

        boolean isFavorite = favoritePostIds.contains(post.getPostId());
        holder.favoriteIcon.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);

        holder.favoriteIcon.setOnClickListener(v -> {
            if (onPostInteractionListener != null && post.getPostId() != null) {
                onPostInteractionListener.onFavoriteClick(post.getPostId(), !isFavorite);
            }
        });

        List<String> likedBy = post.getLikedBy();
        boolean isLiked = currentUserId != null && likedBy != null && likedBy.contains(currentUserId);
        holder.likeIcon.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
        holder.likesCountTextView.setText(String.valueOf(likedBy != null ? likedBy.size() : 0));

        holder.likeLayout.setOnClickListener(v -> {
            if (onPostInteractionListener != null && post.getPostId() != null) {
                onPostInteractionListener.onLikeClick(post.getPostId(), !isLiked);
            }
        });

        holder.commentsContainer.removeAllViews();
        List<Comment> comments = post.getComments();
        if (comments != null) {
            for (Comment comment : comments) {
                TextView tvComment = new TextView(holder.itemView.getContext());
                tvComment.setText(comment.getUsername() + ": " + comment.getText());
                tvComment.setPadding(0, 4, 0, 4);
                tvComment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.commentsContainer.addView(tvComment);
            }
        }

        holder.sendCommentButton.setOnClickListener(v -> {
            String commentText = holder.commentEditText.getText().toString().trim();
            if (!commentText.isEmpty() && onPostInteractionListener != null) {
                onPostInteractionListener.onCommentSend(post.getPostId(), commentText);
                holder.commentEditText.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView songTextView, postedByTextView, artistTextView, genreTextView, recommendedLevelTextView;
        TextView gainTextView, trebleTextView, bassTextView, middleTextView, ampNameTextView, ampPositionTextView, toneTextView;
        TextView effect1, effect2, effect3, effect4;
        ImageView favoriteIcon, likeIcon;
        TextView likesCountTextView;
        View likeLayout;
        LinearLayout commentsContainer;
        EditText commentEditText;
        ImageButton sendCommentButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            songTextView = itemView.findViewById(R.id.tv_song_title);
            postedByTextView = itemView.findViewById(R.id.tv_posted_by);
            artistTextView = itemView.findViewById(R.id.tv_artist);
            genreTextView = itemView.findViewById(R.id.tv_genre);
            recommendedLevelTextView = itemView.findViewById(R.id.tv_recommended_level);
            gainTextView = itemView.findViewById(R.id.tv_gain);
            trebleTextView = itemView.findViewById(R.id.tv_treble);
            bassTextView = itemView.findViewById(R.id.tv_bass);
            middleTextView = itemView.findViewById(R.id.tv_middle);
            ampNameTextView = itemView.findViewById(R.id.tv_amp_name);
            ampPositionTextView = itemView.findViewById(R.id.tv_amp_position);
            toneTextView = itemView.findViewById(R.id.tv_tone);
            effect1 = itemView.findViewById(R.id.tv_effect1);
            effect2 = itemView.findViewById(R.id.tv_effect2);
            effect3 = itemView.findViewById(R.id.tv_effect3);
            effect4 = itemView.findViewById(R.id.tv_effect4);
            favoriteIcon = itemView.findViewById(R.id.iv_favorite);
            likeLayout = itemView.findViewById(R.id.ll_like);
            likeIcon = itemView.findViewById(R.id.iv_like);
            likesCountTextView = itemView.findViewById(R.id.tv_likes_count);
            commentsContainer = itemView.findViewById(R.id.comments_container);
            commentEditText = itemView.findViewById(R.id.et_comment);
            sendCommentButton = itemView.findViewById(R.id.btn_send_comment);
        }
    }
}
