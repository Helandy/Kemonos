package su.afk.kemonos.storage.database.converter

import androidx.room.TypeConverter
import su.afk.kemonos.domain.SelectedSite

internal class SelectedSiteConverters {
    @TypeConverter
    fun toDb(value: SelectedSite): String = value.name

    @TypeConverter
    fun fromDb(value: String): SelectedSite = SelectedSite.valueOf(value)
}