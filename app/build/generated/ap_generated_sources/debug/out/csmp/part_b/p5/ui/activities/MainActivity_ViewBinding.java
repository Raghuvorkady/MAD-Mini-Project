// Generated code from Butter Knife. Do not modify!
package csmp.part_b.p5.ui.activities;

import android.view.View;
import androidx.annotation.UiThread;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.internal.Utils;
import com.google.android.material.navigation.NavigationView;
import csmp.part_b.p5.R;
import csmp.part_b.p5.ui.activities.base.AbsSlidingMusicPanelActivity_ViewBinding;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding extends AbsSlidingMusicPanelActivity_ViewBinding {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    super(target, source);

    this.target = target;

    target.navigationView = Utils.findRequiredViewAsType(source, R.id.navigation_view, "field 'navigationView'", NavigationView.class);
    target.drawerLayout = Utils.findRequiredViewAsType(source, R.id.drawer_layout, "field 'drawerLayout'", DrawerLayout.class);
  }

  @Override
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.navigationView = null;
    target.drawerLayout = null;

    super.unbind();
  }
}
