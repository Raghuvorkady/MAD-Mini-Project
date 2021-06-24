// Generated by view binder compiler. Do not edit!
package csmp.part_b.p5.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import csmp.part_b.p5.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ShadowStatusbarToolbarBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final LinearLayout dummyStatusbarActionbar;

  private ShadowStatusbarToolbarBinding(@NonNull RelativeLayout rootView,
      @NonNull LinearLayout dummyStatusbarActionbar) {
    this.rootView = rootView;
    this.dummyStatusbarActionbar = dummyStatusbarActionbar;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ShadowStatusbarToolbarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ShadowStatusbarToolbarBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.shadow_statusbar_toolbar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ShadowStatusbarToolbarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.dummy_statusbar_actionbar;
      LinearLayout dummyStatusbarActionbar = rootView.findViewById(id);
      if (dummyStatusbarActionbar == null) {
        break missingId;
      }

      return new ShadowStatusbarToolbarBinding((RelativeLayout) rootView, dummyStatusbarActionbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
