package su.afk.kemonos.creatorPost.presenter.util

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.mapSaver

/** пересчет блока content */
internal val IntStateMapSaver = mapSaver(
    save = { stateMap -> stateMap.mapValues { it.value.intValue } },
    restore = { restored ->
        restored.mapValues { mutableIntStateOf(it.value as Int) }.toMutableMap()
    }
)