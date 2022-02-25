package com.dboy.newsmvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.databinding.FragmentArticleNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel

class ArticleNewsFragment : Fragment() {
    private var binding: FragmentArticleNewsBinding? = null
    private val args: ArticleNewsFragmentArgs by navArgs()
    private val article: Article by lazy { args.article }
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
        binding?.webViewArticle?.let {
            it.webViewClient = WebViewClient() //a view vai carregar no WebView, e não no browser do celular
            it.loadUrl(article.url)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}