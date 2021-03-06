package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.adapters.NewsAdapterWithPagination
import com.dboy.newsmvvm.adapters.NewsLoadStateAdapter
import com.dboy.newsmvvm.databinding.FragmentSearchNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//funcionou sem o @AndroidEntryPoint aqui. É necessário acrescentar essa anotação?
@AndroidEntryPoint
class SearchNewsFragment : Fragment() {
    private var binding: FragmentSearchNewsBinding? = null
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapterWithPagination

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

        var job: Job? = null
        binding?.tieSearch?.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                it?.let {
                    newsViewModel.searchNews(it.toString())
                }
            }
        }
        //O código acima foi escrito para a função search ter um delay antes de procurar. Isso evita com que uma nova procura seja
        // iniciada a cada letra escrita ou apagada. O job foi utilizado e ele facilitou a escrita do código.

        newsViewModel.searchedNewsWithPagination.observe(viewLifecycleOwner) {
            newsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        newsAdapter.setOnItemArticleClickListener {
            val action =
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleNewsFragment(it)
            findNavController().navigate(action)
        }

        newsAdapter.addLoadStateListener {
            binding?.apply {
                val sourceRefresh = it.source.refresh
                pgSearchNews.visibility = if(sourceRefresh is LoadState.Loading) View.VISIBLE else View.GONE
                rvSearchNews.visibility = if(sourceRefresh !is LoadState.NotLoading) View.INVISIBLE else View.VISIBLE
                btnRetry.visibility = if(sourceRefresh is LoadState.Error) View.VISIBLE else View.GONE
                tvError.visibility = if(sourceRefresh is LoadState.Error) View.VISIBLE else View.GONE
                Log.i("SearchNewsFragment", "CombinedLoadStates.source.refresh: ${sourceRefresh}")
                ivEmptySearch.visibility =  if (sourceRefresh is LoadState.NotLoading && newsAdapter.itemCount < 1) View.VISIBLE else View.GONE
                tvEmptySearch.visibility = if (sourceRefresh is LoadState.NotLoading && newsAdapter.itemCount < 1) View.VISIBLE else View.GONE

                ivEmptySearch.setImageResource(if (tieSearch.editableText.isEmpty()) R.drawable.search_background else R.drawable.noresultsfound)
                tvEmptySearch.text = if (tieSearch.editableText.isEmpty()) getString(R.string.searchSomething) else getString(R.string.noResultsFound)

                tvError.text = if (sourceRefresh is LoadState.Error && sourceRefresh.error.message.equals("HTTP 429 "))
                    getString(R.string.tooManyRequests)
                else getString(R.string.resultsCouldNotBeLoaded)
            }
        }

        binding?.btnRetry?.setOnClickListener {
            newsAdapter.retry()
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterWithPagination()
        binding?.rvSearchNews?.apply {
            adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter(newsAdapter::retry),
                footer = NewsLoadStateAdapter(newsAdapter::retry)
            )
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null  //the old data flashes quickly on the screen after new research. Setting this property to null gets rid of this behavior.
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}