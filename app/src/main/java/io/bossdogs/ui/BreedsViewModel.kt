package io.bossdogs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bossdogs.DogRepository
import io.bossdogs.model.ApiResult
import io.bossdogs.model.DogBreed
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedsViewModel @Inject constructor(
    private val repository: DogRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState<List<DogBreed>>>(UiState.Loading)
    val uiState: LiveData<UiState<List<DogBreed>>> = _uiState

    private val _allBreeds = MutableLiveData<List<DogBreed>>(emptyList())

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    val filteredBreeds: LiveData<List<DogBreed>> = MediatorLiveData<List<DogBreed>>().apply {
        var currentList = emptyList<DogBreed>()
        fun doFilter(list: List<DogBreed>, query: String): List<DogBreed> {
            if (query.isBlank()) return list
            return list.filter { breed ->
                breed.displayName.contains(query, ignoreCase = true) ||
                        breed.subBreeds.any { it.contains(query, ignoreCase = true) }
            }
        }
        addSource(_allBreeds) { list ->
            currentList = list
            value = doFilter(list, _searchQuery.value.orEmpty())
        }
        addSource(_searchQuery) { q ->
            value = doFilter(currentList, q)
        }
    }

    private val _breedImages = MutableLiveData<Map<String, String>>(emptyMap())
    val breedImages: LiveData<Map<String, String>> = _breedImages


    init {
        loadBreeds()
    }

    /** Fetch the list & update UI state */
    fun loadBreeds() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getBreeds()) {
                is ApiResult.Success -> {
                    // unwrap the list
                    _uiState.value = UiState.Success(result.data)
                }

                is ApiResult.Error -> {
                    _uiState.value = UiState.Error(result.exception)
                }
            }
        }
    }

    /** Retry helper */
    fun retry() {
        loadBreeds()
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
}