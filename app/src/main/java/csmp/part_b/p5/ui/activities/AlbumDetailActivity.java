package csmp.part_b.p5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import csmp.part_b.p5.R;
import csmp.part_b.p5.adapter.song.AlbumSongAdapter;
import csmp.part_b.p5.dialogs.DeleteSongsDialog;
import csmp.part_b.p5.glide.MusicColoredTarget;
import csmp.part_b.p5.glide.SongGlideRequest;
import csmp.part_b.p5.helper.MusicPlayerRemote;
import csmp.part_b.p5.interfaces.CabHolder;
import csmp.part_b.p5.interfaces.LoaderIds;
import csmp.part_b.p5.interfaces.PaletteColorHolder;
import csmp.part_b.p5.loader.AlbumLoader;
import csmp.part_b.p5.misc.SimpleObservableScrollViewCallbacks;
import csmp.part_b.p5.misc.WrappedAsyncTaskLoader;
import csmp.part_b.p5.model.Album;
import csmp.part_b.p5.model.Song;
import csmp.part_b.p5.rest.LastFMRestClient;
import csmp.part_b.p5.ui.activities.base.AbsSlidingMusicPanelActivity;
import csmp.part_b.p5.util.MusicColorUtil;
import csmp.part_b.p5.util.MusicUtil;

/**
 * Be careful when changing things in this Activity!
 */
public class AlbumDetailActivity extends AbsSlidingMusicPanelActivity implements PaletteColorHolder, CabHolder, LoaderManager.LoaderCallbacks<Album> {

    private static final int TAG_EDITOR_REQUEST = 2001;
    private static final int LOADER_ID = LoaderIds.ALBUM_DETAIL_ACTIVITY;

    public static final String EXTRA_ALBUM_ID = "extra_album_id";

    private Album album;

    @BindView(R.id.list)
    ObservableRecyclerView recyclerView;
    @BindView(R.id.image)
    ImageView albumArtImageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header)
    View headerView;
    @BindView(R.id.header_overlay)
    View headerOverlay;

    @BindView(R.id.artist_icon)
    ImageView artistIconImageView;
    @BindView(R.id.duration_icon)
    ImageView durationIconImageView;
    @BindView(R.id.song_count_icon)
    ImageView songCountIconImageView;
    @BindView(R.id.album_year_icon)
    ImageView albumYearIconImageView;
    @BindView(R.id.artist_text)
    TextView artistTextView;
    @BindView(R.id.duration_text)
    TextView durationTextView;
    @BindView(R.id.song_count_text)
    TextView songCountTextView;
    @BindView(R.id.album_year_text)
    TextView albumYearTextView;

    private AlbumSongAdapter adapter;

    private MaterialCab cab;
    private int headerViewHeight;
    private int toolbarColor;

    @Nullable
    private Spanned wiki;
    private MaterialDialog wikiDialog;
    private LastFMRestClient lastFMRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        lastFMRestClient = new LastFMRestClient(this);

        setUpObservableListViewParams();
        setUpToolBar();
        setUpViews();

        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    @Override
    protected View createContentView() {
        return wrapSlidingMusicPanel(R.layout.activity_album_detail);
    }

    private final SimpleObservableScrollViewCallbacks observableScrollViewCallbacks = new SimpleObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean b, boolean b2) {
            scrollY += headerViewHeight;

            // Change alpha of overlay
            float headerAlpha = Math.max(0, Math.min(1, (float) 2 * scrollY / headerViewHeight));
            headerOverlay.setBackgroundColor(ColorUtil.withAlpha(toolbarColor, headerAlpha));

            // Translate name text
            headerView.setTranslationY(Math.max(-scrollY, -headerViewHeight));
            headerOverlay.setTranslationY(Math.max(-scrollY, -headerViewHeight));
            albumArtImageView.setTranslationY(Math.max(-scrollY, -headerViewHeight));
        }
    };

    private void setUpObservableListViewParams() {
        headerViewHeight = getResources().getDimensionPixelSize(R.dimen.detail_header_height);
    }

    private void setUpViews() {
        setUpRecyclerView();
        setUpSongsAdapter();
        setColors(DialogUtils.resolveColor(this, R.attr.defaultFooterColor));
    }

    private void loadAlbumCover() {
        SongGlideRequest.Builder.from(Glide.with(this), getAlbum().safeGetFirstSong())
                .checkIgnoreMediaStore(this)
                .generatePalette(this).build()
                .dontAnimate()
                .into(new MusicColoredTarget(albumArtImageView) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    private void setColors(int color) {
        toolbarColor = color;
        headerView.setBackgroundColor(color);

        setNavigationbarColor(color);
        setTaskDescriptionColor(color);

        toolbar.setBackgroundColor(color);
        setSupportActionBar(toolbar); // needed to auto readjust the toolbar content color
        setStatusBarColor(color);

        int secondaryTextColor = MaterialValueHelper.getSecondaryTextColor(this, ColorUtil.isColorLight(color));
        artistIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        durationIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        songCountIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        albumYearIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        artistTextView.setTextColor(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(color)));
        durationTextView.setTextColor(secondaryTextColor);
        songCountTextView.setTextColor(secondaryTextColor);
        albumYearTextView.setTextColor(secondaryTextColor);
    }

    @Override
    public int getPaletteColor() {
        return toolbarColor;
    }

    private void setUpRecyclerView() {
        setUpRecyclerViewPadding();
        recyclerView.setScrollViewCallbacks(observableScrollViewCallbacks);
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.post(() -> observableScrollViewCallbacks.onScrollChanged(-headerViewHeight, false, false));
    }

    private void setUpRecyclerViewPadding() {
        recyclerView.setPadding(0, headerViewHeight, 0, 0);
    }

    private void setUpToolBar() {
        toolbar.setTitleTextAppearance(this, R.style.ProductSansTextAppearance);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpSongsAdapter() {
        adapter = new AlbumSongAdapter(this, getAlbum().songs, R.layout.item_list, false, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() == 0) finish();
            }
        });
    }

    private void reload() {
        getSupportLoaderManager().restartLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final List<Song> songs = adapter.getDataSet();
        switch (id) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(getSupportFragmentManager(), "DELETE_SONGS");
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_EDITOR_REQUEST) {
            reload();
            setResult(RESULT_OK);
        }
    }

    @NonNull
    @Override
    public MaterialCab openCab(int menuRes, @NonNull final MaterialCab.Callback callback) {
        if (cab != null && cab.isActive()) cab.finish();
        adapter.setColor(getPaletteColor());
        cab = new MaterialCab(this, R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(MusicColorUtil.shiftBackgroundColorForLightText(getPaletteColor()))
                .start(new MaterialCab.Callback() {
                    @Override
                    public boolean onCabCreated(MaterialCab materialCab, Menu menu) {
                        return callback.onCabCreated(materialCab, menu);
                    }

                    @Override
                    public boolean onCabItemClicked(MenuItem menuItem) {
                        return callback.onCabItemClicked(menuItem);
                    }

                    @Override
                    public boolean onCabFinished(MaterialCab materialCab) {
                        return callback.onCabFinished(materialCab);
                    }
                });
        return cab;
    }

    @Override
    public void onBackPressed() {
        if (cab != null && cab.isActive()) cab.finish();
        else {
            recyclerView.stopScroll();
            super.onBackPressed();
        }
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        reload();
    }

    @Override
    public void setStatusBarColor(int color) {
        super.setStatusBarColor(color);
        setLightStatusbar(false);
    }

    private void setAlbum(Album album) {
        this.album = album;
        loadAlbumCover();

        getSupportActionBar().setTitle(album.getTitle());
        artistTextView.setText(album.getArtistName());
        songCountTextView.setText(MusicUtil.getSongCountString(this, album.getSongCount()));
        durationTextView.setText(MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(this, album.songs)));
        albumYearTextView.setText(MusicUtil.getYearString(album.getYear()));

        adapter.swapDataSet(album.songs);
    }

    private Album getAlbum() {
        if (album == null) album = new Album();
        return album;
    }

    @Override
    public Loader<Album> onCreateLoader(int id, Bundle args) {
        return new AsyncAlbumLoader(this, args.getLong(EXTRA_ALBUM_ID));
    }

    @Override
    public void onLoadFinished(Loader<Album> loader, Album data) {
        setAlbum(data);
    }

    @Override
    public void onLoaderReset(Loader<Album> loader) {
        this.album = new Album();
        adapter.swapDataSet(album.songs);
    }

    private static class AsyncAlbumLoader extends WrappedAsyncTaskLoader<Album> {
        private final long albumId;

        public AsyncAlbumLoader(Context context, long albumId) {
            super(context);
            this.albumId = albumId;
        }

        @Override
        public Album loadInBackground() {
            return AlbumLoader.getAlbum(getContext(), albumId);
        }
    }
}
