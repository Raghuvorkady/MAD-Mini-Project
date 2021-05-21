package csmp.part_b.p5.ui.fragments.player.card;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import csmp.part_b.p5.R;
import csmp.part_b.p5.adapter.base.MediaEntryViewHolder;
import csmp.part_b.p5.adapter.song.PlayingQueueAdapter;
import csmp.part_b.p5.dialogs.SongShareDialog;
import csmp.part_b.p5.helper.MusicPlayerRemote;
import csmp.part_b.p5.helper.menu.SongMenuHelper;
import csmp.part_b.p5.model.Song;
import csmp.part_b.p5.ui.activities.base.AbsSlidingMusicPanelActivity;
import csmp.part_b.p5.ui.fragments.player.AbsPlayerFragment;
import csmp.part_b.p5.ui.fragments.player.PlayerAlbumCoverFragment;
import csmp.part_b.p5.util.MusicUtil;
import csmp.part_b.p5.util.Util;
import csmp.part_b.p5.util.ViewUtil;
import csmp.part_b.p5.views.WidthFitSquareLayout;

public class CardPlayerFragment extends AbsPlayerFragment implements PlayerAlbumCoverFragment.Callbacks, SlidingUpPanelLayout.PanelSlideListener {

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.player_toolbar)
    Toolbar toolbar;
    @BindView(R.id.player_sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.player_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.playing_queue_card)
    CardView playingQueueCard;
    @BindView(R.id.color_background)
    View colorBackground;
    @BindView(R.id.player_queue_sub_header)
    TextView playerQueueSubHeader;

    private int lastColor;

    private CardPlayerPlaybackControlsFragment playbackControlsFragment;
    private PlayerAlbumCoverFragment playerAlbumCoverFragment;

    private LinearLayoutManager layoutManager;

    private PlayingQueueAdapter playingQueueAdapter;

    private RecyclerView.Adapter wrappedAdapter;
    private RecyclerViewDragDropManager recyclerViewDragDropManager;

    private Impl impl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Util.isLandscape(getResources())) {
            impl = new LandscapeImpl(this);
        } else {
            impl = new PortraitImpl(this);
        }

        View view = inflater.inflate(R.layout.fragment_card_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        impl.init();

        setUpPlayerToolbar();
        setUpSubFragments();

        setUpRecyclerView();

        slidingUpPanelLayout.addPanelSlideListener(this);
        slidingUpPanelLayout.setDragView(view.findViewById(R.id.draggable_area));

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                impl.setUpPanelAndAlbumCoverHeight();
            }
        });

        // for some reason the xml attribute doesn't get applied here.
        playingQueueCard.setCardBackgroundColor(ATHUtil.resolveColor(getActivity(), R.attr.cardBackgroundColor));
    }

    @Override
    public void onDestroyView() {
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.removePanelSlideListener(this);
        }
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.release();
            recyclerViewDragDropManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        playingQueueAdapter = null;
        layoutManager = null;
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.cancelDrag();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkToggleToolbar(toolbarContainer);
    }

    @Override
    public void onServiceConnected() {
        updateQueue();
        updateCurrentSong();
    }

    @Override
    public void onPlayingMetaChanged() {
        updateCurrentSong();
        updateQueuePosition();
    }

    @Override
    public void onQueueChanged() {
        updateQueue();
    }

    @Override
    public void onMediaStoreChanged() {
        updateQueue();
    }

    private void updateQueue() {
        playingQueueAdapter.swapDataSet(MusicPlayerRemote.getPlayingQueue(), MusicPlayerRemote.getPosition());
        playerQueueSubHeader.setText(getUpNextAndQueueTime());
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            resetToCurrentPosition();
        }
    }

    private void updateQueuePosition() {
        playingQueueAdapter.setCurrent(MusicPlayerRemote.getPosition());
        playerQueueSubHeader.setText(getUpNextAndQueueTime());
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            resetToCurrentPosition();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void updateCurrentSong() {
        impl.updateCurrentSong(MusicPlayerRemote.getCurrentSong());
    }

    private void setUpSubFragments() {
        playbackControlsFragment = (CardPlayerPlaybackControlsFragment) getChildFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        playerAlbumCoverFragment = (PlayerAlbumCoverFragment) getChildFragmentManager().findFragmentById(R.id.player_album_cover_fragment);

        playerAlbumCoverFragment.setCallbacks(this);
    }

    private void setUpPlayerToolbar() {
        //toolbar.inflateMenu(R.menu.menu_player);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return super.onMenuItemClick(item);
    }

    private void setUpRecyclerView() {
        recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        playingQueueAdapter = new PlayingQueueAdapter(
                ((AppCompatActivity) getActivity()),
                MusicPlayerRemote.getPlayingQueue(),
                MusicPlayerRemote.getPosition(),
                R.layout.item_list,
                false,
                null);
        wrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(playingQueueAdapter);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wrappedAdapter);
        recyclerView.setItemAnimator(animator);

        recyclerViewDragDropManager.attachRecyclerView(recyclerView);

        layoutManager.scrollToPositionWithOffset(MusicPlayerRemote.getPosition() + 1, 0);
    }

    @Override
    @ColorInt
    public int getPaletteColor() {
        return lastColor;
    }

    private void animateColorChange(final int newColor) {
        impl.animateColorChange(newColor);
        lastColor = newColor;
    }

    @Override
    public void onShow() {
        playbackControlsFragment.show();
    }

    @Override
    public void onHide() {
        playbackControlsFragment.hide();
        onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        boolean wasExpanded = false;
        if (slidingUpPanelLayout != null) {
            wasExpanded = slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        return wasExpanded;
    }

    @Override
    public void onColorChanged(int color) {
        animateColorChange(color);
        playbackControlsFragment.setDark(ColorUtil.isColorLight(color));
        getCallbacks().onPaletteColorChanged();
    }

    @Override
    public void onToolbarToggled() {
        toggleToolbar(toolbarContainer);
    }

    @Override
    public void onPanelSlide(View view, float slide) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float density = getResources().getDisplayMetrics().density;

            float cardElevation = (6 * slide + 2) * density;
            if (!isValidElevation(cardElevation))
                return; // we have received some crash reports in setCardElevation()
            playingQueueCard.setCardElevation(cardElevation);

            float buttonElevation = (2 * Math.max(0, (1 - (slide * 16))) + 2) * density;
            if (!isValidElevation(buttonElevation)) return;
            playbackControlsFragment.playPauseFab.setElevation(buttonElevation);
        }
    }

    private boolean isValidElevation(float elevation) {
        return elevation >= -Float.MAX_VALUE && elevation <= Float.MAX_VALUE;
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed(panel);
                break;
            case ANCHORED:
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // this fixes a bug where the panel would get stuck for some reason
                break;
        }
    }

    public void onPanelCollapsed(View panel) {
        resetToCurrentPosition();
    }

    private void resetToCurrentPosition() {
        recyclerView.stopScroll();
        layoutManager.scrollToPositionWithOffset(MusicPlayerRemote.getPosition() + 1, 0);
    }

    interface Impl {
        void init();

        void updateCurrentSong(Song song);

        void animateColorChange(final int newColor);

        void setUpPanelAndAlbumCoverHeight();
    }

    private static abstract class BaseImpl implements Impl {
        protected CardPlayerFragment fragment;

        public BaseImpl(CardPlayerFragment fragment) {
            this.fragment = fragment;
        }

        public AnimatorSet createDefaultColorChangeAnimatorSet(int newColor) {
            Animator backgroundAnimator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //noinspection ConstantConditions
                int x = (int) (fragment.playbackControlsFragment.playPauseFab.getX() + fragment.playbackControlsFragment.playPauseFab.getWidth() / 2 + fragment.playbackControlsFragment.getView().getX());
                int y = (int) (fragment.playbackControlsFragment.playPauseFab.getY() + fragment.playbackControlsFragment.playPauseFab.getHeight() / 2 + fragment.playbackControlsFragment.getView().getY() + fragment.playbackControlsFragment.progressSlider.getHeight());
                float startRadius = Math.max(fragment.playbackControlsFragment.playPauseFab.getWidth() / 2, fragment.playbackControlsFragment.playPauseFab.getHeight() / 2);
                float endRadius = Math.max(fragment.colorBackground.getWidth(), fragment.colorBackground.getHeight());
                fragment.colorBackground.setBackgroundColor(newColor);
                backgroundAnimator = ViewAnimationUtils.createCircularReveal(fragment.colorBackground, x, y, startRadius, endRadius);
            } else {
                backgroundAnimator = ViewUtil.createBackgroundColorTransition(fragment.colorBackground, fragment.lastColor, newColor);
            }

            AnimatorSet animatorSet = new AnimatorSet();

            animatorSet.play(backgroundAnimator);

            if (!ATHUtil.isWindowBackgroundDark(fragment.getActivity())) {
                int adjustedLastColor = ColorUtil.isColorLight(fragment.lastColor) ? ColorUtil.darkenColor(fragment.lastColor) : fragment.lastColor;
                int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
                Animator subHeaderAnimator = ViewUtil.createTextColorTransition(fragment.playerQueueSubHeader, adjustedLastColor, adjustedNewColor);
                animatorSet.play(subHeaderAnimator);
            }

            animatorSet.setDuration(ViewUtil.MUSIC_ANIM_TIME);
            return animatorSet;
        }

        @Override
        public void animateColorChange(int newColor) {
            if (ATHUtil.isWindowBackgroundDark(fragment.getActivity())) {
                fragment.playerQueueSubHeader.setTextColor(ThemeStore.textColorSecondary(fragment.getActivity()));
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class PortraitImpl extends BaseImpl {
        MediaEntryViewHolder currentSongViewHolder;
        Song currentSong = Song.EMPTY_SONG;

        public PortraitImpl(CardPlayerFragment fragment) {
            super(fragment);
        }

        @Override
        public void init() {
            currentSongViewHolder = new MediaEntryViewHolder(fragment.getView().findViewById(R.id.current_song));

            currentSongViewHolder.separator.setVisibility(View.VISIBLE);
            currentSongViewHolder.shortSeparator.setVisibility(View.GONE);
            currentSongViewHolder.image.setScaleType(ImageView.ScaleType.CENTER);
            currentSongViewHolder.image.setColorFilter(ATHUtil.resolveColor(fragment.getActivity(), R.attr.iconColor, ThemeStore.textColorSecondary(fragment.getActivity())), PorterDuff.Mode.SRC_IN);
            currentSongViewHolder.image.setImageResource(R.drawable.ic_volume_up_white_24dp);
            currentSongViewHolder.itemView.setOnClickListener(v -> {
                // toggle the panel
                if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                } else if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });
            currentSongViewHolder.menu.setOnClickListener(new SongMenuHelper.OnClickSongMenu((AppCompatActivity) fragment.getActivity()) {
                @Override
                public Song getSong() {
                    return currentSong;
                }

                public int getMenuRes() {
                    return R.menu.menu_item_playing_queue_song;
                }

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_remove_from_playing_queue:
                            MusicPlayerRemote.removeFromQueue(MusicPlayerRemote.getPosition());
                            return true;
                        case R.id.action_share:
                            SongShareDialog.create(getSong()).show(fragment.getFragmentManager(), "SONG_SHARE_DIALOG");
                            return true;
                    }
                    return super.onMenuItemClick(item);
                }
            });
        }

        @Override
        public void setUpPanelAndAlbumCoverHeight() {
            WidthFitSquareLayout albumCoverContainer = fragment.getView().findViewById(R.id.album_cover_container);

            final int availablePanelHeight = fragment.slidingUpPanelLayout.getHeight() - fragment.getView().findViewById(R.id.player_content).getHeight() + (int) ViewUtil.convertDpToPixel(8, fragment.getResources());
            final int minPanelHeight = (int) ViewUtil.convertDpToPixel(72 + 24, fragment.getResources());
            if (availablePanelHeight < minPanelHeight) {
                albumCoverContainer.getLayoutParams().height = albumCoverContainer.getHeight() - (minPanelHeight - availablePanelHeight);
                albumCoverContainer.forceSquare(false);
            }
            fragment.slidingUpPanelLayout.setPanelHeight(Math.max(minPanelHeight, availablePanelHeight));

            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.slidingUpPanelLayout.findViewById(R.id.player_panel));
        }

        @Override
        public void updateCurrentSong(Song song) {
            currentSong = song;
            currentSongViewHolder.title.setText(song.title);
            currentSongViewHolder.text.setText(MusicUtil.getSongInfoString(song));
        }

        @Override
        public void animateColorChange(int newColor) {
            super.animateColorChange(newColor);

            fragment.slidingUpPanelLayout.setBackgroundColor(fragment.lastColor);

            createDefaultColorChangeAnimatorSet(newColor).start();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class LandscapeImpl extends BaseImpl {
        public LandscapeImpl(CardPlayerFragment fragment) {
            super(fragment);
        }

        @Override
        public void init() {

        }

        @Override
        public void setUpPanelAndAlbumCoverHeight() {
            int panelHeight = fragment.slidingUpPanelLayout.getHeight() - fragment.playbackControlsFragment.getView().getHeight();
            fragment.slidingUpPanelLayout.setPanelHeight(panelHeight);

            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.slidingUpPanelLayout.findViewById(R.id.player_panel));
        }

        @Override
        public void updateCurrentSong(Song song) {
            fragment.toolbar.setTitle(song.title);
            fragment.toolbar.setSubtitle(MusicUtil.getSongInfoString(song));
        }

        @Override
        public void animateColorChange(int newColor) {
            super.animateColorChange(newColor);

            fragment.slidingUpPanelLayout.setBackgroundColor(fragment.lastColor);

            AnimatorSet animatorSet = createDefaultColorChangeAnimatorSet(newColor);
            animatorSet.play(ViewUtil.createBackgroundColorTransition(fragment.toolbar, fragment.lastColor, newColor))
                    .with(ViewUtil.createBackgroundColorTransition(fragment.getView().findViewById(R.id.status_bar), ColorUtil.darkenColor(fragment.lastColor), ColorUtil.darkenColor(newColor)));
            animatorSet.start();
        }
    }
}
