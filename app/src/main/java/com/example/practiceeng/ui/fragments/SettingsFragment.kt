package com.example.practiceeng.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.practiceeng.IEManager
import com.example.practiceeng.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val importCards: Preference? = findPreference(getString(R.string.import_cards_key));
        val importWords: Preference? = findPreference(getString(R.string.import_words_key));
        val exportCards: Preference? = findPreference(getString(R.string.export_cards_key));
        val enableNotification: Preference? = findPreference(getString(R.string.send_notifications_key));
        val setNotificationTime: Preference? = findPreference(getString(R.string.notifications_time_key));
    }

}
