package com.massimoregoli.roomdemo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Proverb(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var text: String,
    var lang: String,
    var category: Int,
    var favorite: Int,) {

    constructor(text: String) : this(0,"","it",0,0) {      //costruttore che permettera di passare provverbio a partire da stringa
        this.text = text                                                                //tuttavia lo crea solo in italiano e non gestisce categoria e id
    }
}