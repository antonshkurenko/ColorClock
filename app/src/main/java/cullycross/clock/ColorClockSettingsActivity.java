package cullycross.clock;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A settings Activity for {@link ColorClockDayDream}.
 * <p />
 * A DreamService can only be used on devices with API v17+, so it is safe
 * for us to use a {@link PreferenceFragment} here.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class ColorClockSettingsActivity
    extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {

      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, DreamPreferenceFragment.newInstance())
          .commit();
    }
  }

  public static class DreamPreferenceFragment extends PreferenceFragmentCompat {

    public static DreamPreferenceFragment newInstance() {
      return new DreamPreferenceFragment();
    }

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.color_clock_prefs, rootKey);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      final Preference preference = findPreference(getString(R.string.pref_about_key));
      preference.setSummary(getString(R.string.questions_preference_fragment_version_summary,
          BuildConfig.VERSION_NAME));
    }
  }
}
