// Generated by view binder compiler. Do not edit!
package csmp.part_b.p5.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import csmp.part_b.p5.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogFileDetailsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView bitrate;

  @NonNull
  public final TextView fileFormat;

  @NonNull
  public final TextView fileName;

  @NonNull
  public final TextView filePath;

  @NonNull
  public final TextView fileSize;

  @NonNull
  public final TextView samplingRate;

  @NonNull
  public final TextView songLength;

  private DialogFileDetailsBinding(@NonNull LinearLayout rootView, @NonNull TextView bitrate,
      @NonNull TextView fileFormat, @NonNull TextView fileName, @NonNull TextView filePath,
      @NonNull TextView fileSize, @NonNull TextView samplingRate, @NonNull TextView songLength) {
    this.rootView = rootView;
    this.bitrate = bitrate;
    this.fileFormat = fileFormat;
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileSize = fileSize;
    this.samplingRate = samplingRate;
    this.songLength = songLength;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogFileDetailsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogFileDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_file_details, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogFileDetailsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bitrate;
      TextView bitrate = rootView.findViewById(id);
      if (bitrate == null) {
        break missingId;
      }

      id = R.id.file_format;
      TextView fileFormat = rootView.findViewById(id);
      if (fileFormat == null) {
        break missingId;
      }

      id = R.id.file_name;
      TextView fileName = rootView.findViewById(id);
      if (fileName == null) {
        break missingId;
      }

      id = R.id.file_path;
      TextView filePath = rootView.findViewById(id);
      if (filePath == null) {
        break missingId;
      }

      id = R.id.file_size;
      TextView fileSize = rootView.findViewById(id);
      if (fileSize == null) {
        break missingId;
      }

      id = R.id.sampling_rate;
      TextView samplingRate = rootView.findViewById(id);
      if (samplingRate == null) {
        break missingId;
      }

      id = R.id.song_length;
      TextView songLength = rootView.findViewById(id);
      if (songLength == null) {
        break missingId;
      }

      return new DialogFileDetailsBinding((LinearLayout) rootView, bitrate, fileFormat, fileName,
          filePath, fileSize, samplingRate, songLength);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
