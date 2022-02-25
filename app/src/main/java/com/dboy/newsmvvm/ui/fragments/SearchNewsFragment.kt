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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.adapters.NewsAdapter
import com.dboy.newsmvvm.api.CountryCode
import com.dboy.newsmvvm.api.response.Language
import com.dboy.newsmvvm.databinding.FragmentSearchNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import com.dboy.newsmvvm.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
//funcionou sem o @AndroidEntryPoint aqui. É necessário acrescentar essa anotação?
class SearchNewsFragment : Fragment() {
    private var binding: FragmentSearchNewsBinding? = null
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter
    private val TAG = "SearchNewsFragment"

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
                    if (it.isNotEmpty()){
                        newsViewModel.searchNews(it.toString(), Language.en)
                    }
                }
            }
        }
//O código acima foi escrito para a função search ter um delay antes de procurar. Isso evita com que uma nova procura seja
// iniciada a cada letra escrita ou apagada. O job foi utilizado e ele facilitou a escrita do código.
        newsViewModel.searchedNews.observe(viewLifecycleOwner) {
            when (it) {
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
            val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleNewsFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun showOrHideProgressBar(visibility: Int) {
        binding?.pgSearchNews?.visibility = visibility
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding?.rvSearchNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}