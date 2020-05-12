package com.dror.feedapp.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.dror.feedapp.R
import com.dror.feedapp.model.FeedMessage
import com.dror.feedapp.viewmodel.APIViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_layout.*


class MainActivity : AppCompatActivity() {
    private val viewModel: APIViewModel by viewModels()
    private var adapter: FeedMessagesAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setSearchFilter(newText)
                return false
            }
        })

        startButton.setOnClickListener {
            viewModel.start()
        }
        stopButton.setOnClickListener {
            viewModel.stop()
        }


        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.feedMessage.observe(this, Observer {
            it?.let { message ->
                updateFeed(message)
            }
        })

        viewModel.feedError.observe(this, Observer {
            it?.let { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.filter.observe(this, Observer {
            adapter?.filter?.filter(it)
        })
    }

    private fun updateFeed(feedMessage: FeedMessage) {
        if(adapter == null) {
            adapter = FeedMessagesAdapter()
            recyclerView.adapter = adapter
        }

        adapter?.update(feedMessage)
    }
}
