package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.adapters.NewsAdapterWithPagination
import com.dboy.newsmvvm.adapters.NewsLoadStateAdapter
import com.dboy.newsmvvm.databinding.FragmentBreakingNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {
    private var binding: FragmentBreakingNewsBinding? = null
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapterWithPagination

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        //it's important to pass viewLifecycleOwner instead of the fragment itself, cause I want to stop updating the UI when the view is
        //destroyed and the fragment is still alive in the background.
        newsViewModel.breakingNewsWithPagination.observe(viewLifecycleOwner) {
            newsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        newsAdapter.setOnItemArticleClickListener {
            val action =
                BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleNewsFragment(
                    article = it
                )
            findNavController().navigate(action)
        }

        newsAdapter.addLoadStateListener {
            binding?.apply {
                val sourceRefresh = it.source.refresh
                rvBreakingNews.visibility = if(sourceRefresh !is LoadState.NotLoading) View.INVISIBLE else View.VISIBLE
                pgBreakingNews.visibility = if(sourceRefresh is LoadState.Loading && !swipeRefresh.isRefreshing) View.VISIBLE else View.GONE
                btnRetry.visibility = if(sourceRefresh is LoadState.Error) View.VISIBLE else View.GONE
                tvError.visibility = if(sourceRefresh is LoadState.Error) View.VISIBLE else View.GONE
                tvError.text = if (sourceRefresh is LoadState.Error && sourceRefresh.error.message.equals("HTTP 429 "))
                    getString(R.string.tooManyRequests)
                else getString(R.string.resultsCouldNotBeLoaded)
                if (sourceRefresh !is LoadState.Loading) swipeRefresh.isRefreshing = false
                if (sourceRefresh is LoadState.Error) Log.i("BreakingNewsFrag", "${sourceRefresh.error.message}")
            }
        }

        binding?.btnRetry?.setOnClickListener {
            newsAdapter.retry()
        }

        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                newsAdapter.refresh()
                Log.i("BreakingNews", "isRefreshing: ${swipeRefresh.isRefreshing}" )
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterWithPagination()
        binding?.rvBreakingNews?.apply {
            adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter(newsAdapter::retry),
                footer = NewsLoadStateAdapter(newsAdapter::retry)
            )
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}