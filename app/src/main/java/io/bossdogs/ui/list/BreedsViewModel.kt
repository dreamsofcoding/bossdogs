package io.bossdogs.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bossdogs.DogRepository
import io.bossdogs.model.ApiResult
import io.bossdogs.model.DogBreed
import io.bossdogs.ui.UiState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BreedsViewModel @Inject constructor(
    private val repository: DogRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState<List<DogBreed>>>(UiState.Companion.loading())
    val uiState: LiveData<UiState<List<DogBreed>>> = _uiState

    private val _allBreeds = MutableLiveData<List<DogBreed>>(emptyList())
    val allBreeds: LiveData<List<DogBreed>> = _allBreeds
    private val _breedImages = MutableLiveData<Map<String, String>>(emptyMap())
    val breedImages: LiveData<Map<String, String>> = _breedImages


    init {
        loadBreeds()
    }

    fun loadBreeds() {
        _uiState.value = UiState.Companion.loading()
        viewModelScope.launch {
            when (val result = repository.getBreeds()) {
                is ApiResult.Success -> {
                    _allBreeds.value = result.data
                    _uiState.value = UiState.Companion.success(result.data)
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        UiState.Companion.error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun retry() = loadBreeds()

    private val loadingBreeds = mutableSetOf<String>()

    fun loadBreedImage(breedName: String) {
        if (_breedImages.value!!.containsKey(breedName) ||
            loadingBreeds.contains(breedName)
        ) return

        loadingBreeds += breedName

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