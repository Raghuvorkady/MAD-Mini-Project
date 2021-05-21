package csmp.part_b.p5.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.kabouzeid.appthemehelper.ThemeStore;

import butterknife.BindView;
import butterknife.ButterKnife;
import csmp.part_b.p5.R;
import csmp.part_b.p5.ui.activities.base.AbsBaseActivity;
import csmp.part_b.p5.ui.activities.intro.AppIntroActivity;

@SuppressWarnings("FieldCanBeLocal")
public class AboutActivity extends AbsBaseActivity implements View.OnClickListener {

    private static String GITHUB = "https://github.com/Raghuvorkady";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.intro)
    LinearLayout intro;
    @BindView(R.id.fork_on_github)
    LinearLayout forkOnGitHub;
    @BindView(R.id.write_an_email)
    LinearLayout writeAnEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        setUpViews();
    }

    private void setUpViews() {
        setUpToolbar();
        setUpAppVersion();
        setUpOnClickListeners();
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        toolbar.setTitleTextAppearance(this, R.style.ProductSansTextAppearance);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAppVersion() {
        //appVersion.setText(R.string.app_version);
        appVersion.setText("1.0.1");
    }

    private void setUpOnClickListeners() {
        intro.setOnClickListener(this);
        forkOnGitHub.setOnClickListener(this);
        writeAnEmail.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == intro) {
            startActivity(new Intent(this, AppIntroActivity.class));
        } else if (v == forkOnGitHub) {
            openUrl(GITHUB);
        } else if (v == writeAnEmail) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:raghavendrakm300@gmail.com"));
            intent.putExtra(Intent.EXTRA_EMAIL, "raghavendrakm300@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Music");
            startActivity(Intent.createChooser(intent, "E-Mail"));
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
