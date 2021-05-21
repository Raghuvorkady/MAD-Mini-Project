package csmp.part_b.p5.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import csmp.part_b.p5.glide.artistimage.ArtistImage;
import csmp.part_b.p5.glide.artistimage.ArtistImageLoader;
import csmp.part_b.p5.glide.audiocover.AudioFileCover;
import csmp.part_b.p5.glide.audiocover.AudioFileCoverLoader;

public class MusicGlideModule implements GlideModule {

  	@Override
  	public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        glide.register(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory());
    }
}
