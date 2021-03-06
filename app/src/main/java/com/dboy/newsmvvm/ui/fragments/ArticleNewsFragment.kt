package com.dboy.newsmvvm.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.databinding.FragmentArticleNewsBinding
import com.dboy.newsmvvm.ui.MainActivity
import com.dboy.newsmvvm.ui.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ArticleNewsFragment : Fragment() {
    private var binding: FragmentArticleNewsBinding? = null
    private val args: ArticleNewsFragmentArgs by navArgs()
    private val article: Article by lazy { args.article }
    private val newsViewModel: NewsViewModel by activityViewModels()
    private val bottomNavigationView: BottomNavigationView by lazy { (activity as MainActivity).findViewById(R.id.bottomNavigationView) }
    //Não era necessário enviar o objeto Article como argumento. Bastava capturar apenas sua URL e enviar como String.
    //Para fins didáticos sobre como utilizar o Parcelize, deixei assim mesmo.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomNavigationView.isVisible = false
        binding?.webViewArticle?.let {
            it.webViewClient =
                WebViewClient() //the view will be loaded in the WebView, and not in the phone's browser.
            it.loadUrl(article.url)
        }

        val savedArticlesList = newsViewModel.getSavedNews()

        binding?.fabFavorite?.setOnClickListener {
            val savedArticleFromDb = savedArticlesList.value?.find {
                it.url == article.url
            }
            if (savedArticleFromDb != null) { // != null means it is in the database
                Toast.makeText(requireContext(), R.string.articleRemoved, Toast.LENGTH_SHORT).show()
                newsViewModel.deleteNews(savedArticleFromDb) //savedArtFromDb came from db and it contains an id. With an id it's possible to delete the article from db.
            } else {
                Toast.makeText(requireContext(), R.string.articleSaved, Toast.LENGTH_SHORT).show()
                newsViewModel.saveNews(article) //Here, the article is not in the db, so I'm saving the article that came from args.
            }
        }

        savedArticlesList.observe(viewLifecycleOwner) {
            if (article in it) {
                binding?.fabFavorite?.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.favorite_rosa
                    )
                )
            } else {
                binding?.fabFavorite?.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.favorite_vazio
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        bottomNavigationView.isVisible = true
        super.onDestroyView()
    }
}