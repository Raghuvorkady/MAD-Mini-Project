package csmp.part_b.p5.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.kabouzeid.appthemehelper.util.TintHelper;

import java.io.InputStream;

public class ImageUtil {

    public static Bitmap createBitmap(Drawable drawable, float sizeMultiplier) {
        Bitmap bitmap = Bitmap.createBitmap((int) (drawable.getIntrinsicWidth() * sizeMultiplier), (int) (drawable.getIntrinsicHeight() * sizeMultiplier), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return bitmap;
    }

    public static Drawable getVectorDrawable(@NonNull Resources res, @DrawableRes int resId, @Nullable Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            return res.getDrawable(resId, theme);
        }
        return VectorDrawableCompat.create(res, resId, theme);
    }

    public static Drawable getTintedVectorDrawable(@NonNull Context context, @DrawableRes int id, @ColorInt int color) {
        return TintHelper.createTintedDrawable(getVectorDrawable(context.getResources(), id, context.getTheme()), color);
    }

    public static Bitmap resize(InputStream stream, int scaledWidth, int scaledHeight) {
        final Bitmap bitmap = BitmapFactory.decodeStream(stream);
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);

    }
}
