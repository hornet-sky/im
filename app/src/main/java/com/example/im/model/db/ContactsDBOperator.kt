package com.example.im.model.db

import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

object ContactsDBOperator {
    fun save(contact: Contact) {
        MyDatabaseOpenHelper.getInstance().use {
            insert(MyTables.ContactsTable.TABLE_NAME, *contact.toPairArray())
        }
    }
    fun deleteAll() {
        MyDatabaseOpenHelper.getInstance().use {
            delete(MyTables.ContactsTable.TABLE_NAME)
        }
    }
    fun listAll(): List<Contact> {
        return MyDatabaseOpenHelper.getInstance().use {
            select(MyTables.ContactsTable.TABLE_NAME).parseList(object: MapRowParser<Contact> {
                override fun parseRow(columns: Map<String, Any?>) = Contact(columns.toMutableMap())
            })
        }
    }
}