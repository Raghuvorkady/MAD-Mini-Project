// Generated code from Butter Knife. Do not modify!
package csmp.part_b.p5.ui.fragments.player.card;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import csmp.part_b.p5.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CardPlayerFragment_ViewBinding implements Unbinder {
  private CardPlayerFragment target;

  @UiThread
  public CardPlayerFragment_ViewBinding(CardPlayerFragment target, View source) {
    this.target = target;

    target.toolbarContainer = Utils.findOptionalViewAsType(source, R.id.toolbar_container, "field 'toolbarContainer'", FrameLayout.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.player_toolbar, "field 'toolbar'", Toolbar.class);
    target.slidingUpPanelLayout = Utils.findRequiredViewAsType(source, R.id.player_sliding_layout, "field 'slidingUpPanelLayout'", SlidingUpPanelLayout.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.player_recycler_view, "field 'recyclerView'", RecyclerView.class);
    target.playingQueueCard = Utils.findRequiredViewAsType(source, R.id.playing_queue_card, "field 'playingQueueCard'", CardView.class);
    target.colorBackground = Utils.findRequiredView(source, R.id.color_background, "field 'colorBackground'");
    target.playerQueueSubHeader = Utils.findRequiredViewAsType(source, R.id.player_queue_sub_header, "field 'playerQueueSubHeader'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CardPlayerFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbarContainer = null;
    target.toolbar = null;
    target.slidingUpPanelLayout = null;
    target.recyclerView = null;
    target.playingQueueCard = null;
    target.colorBackground = null;
    target.playerQueueSubHeader = null;
  }
}
