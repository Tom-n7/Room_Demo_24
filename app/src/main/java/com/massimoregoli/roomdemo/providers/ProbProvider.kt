package com.massimoregoli.roomdemo.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.massimoregoli.roomdemo.db.DaoProverb
import com.massimoregoli.roomdemo.db.DbProverb

const val PROVIDER = "com.massimoregoli.roomdemo"
const val PROVERBS_ITEM = "proverb/#"
const val PROVERBS = "proverb"

const val PROVERBS_CODE = 10
const val PROVERBS_ITEM_CODE = 11
class ProvProvider : ContentProvider() {

    private lateinit var database: DbProverb
    private lateinit var domainDao: DaoProverb

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(PROVIDER, PROVERBS, PROVERBS_CODE)
        addURI(PROVIDER, PROVERBS_ITEM, PROVERBS_ITEM_CODE)
    }

    override fun onCreate(): Boolean {
        database = DbProverb.getInstance(context!!)
        domainDao = database.proverbDao()
        return true
    }

    override fun query(
        uri: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        return if (p2 == null)
            domainDao.selectAll()
        else
            if (p3?.size!! > 0)
                domainDao.selectById(p3[0].toInt())
            else
                null
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            PROVERBS_CODE -> "vnd.android.cursor.dir/$PROVIDER/proverb"
            PROVERBS_ITEM_CODE -> "vnd.android.cursor.item/$PROVIDER/proverb"
            else -> null
        }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }
}