package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.newsmvvm.adapters.NewsAdapter
import com.dboy.newsmvvm.databinding.FragmentFavoriteNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavoriteNewsFragment : Fragment() {
    private var binding: FragmentFavoriteNewsBinding? = null
    private lateinit var newsAdapter: NewsAdapter
    private val newsViewModel: NewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecyclerView()
        newsAdapter.setOnItemArticleClickListener {
            val action = FavoriteNewsFragmentDirections.actionFavoriteNewsFragmentToArticleNewsFragment(article = it)
            findNavController().navigate(action)
        }

        newsViewModel.getSavedNews().observe(viewLifecycleOwner){
            newsAdapter.differ.submitList(it)
        }

        //arrastar para o lado para deletar:
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteNews(article)

                Snackbar.make(view, "Article successfully removed from favorites!", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.saveNews(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding?.rvFavoriteNews)
        }
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        binding?.rvFavoriteNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}