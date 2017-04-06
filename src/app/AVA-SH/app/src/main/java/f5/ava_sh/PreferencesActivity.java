package f5.ava_sh;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 *Class:             ConnectionHelper.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           1.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:           Builds a preferencesActivity panel from the MainActivity
 *
 *
 * */


public class PreferencesActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    public static class PreferencesFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}