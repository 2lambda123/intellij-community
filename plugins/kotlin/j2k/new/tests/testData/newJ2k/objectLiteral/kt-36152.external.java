// Stubbed class from 'androidx.preference:preference:1.1.0'
public class Preference {
    private OnPreferenceClickListener l;

    public interface OnPreferenceClickListener {
        boolean onPreferenceClick(Preference preference);
    }

    @SuppressWarnings("unused")
    public OnPreferenceClickListener getOnPreferenceClickListener() {
        return null;
    }

    public void setOnPreferenceClickListener(OnPreferenceClickListener onPreferenceClickListener) {
        l = onPreferenceClickListener;
    }
}