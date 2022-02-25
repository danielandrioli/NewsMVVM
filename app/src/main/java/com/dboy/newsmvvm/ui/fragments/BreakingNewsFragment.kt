package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.adapters.NewsAdapter
import com.dboy.newsmvvm.databinding.FragmentBreakingNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import com.dboy.newsmvvm.ui.NewsViewModel_Factory
import com.dboy.newsmvvm.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {
    private var binding: FragmentBreakingNewsBinding? = null
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter
    private val TAG = "BreakingNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        newsViewModel.breakingNews.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    showOrHideProgressBar(View.GONE)
                    newsAdapter.differ.submitList(it.data?.articles)

                }
                is Resource.Error -> {
                    showOrHideProgressBar(View.GONE)
                    Log.e(TAG, it.message.toString())
                }
                is Resource.Loading -> showOrHideProgressBar(View.VISIBLE)
            }
        }

        newsAdapter.setOnItemArticleClickListener {
            val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleNewsFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun showOrHideProgressBar(visibility: Int){
        binding?.pgBreakingNews?.visibility = visibility
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding?.rvBreakingNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}