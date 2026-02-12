package su.afk.kemonos.creators.presenter.delegates

import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.State
import su.afk.kemonos.domain.models.creator.Creators.Companion.toFavoriteArtistUi
import javax.inject.Inject

internal class RandomListDelegate @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
) {
    /** Подгрузка рандомных авторов */
    suspend fun loadRandom(
        state: State,
        setState: (State.() -> State) -> Unit,
    ) {
        val suggestEnabled = state.uiSettingModel.suggestRandomAuthors

        if (!suggestEnabled) {
            setState {
                copy(
                    randomSuggestions = emptyList(),
                    randomSuggestionsFiltered = emptyList(),
                    randomSuggestionsLoading = false,
                )
            }
        } else {
            val service = state.selectedService

            setState { copy(randomSuggestionsLoading = true) }

            runCatching {
                getCreatorsPagedUseCase.getRandomCreatorsFromStorage(service = service, limit = 50)
            }.onSuccess { list ->
                val mapped = list.map { it.toFavoriteArtistUi() }

                setState {
                    copy(
                        randomSuggestions = mapped,
                        randomSuggestionsFiltered = mapped,
                        randomSuggestionsLoading = false,
                    )
                }
            }.onFailure {
                setState { copy(randomSuggestionsLoading = false) }
            }
        }
    }

    /** Локальная фильтрация */
    fun applyFilterToRandomCreators(
        query: String,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
    ) {
        val creatorsRandom = state().randomSuggestions
        val q = query.trim().lowercase()

        val filtered = if (q.isEmpty()) {
            creatorsRandom
        } else {
            creatorsRandom.filter { a ->
                a.name.lowercase().contains(q)
            }
        }

        setState { copy(randomSuggestionsFiltered = filtered) }
    }
}
