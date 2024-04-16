package com.massimoregoli.roomdemo.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repository(private val dao: DaoProverb) {
    fun readNext(favorite: Int): Proverb? {
        return dao.loadRandomProverb(favorite)
    }

    fun readFilteredNext(filter: String, favorite: Int): Proverb? {
        return dao.readFilteredNext(filter, favorite)
    }

    fun update(proverb: Proverb) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.update(proverb)
        }
    }
}