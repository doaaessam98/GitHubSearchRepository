package com.example.githubsearchrepository.ui

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearchrepository.databinding.ActivityMainBinding

import com.example.githubsearchrepository.model.uiModel.UiAction
import com.example.githubsearchrepository.model.uiModel.UiModel
import com.example.githubsearchrepository.model.uiModel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  lateinit  var binding: ActivityMainBinding
    private  val viewModel: SearchViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setupList()
//        setupView()

         var decoration =DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

    }


//    private fun setupView() {
//        lifecycleScope.launch {
//            viewModel.searchRepo("Android").collect {
//                repoAdapter.submitData(it)
//
//        }
//    }}

//    private fun setupList() {
//        repoAdapter=ReposAdapter()
//       binding.list .apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = repoAdapter
//        }
//    }
    private fun ActivityMainBinding.bindState(
    uiState: StateFlow<UiState>,
    pagingData: Flow<PagingData<UiModel>>,
    uiActions: (UiAction) -> Unit
    ) {
        val repoAdapter = ReposAdapter()
    val header = ReposLoadStateAdapter { repoAdapter.retry() }

    this.recyclerView.adapter = repoAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = ReposLoadStateAdapter { repoAdapter.retry() }
        )


        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        bindList(
            header = header,
            repoAdapter = repoAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }
    private fun ActivityMainBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
      searchImage.setOnClickListener {
          updateRepoListFromInput(onQueryChanged)
      }
        searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState.map { it.query }.distinctUntilChanged()
                .collect(searchRepo::setText)
        }
    }

    private fun ActivityMainBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {

        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }





        private fun ActivityMainBinding.bindList(
            header: ReposLoadStateAdapter,

            repoAdapter: ReposAdapter,
            uiState: StateFlow<UiState>,
            pagingData: Flow<PagingData<UiModel>>,
            onScrollChanged: (UiAction.Scroll) -> Unit
        ) {
            retryButton.setOnClickListener { repoAdapter.retry() }
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
                }
            })

            val notLoading = repoAdapter.loadStateFlow
                .asRemotePresentationState().map {
                    it == RemotePresentationState.PRESENTED }


            val hasNotScrolledForCurrentSearch = uiState
                .map { it.hasNotScrolledForCurrentSearch }
                .distinctUntilChanged()

            val shouldScrollToTop = combine(
                notLoading,
                hasNotScrolledForCurrentSearch,
                Boolean::and
            )
                .distinctUntilChanged()

            lifecycleScope.launch {
                pagingData.collectLatest{
                    repoAdapter.submitData(it)
                }

            }

            lifecycleScope.launch {
                shouldScrollToTop.collect { shouldScroll ->
                    if (shouldScroll) recyclerView.scrollToPosition(0)
                }
            }


            lifecycleScope.launch {
                repoAdapter.loadStateFlow.collect { loadState ->
                    header.loadState = loadState.mediator
                        ?.refresh
                        ?.takeIf { it is LoadState.Error && repoAdapter.itemCount > 0 }
                        ?: loadState.prepend
                    val isListEmpty = loadState.refresh is LoadState.Error && repoAdapter.itemCount == 0
                    emptyList.isVisible = isListEmpty
                    recyclerView.isVisible =loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                    progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                    retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && repoAdapter.itemCount == 0

                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            this@MainActivity,
                            "\uD83D\uDE28 Whoops ${it.error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                                    }

        }




}