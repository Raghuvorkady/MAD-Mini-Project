// Generated code from Butter Knife. Do not modify!
package csmp.part_b.p5.ui.activities.base;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import csmp.part_b.p5.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AbsSlidingMusicPanelActivity_ViewBinding implements Unbinder {
  private AbsSlidingMusicPanelActivity target;

  @UiThread
  public AbsSlidingMusicPanelActivity_ViewBinding(AbsSlidingMusicPanelActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AbsSlidingMusicPanelActivity_ViewBinding(AbsSlidingMusicPanelActivity target,
      View source) {
    this.target = target;

    target.slidingUpPanelLayout = Utils.findRequiredViewAsType(source, R.id.sliding_layout, "field 'slidingUpPanelLayout'", SlidingUpPanelLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AbsSlidingMusicPanelActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.slidingUpPanelLayout = null;
  }
}
