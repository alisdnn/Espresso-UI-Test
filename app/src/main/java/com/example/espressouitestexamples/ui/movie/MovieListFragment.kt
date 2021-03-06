package com.example.espressouitestexamples.ui.movie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.espressouitestexamples.R
import com.example.espressouitestexamples.data.FakeMovieData.FAKE_NETWORK_DELAY
import com.example.espressouitestexamples.data.Movie
import com.example.espressouitestexamples.data.source.MoviesDataSource
import com.example.espressouitestexamples.ui.UICommunicationListener
import com.example.espressouitestingexamples.util.EspressoIdlingResource
import com.example.espressouitestexamples.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.lang.ClassCastException

class MovieListFragment(
    val moviesDataSource: MoviesDataSource
) : Fragment(),
    MoviesListAdapter.Interaction
{
    private val TAG: String = "AppDebug"

    override fun onItemSelected(position: Int, item: Movie) {
        activity?.run {
            val bundle = Bundle()
            bundle.putInt("movie_id", item.id)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MovieDetailFragment::class.java, bundle)
                .addToBackStack("MovieDetailFragment")
                .commit()
        }
    }

    lateinit var listAdapter: MoviesListAdapter
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        getData()
    }

    private fun getData(){
        EspressoIdlingResource.increment()
        uiCommunicationListener.loading(true)
        val job = GlobalScope.launch(IO) {
            delay(FAKE_NETWORK_DELAY)
        }
        job.invokeOnCompletion{
            GlobalScope.launch(Main){
                EspressoIdlingResource.decrement()
                uiCommunicationListener.loading(false)
                listAdapter.submitList(moviesDataSource.getMovies())
            }
        }
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            removeItemDecoration(TopSpacingItemDecoration(30))
            addItemDecoration(TopSpacingItemDecoration(30))
            listAdapter = MoviesListAdapter(this@MovieListFragment)
            adapter = listAdapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch (e: ClassCastException){
            Log.e(TAG, "Must implement interface in $activity: ${e.message}")
        }
    }
}




















