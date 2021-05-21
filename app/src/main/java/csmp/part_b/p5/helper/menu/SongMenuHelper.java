package csmp.part_b.p5.helper.menu;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import csmp.part_b.p5.R;
import csmp.part_b.p5.dialogs.DeleteSongsDialog;
import csmp.part_b.p5.dialogs.SongDetailDialog;
import csmp.part_b.p5.model.Song;
import csmp.part_b.p5.util.MusicUtil;

public class SongMenuHelper {
    public static final int MENU_RES = R.menu.menu_item_song;

    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull Song song, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_share:
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song, activity), null));
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(song).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                return true;
            case R.id.action_details:
                SongDetailDialog.create(song).show(activity.getSupportFragmentManager(), "SONG_DETAILS");
                return true;
        }
        return false;
    }

    public static abstract class OnClickSongMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private AppCompatActivity activity;

        public OnClickSongMenu(@NonNull AppCompatActivity activity) {
            this.activity = activity;
        }

        public int getMenuRes() {
            return MENU_RES;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.inflate(getMenuRes());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, getSong(), item.getItemId());
        }

        public abstract Song getSong();
    }
}
