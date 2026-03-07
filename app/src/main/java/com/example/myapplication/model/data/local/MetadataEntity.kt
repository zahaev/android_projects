package com.example.myapplication.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
data class MetadataEntity(
    @PrimaryKey val id: Int = 1,
    val count: Int,
    val pages: Int
)