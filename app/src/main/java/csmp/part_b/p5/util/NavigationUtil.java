package csmp.part_b.p5.util;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import csmp.part_b.p5.ui.activities.AlbumDetailActivity;

public class NavigationUtil {
    public static void goToAlbum(@NonNull final Activity activity, final long albumId, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_ID, albumId);

        //noinspection unchecked
        if (sharedElements != null && sharedElements.length > 0) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }
}
