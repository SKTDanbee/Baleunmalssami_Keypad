package com.myhome.rpgkeyboard.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createProfaneWordsTable = """
            CREATE TABLE $TABLE_PROFANE_WORDS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createProfaneWordsTable)

        val createEmojisTable = """
            CREATE TABLE $TABLE_EMOJIS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMOTION TEXT NOT NULL,
                $COLUMN_EMOJI TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createEmojisTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFANE_WORDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMOJIS")
        onCreate(db)
    }

    fun isProfaneWord(word: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_PROFANE_WORDS,
            arrayOf(COLUMN_ID),
            "$COLUMN_WORD = ?",
            arrayOf(word),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getEmojiForEmotion(emotion: String): String? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_EMOJIS,
            arrayOf(COLUMN_EMOJI),
            "$COLUMN_EMOTION = ?",
            arrayOf(emotion),
            null,
            null,
            null
        )
        val emojis = mutableListOf<String>()
        Log.d("DatabaseHelper", "getEmojiForEmotion: cursor count = ${cursor.count}")
        while (cursor.moveToNext()) {
            emojis.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMOJI)))
        }
        cursor.close()
        return if (emojis.isNotEmpty()) emojis.random() else null
    }

    companion object {
        private const val DATABASE_NAME = "keypad.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PROFANE_WORDS = "ProfaneWords"
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"

        const val TABLE_EMOJIS = "Emojis"
        const val COLUMN_EMOTION = "emotion"
        const val COLUMN_EMOJI = "emoji"
    }

    fun insertProfaneWord(context: Context, word: String) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_WORD, word)
        }
        db.insert(DatabaseHelper.TABLE_PROFANE_WORDS, null, contentValues)
    }

    fun insertEmoji(context: Context, emotion: String, emoji: String) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        val emojiValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EMOTION, emotion)
            put(DatabaseHelper.COLUMN_EMOJI, emoji)
        }
        db.insert(DatabaseHelper.TABLE_EMOJIS, null, emojiValues)
    }

    fun loadCSVData(context: Context) {
        loadProfaneWordsFromCSV(context, "profane_words.csv")
        loadEmojisFromCSV(context, "emojis.csv")
    }

    private fun loadProfaneWordsFromCSV(context: Context, fileName: String) {
        val db = this.writableDatabase
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use {
            it.readLine() // Skip header
            var line = it.readLine()
            while (line != null) {
                val columns = line.split(",")
                if (columns.size == 2) {
                    val contentValues = ContentValues().apply {
                        put(COLUMN_WORD, columns[1])
                    }
                    db.insert(TABLE_PROFANE_WORDS, null, contentValues)
                }
                line = it.readLine()
            }
        }
    }

    private fun loadEmojisFromCSV(context: Context, fileName: String) {
        val db = this.writableDatabase
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use {
            it.readLine() // Skip header
            var line = it.readLine()
            while (line != null) {
                val columns = line.split(",")
                if (columns.size == 3) {
                    val contentValues = ContentValues().apply {
                        put(COLUMN_EMOTION, columns[1])
                        put(COLUMN_EMOJI, columns[2])
                    }
                    db.insert(TABLE_EMOJIS, null, contentValues)
                }
                line = it.readLine()
            }
        }
    }

}