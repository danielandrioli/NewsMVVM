package com.dboy.newsmvvm.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.databinding.FragmentArticleNewsBinding
import com.dboy.newsmvvm.ui.NewsViewModel

class ArticleNewsFragment : Fragment() {
    private var binding: FragmentArticleNewsBinding? = null
    private val args: ArticleNewsFragmentArgs by navArgs()
    private val article: Article by lazy { args.article }
    private val newsViewModel: NewsViewModel by activityViewModels()
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
            it.webViewClient =
                WebViewClient() //a view vai carregar no WebView, e não no browser do celular
            it.loadUrl(article.url)
        }

        val savedArticlesList = newsViewModel.getSavedNews()

        binding?.fabFavorite?.setOnClickListener {
            if (article in savedArticlesList.value!!){
//                val art = savedArticlesList!!.value?.find {
//                    it.url == article.url
//                }
//                Log.i("ArticleNewsFragment" ,"- it.url = ${art?.url}\narticle.url = ${article.url}\nit.id = ${art?.id}\narticle.id = ${article.id}")

                newsViewModel.deleteNews(article)
                Log.i("ArticleNewsFragment" ,"- Deletando artigo!")
                //nao consegue deletar se o article vem do BreakingNewsFragment pq de lá o artigo não tem o ID.
                //Devo remover o ID e colocar primaryKey como a URL!! E sem ser automaticamente criada pelo Room.
            } else{
                newsViewModel.saveNews(article)
                Log.i("ArticleNewsFragment" ,"- Salvando artigo!")
            }
        }

        savedArticlesList.observe(viewLifecycleOwner) {
            if (article in it) {  //terei que mexer na comparação da classe Article. A comparação deve ser com base na url
                binding?.fabFavorite?.setColorFilter(R.color.favorite_rosa) //cor do ícone
                Log.i("ArticleNewsFragment" ,"- Está na lista!")
            } else {
                binding?.fabFavorite?.setColorFilter(R.color.favorite_vazio)
                Log.i("ArticleNewsFragment" ,"- NÃO está na lista!")

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}