package pdm.progetto.registratore;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OpzioniActivity extends PreferenceActivity 
{
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource (R.xml.options);
    }

}

