package csmp.part_b.p5.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.kabouzeid.appthemehelper.util.ATHUtil;

import csmp.part_b.p5.R;
import csmp.part_b.p5.glide.palette.BitmapPaletteTarget;
import csmp.part_b.p5.glide.palette.BitmapPaletteWrapper;
import csmp.part_b.p5.util.MusicColorUtil;

public abstract class MusicColoredTarget extends BitmapPaletteTarget {
    public MusicColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(MusicColorUtil.getColor(resource.getPalette(), getDefaultFooterColor()));
    }

    protected int getDefaultFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.cardBackgroundColor);
    }

    public abstract void onColorReady(int color);
}
