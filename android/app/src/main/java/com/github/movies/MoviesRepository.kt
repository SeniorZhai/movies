/*
 * MIT License
 *
 * Copyright (c) 2018 Pablo Pallocchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 */
package com.github.movies

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MoviesRepository(val context: Context) {

    private var titleWeight = 10.0
    private var overviewWeight = 1.0

    private var helper: DBHelper? = null

    private fun getDatabase(): SQLiteDatabase {

        if (helper == null) {
            helper = DBHelper(context)
        }

        return requireNotNull(helper?.readableDatabase) { "Database not readable!" }
    }

    /**
     * Get the top n movies from the repository.
     */
    fun top(limit: Int = 100): List<Movie> {
        insert()
        val movies = mutableListOf<Movie>()
        val cursor = getDatabase().rawQuery("SELECT title, overview, poster, year FROM movies LIMIT $limit", null)
        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                        title = cursor.getString(0),
                        overview = cursor.getString(1),
                        poster = cursor.getString(2),
                        year = cursor.getInt(3)
                )

                movies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movies
    }

    private fun insert(){
        helper?.writableDatabase?.use {
            val values = ContentValues().apply {
                put("title", randomStringByJavaRandom().apply {
                    Log.e("insert Movies", this)
                })
                put("overview", randomStringByJavaRandom())
                put("poster", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.canva.cn%2Fcreate%2Fposters-china-only%2F&psig=AOvVaw2hNHqC51wS-02_LWB3fK-S&ust=1676532776184000&source=images&cd=vfe&ved=0CA8QjRxqFwoTCKjUibaBl_0CFQAAAAAdAAAAABAE")
                put("year", 1998)
            }
            it.insert("movies", null, values)
        }
    }

    /**
     * Get the movies that match a given text.
     */
    fun search(text: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = getDatabase().rawQuery("SELECT title, overview, poster, year FROM movies WHERE movies MATCH '$text*'", null)
        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                        title = cursor.getString(0),
                        overview = cursor.getString(1),
                        poster = cursor.getString(2),
                        year = cursor.getInt(3))
                movies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movies
    }

}