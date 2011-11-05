package org.ov.android.smarthud;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class PrefsActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.prefs);   
  }      
}

  
  
  
  


