package csmp.part_b.p5;

import android.app.Application;

import com.kabouzeid.appthemehelper.ThemeStore;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.primary_color)
                    .accentColorRes(R.color.accent_color)
                    .commit();
        }

    }

    public static App getInstance() {
        return app;
    }
}
