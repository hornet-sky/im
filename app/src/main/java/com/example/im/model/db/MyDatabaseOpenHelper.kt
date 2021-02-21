package com.example.im.model.db

import android.database.sqlite.SQLiteDatabase
import com.example.im.config.MyApplication
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper private constructor(): ManagedSQLiteOpenHelper(MyApplication.instance, DB_NAME, null, DB_VERSION) {
    init {
        instance = this
    }
    companion object {
        const val DB_NAME = "im.db"
        const val DB_VERSION = 1
        private var instance: MyDatabaseOpenHelper? = null
        @Synchronized fun getInstance() = instance ?: MyDatabaseOpenHelper()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(MyTables.ContactsTable.TABLE_NAME, true,
            MyTables.ContactsTable.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            MyTables.ContactsTable.COLUMN_ACCOUNT to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(MyTables.ContactsTable.TABLE_NAME, true)
        onCreate(db)
    }
}