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

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MoviesActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val moviesRepository: MoviesRepository = MoviesRepository(context = this)

    private var moviesRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)
        initToolbar()
        initRecyclerView()
        loadTopMovies()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun initRecyclerView() {
        moviesRecyclerView = findViewById(R.id.movies_recycler_view)
        moviesRecyclerView?.setHasFixedSize(true)
        moviesRecyclerView?.layoutManager = LinearLayoutManager(this)
    }

    private fun loadTopMovies() {
        val movies = moviesRepository.top()
        moviesRecyclerView?.adapter = MoviesAdapter(movies)
    }

    override fun onQueryTextChange(text: String?): Boolean {
        val movies = if (text.isNullOrBlank()) {
            // Should not be in the main thread but it's just for educational purposes
            moviesRepository.top()
        } else {
            // Should not be in the main thread but it's just for educational purposes
            moviesRepository.search(text = text!!)
        }
        moviesRecyclerView?.adapter = MoviesAdapter(movies)
        return true
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        // Nothing to do since search is performed in real time while user is typing
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_movies, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_view_hint)
        searchView.setOnQueryTextListener(this)
        return true
    }

}
