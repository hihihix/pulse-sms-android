/*
 * Copyright (C) 2017 Luke Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.klinker.messenger.shared.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color

import xyz.klinker.messenger.shared.data.model.Blacklist
import xyz.klinker.messenger.shared.data.model.Contact
import xyz.klinker.messenger.shared.data.model.Conversation
import xyz.klinker.messenger.shared.data.model.Draft
import xyz.klinker.messenger.shared.data.model.Message
import xyz.klinker.messenger.shared.data.model.ScheduledMessage
import xyz.klinker.messenger.encryption.EncryptionUtils

/**
 * Handles creating and updating databases.
 */
class DatabaseSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val tables = arrayOf(Contact(), Conversation(), Message(), Draft(), ScheduledMessage(), Blacklist())

    override fun onCreate(db: SQLiteDatabase) {
        for (table in tables) {
            db.execSQL(table.getCreateStatement())

            for (index in table.getIndexStatements()) {
                db.execSQL(index)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        // upgrade the database depending on version changes
        // for example, if old version was 1, then we need to execute all changes from 2 through the
        // newest version

        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE conversation ADD COLUMN archive integer not null DEFAULT 0")
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 3) {
            try {
                db.execSQL(Contact().getCreateStatement())
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE conversation ADD COLUMN private_notifications integer not null DEFAULT 0")
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE conversation ADD COLUMN led_color integer not null DEFAULT " + Color.WHITE) // white default
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 6) {
            try {
                db.execSQL("ALTER TABLE conversation ADD COLUMN sim_subscription_id integer DEFAULT -1")
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE message ADD COLUMN sim_phone_number text")
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 9) {
            try {
                db.execSQL("ALTER TABLE contact ADD COLUMN id_matcher text")
            } catch (e: Exception) {
            }

        }

        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE message ADD COLUMN sent_device text")
            } catch (e: Exception) {
            }

        }
    }

    fun onDrop(db: SQLiteDatabase) {
        for (table in tables) {
            db.execSQL("drop table if exists " + table.getTableName())
        }
    }

    companion object {

        private val DATABASE_NAME = "messenger.db"
        private val DATABASE_VERSION = 10
    }

}