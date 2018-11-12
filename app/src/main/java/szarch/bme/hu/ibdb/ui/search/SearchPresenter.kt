package szarch.bme.hu.ibdb.ui.search

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szarch.bme.hu.ibdb.network.repository.BookRepository
import szarch.bme.hu.ibdb.ui.base.Presenter
import szarch.bme.hu.ibdb.util.Contexts
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val bookRepository: BookRepository
) : Presenter<SearchScreen>() {

    fun searchBooks(query: String) {
        GlobalScope.launch(Contexts.UI) {
            screen?.showBooks(bookRepository.findBooks(query))
        }
    }

}