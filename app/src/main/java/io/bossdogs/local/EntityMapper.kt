package io.bossdogs.local

import io.bossdogs.model.DogBreed
import io.bossdogs.model.DogImage

object EntityMapper {

    fun DogBreed.toEntity(): DogBreedEntity {
        return DogBreedEntity(
            name = name,
            subBreeds = subBreeds,
            cachedAt = System.currentTimeMillis()
        )
    }

    fun DogBreedEntity.toDomain(): DogBreed {
        return DogBreed(
            name = name,
            subBreeds = subBreeds
        )
    }

    fun List<DogBreedEntity>.toDomainList(): List<DogBreed> {
        return map { it.toDomain() }
    }

    fun List<DogBreed>.toEntityList(): List<DogBreedEntity> {
        return map { it.toEntity() }
    }

    fun DogImage.toEntity(): DogImageEntity {
        return DogImageEntity(
            url = url,
            localPath = localPath,
            breed = breed,
            cachedAt = System.currentTimeMillis()
        )
    }

    fun DogImageEntity.toDomain(): DogImage {
        return DogImage(
            url = url,
            localPath = localPath,
            breed = breed
        )
    }

    fun List<DogImageEntity>.toDomainImageList(): List<DogImage> {
        return map { it.toDomain() }
    }

    fun List<DogImage>.toImageEntityList(): List<DogImageEntity> {
        return map { it.toEntity() }
    }
}