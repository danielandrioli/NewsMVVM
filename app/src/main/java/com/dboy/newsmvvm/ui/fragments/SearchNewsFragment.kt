package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dboy.newsmvvm.adapters.NewsAdapterWithPagination
import com.dboy.newsmvvm.adapters.NewsLoadStateAdapter
import com.dboy.newsmvvm.databinding.FragmentSearchNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import com.dboy.newsmvvm.util.Language
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
                    if (it.isNotEmpty()) {
                        newsViewModel.searchNews(it.toString(), Language.en)
                    }
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
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterWithPagination()
        binding?.rvSearchNews?.apply {
            adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter(newsAdapter::retry),
                footer = NewsLoadStateAdapter(newsAdapter::retry)
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}