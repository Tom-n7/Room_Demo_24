package com.massimoregoli.roomdemo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Proverb::class, Category::class], version = 1)
abstract class DbProverb : RoomDatabase() {
    abstract fun proverbDao(): DaoProverb

    companion object {
        private var db: DbProverb? = null

        fun getInstance(context: Context): DbProverb {
            if (db == null) {
                db = databaseBuilder(
                    context,
                    DbProverb::class.java,
                    "proverbs.db"
                )
                    .createFromAsset("proverbs.db")
                    .build()
            }
            return db as DbProverb
        }
    }
}