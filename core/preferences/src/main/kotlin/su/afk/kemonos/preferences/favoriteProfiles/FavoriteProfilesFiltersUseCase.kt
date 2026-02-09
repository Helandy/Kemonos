package su.afk.kemonos.preferences.favoriteProfiles

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

internal class FavoriteProfilesFiltersUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : IFavoriteProfilesFiltersUseCase {

    override suspend fun read(site: SelectedSite): FavoriteProfilesFiltersPrefs =
        dataStore.data.map { prefs ->
            FavoriteProfilesFiltersPrefs(
                selectedService = prefs[selectedServiceKey(site)] ?: "Services",
                sortedTypeName = prefs[sortedTypeNameKey(site)] ?: "NewPostsDate",
                sortAscending = prefs[sortAscendingKey(site)] ?: false,
            )
        }.first()

    override suspend fun setSelectedService(site: SelectedSite, value: String) {
        dataStore.edit { it[selectedServiceKey(site)] = value }
    }

    override suspend fun setSortedTypeName(site: SelectedSite, value: String) {
        dataStore.edit { it[sortedTypeNameKey(site)] = value }
    }

    override suspend fun setSortAscending(site: SelectedSite, value: Boolean) {
        dataStore.edit { it[sortAscendingKey(site)] = value }
    }

    private fun selectedServiceKey(site: SelectedSite) =
        stringPreferencesKey("FAVORITE_PROFILES_SELECTED_SERVICE_${site.name}")

    private fun sortedTypeNameKey(site: SelectedSite) =
        stringPreferencesKey("FAVORITE_PROFILES_SORTED_TYPE_${site.name}")

    private fun sortAscendingKey(site: SelectedSite) =
        booleanPreferencesKey("FAVORITE_PROFILES_SORT_ASCENDING_${site.name}")

}
