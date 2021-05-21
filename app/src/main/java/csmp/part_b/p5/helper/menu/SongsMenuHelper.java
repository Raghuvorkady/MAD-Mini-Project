package csmp.part_b.p5.helper.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import csmp.part_b.p5.R;
import csmp.part_b.p5.dialogs.DeleteSongsDialog;
import csmp.part_b.p5.helper.MusicPlayerRemote;
import csmp.part_b.p5.model.Song;

public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull List<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                return true;
        }
        return false;
    }
}
