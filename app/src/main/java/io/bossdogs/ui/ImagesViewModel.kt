package io.bossdogs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bossdogs.DogRepository
import io.bossdogs.model.ApiResult
import io.bossdogs.model.DogImage
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val repository: DogRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<DogImage>>>(UiState.Loading)
    val uiState: LiveData<UiState<List<DogImage>>> = _uiState

    private val _breedDisplayName = MutableLiveData("")
    val breedDisplayName: LiveData<String> = _breedDisplayName

    private var allImages: List<DogImage> = emptyList()

    private var _selectedHeroImage = MutableLiveData<DogImage?>(null)
    var selectedHeroImage: LiveData<DogImage?> = _selectedHeroImage

    fun loadImages(breedName: String) {
        _breedDisplayName.value = breedName.replaceFirstChar { it.uppercase() }
        Timber.d("Loading images for breed: $breedName")
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            when (val result = repository.getBreedImages(breedName)) {
                is ApiResult.Success -> {
                    allImages = result.data
                    selectHero(result.data.random())
                    Timber.d("Successfully loaded ${result.data.size} images for breed: $breedName")
                    _uiState.value = UiState.Success(getRandomImages())
                }

                is ApiResult.Error -> {
//                    val errorMessage = mapToUiError(result.exception)
                    Timber.e(result.exception, "Failed to load images for breed: $breedName")
//                    _uiState.value = UiState.Error(errorMessage, result.exception)
                }
            }
        }
    }

    fun refreshImages() {
        _uiState.value = UiState.Loading
        Timber.d("Refreshing images for breed: ${_breedDisplayName.value}")
        if (allImages.isNotEmpty()) {
            _uiState.value = UiState.Success(getRandomImages())
        } else {
            _breedDisplayName.value?.let { loadImages(it) }
        }
    }

    private fun getRandomImages(): List<DogImage> {
        return if (allImages.size <= 10) {
            allImages
        } else {
            allImages.shuffled().take(10)
        }
    }

    fun selectHero(image: DogImage) {
        _selectedHeroImage.value = image
    }

}