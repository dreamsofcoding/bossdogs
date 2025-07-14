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
import timber.log.Timber
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

    private val _breedImages = MutableLiveData<Map<String, String>>(emptyMap())
    val breedImages: LiveData<Map<String, String>> = _breedImages

    init {
        loadBreeds()
    }

    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    val filteredBreeds: LiveData<List<DogBreed>> = MediatorLiveData<List<DogBreed>>().apply {
        var currentList = emptyList<DogBreed>()
        fun doFilter(list: List<DogBreed>, query: String) = if (query.isBlank()) {
            list
        } else {
            list.filter { b ->
                b.displayName.contains(query, true) ||
                        b.subBreeds.any { it.contains(query, true) }
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

    fun loadBreeds() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getBreeds()) {
                is ApiResult.Success -> {
                    _allBreeds.value = result.data
                    _uiState.value = UiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = UiState.Error(result.exception)
                }
            }
        }
    }

    fun retry() = loadBreeds()

    fun loadBreedImage(breedName: String) {
        if (_breedImages.value?.containsKey(breedName) == true) return

        viewModelScope.launch {
            Timber.d("Loading image for breed: $breedName")
            when (val result = repository.getBreedImages(breedName, 1)) {
                is ApiResult.Success -> {
                    val images = result.data
                    if (images.isNotEmpty()) {
                        val imageUrl = images.random().url
                        _breedImages.value = _breedImages.value?.plus((breedName to imageUrl))
                        Timber.d("Successfully loaded image for $breedName")
                    }
                }

                is ApiResult.Error -> {
                    Timber.e(result.exception, "Failed to load image for breed: $breedName")
                }
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
}