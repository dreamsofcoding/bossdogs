package io.bossdogs.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_images")
data class DogImageEntity(
    @PrimaryKey
    val url: String,
    val localPath: String,
    val breed: String,
    val cachedAt: Long = System.currentTimeMillis()
)