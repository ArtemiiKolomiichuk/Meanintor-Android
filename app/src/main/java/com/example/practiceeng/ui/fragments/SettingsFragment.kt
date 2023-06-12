package com.example.practiceeng.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.practiceeng.IEManager
import com.example.practiceeng.R
import com.example.practiceeng.database.WordRepository

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val exportCards: Preference? = findPreference(getString(R.string.export_cards_key));
        val cleanDatabase: Preference? = findPreference(getString(R.string.clean_unused_words));
        val enableNotification: Preference? = findPreference(getString(R.string.send_notifications_key));
        val setNotificationTime: Preference? = findPreference(getString(R.string.notifications_time_key));

        if (cleanDatabase != null) {
            cleanDatabase.setOnPreferenceClickListener { WordRepository.get().deleteUnusedWords()
                true}
        }

    }

}
