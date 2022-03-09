package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.adapters.NewsAdapterWithPagination
import com.dboy.newsmvvm.databinding.FragmentBreakingNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

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
    }

    private fun showOrHideProgressBar(visibility: Int) {
        binding?.pgBreakingNews?.visibility = visibility
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterWithPagination()
        binding?.rvBreakingNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}