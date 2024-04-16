package com.massimoregoli.roomdemo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var text: String
)