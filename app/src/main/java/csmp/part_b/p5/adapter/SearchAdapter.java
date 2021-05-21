package csmp.part_b.p5.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kabouzeid.appthemehelper.util.ATHUtil;

import java.util.ArrayList;
import java.util.List;

import csmp.part_b.p5.R;
import csmp.part_b.p5.adapter.base.MediaEntryViewHolder;
import csmp.part_b.p5.glide.SongGlideRequest;
import csmp.part_b.p5.helper.MusicPlayerRemote;
import csmp.part_b.p5.helper.menu.SongMenuHelper;
import csmp.part_b.p5.model.Album;
import csmp.part_b.p5.model.Song;
import csmp.part_b.p5.util.MusicUtil;
import csmp.part_b.p5.util.NavigationUtil;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final int HEADER = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 3;

    private final AppCompatActivity activity;
    private List<Object> dataSet;

    public SearchAdapter(@NonNull AppCompatActivity activity, @NonNull List<Object> dataSet) {
        this.activity = activity;
        this.dataSet = dataSet;
    }

    public void swapDataSet(@NonNull List<Object> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) instanceof Album) return ALBUM;
        if (dataSet.get(position) instanceof Song) return SONG;
        return HEADER;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER)
            return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.sub_header, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_list, parent, false), viewType);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ALBUM:
                final Album album = (Album) dataSet.get(position);
                holder.title.setText(album.getTitle());
                holder.text.setText(MusicUtil.getAlbumInfoString(activity, album));
                SongGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
                        .checkIgnoreMediaStore(activity).build()
                        .into(holder.image);
                break;
            case SONG:
                final Song song = (Song) dataSet.get(position);
                holder.title.setText(song.title);
                holder.text.setText(MusicUtil.getSongInfoString(song));
                break;
            default:
                holder.title.setText(dataSet.get(position).toString());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends MediaEntryViewHolder {
        public ViewHolder(@NonNull View itemView, int itemViewType) {
            super(itemView);
            itemView.setOnLongClickListener(null);

            if (itemViewType != HEADER) {
                itemView.setBackgroundColor(ATHUtil.resolveColor(activity, R.attr.cardBackgroundColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setElevation(activity.getResources().getDimensionPixelSize(R.dimen.card_elevation));
                }
                if (shortSeparator != null) {
                    shortSeparator.setVisibility(View.GONE);
                }
            }

            if (menu != null) {
                if (itemViewType == SONG) {
                    menu.setVisibility(View.VISIBLE);
                    menu.setOnClickListener(new SongMenuHelper.OnClickSongMenu(activity) {
                        @Override
                        public Song getSong() {
                            return (Song) dataSet.get(getAdapterPosition());
                        }
                    });
                } else {
                    menu.setVisibility(View.GONE);
                }
            }

            switch (itemViewType) {
                case ALBUM:
                    setImageTransitionName(activity.getString(R.string.transition_album_art));
                    break;
                default:
                    View container = itemView.findViewById(R.id.image_container);
                    if (container != null) {
                        container.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            Object item = dataSet.get(getAdapterPosition());
            switch (getItemViewType()) {
                case ALBUM:
                    NavigationUtil.goToAlbum(activity,
                            ((Album) item).getId(),
                            Pair.create(image,
                                    activity.getResources().getString(R.string.transition_album_art)
                            ));
                    break;
                case SONG:
                    List<Song> playList = new ArrayList<>();
                    playList.add((Song) item);
                    MusicPlayerRemote.openQueue(playList, 0, true);
                    break;
            }
        }
    }
}
