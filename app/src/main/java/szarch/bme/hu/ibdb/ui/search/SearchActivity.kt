package szarch.bme.hu.ibdb.ui.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import szarch.bme.hu.ibdb.R
import szarch.bme.hu.ibdb.network.models.book.Book
import szarch.bme.hu.ibdb.ui.base.BaseApplication
import szarch.bme.hu.ibdb.ui.favourites.FavouriteAdapter
import szarch.bme.hu.ibdb.util.Navigator
import javax.inject.Inject


class SearchActivity : AppCompatActivity(), SearchScreen, FavouriteAdapter.Listener {

    @Inject
    lateinit var searchPresenter: SearchPresenter

    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectActivity()
        setContentView(R.layout.activity_search)
        setupRecyclerViewAdapter()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        searchPresenter.attachScreen(this)
        doSearch(intent)
    }

    override fun onDestroy() {
        searchPresenter.detachScreen()
        super.onDestroy()
    }

    private fun injectActivity() {
        (applicationContext as? BaseApplication)
            ?.injector
            ?.inject(this)
            ?: throw IllegalStateException("InjectedActivity should not be used without an Application that inherits from BaseApplication")
    }

    private fun setupRecyclerViewAdapter() {
        favouriteAdapter = FavouriteAdapter()
        favouriteAdapter.listener = this
    }

    private fun setupRecyclerView() {
        rv_search_books.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
                )
        rv_search_books.adapter = favouriteAdapter
    }

    private fun doSearch(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchPresenter.searchBooks(query)
            }
        }
    }

    override fun showBooks(bookList: List<Book>) {
        if (bookList.isEmpty()) {
            vf_search_books.displayedChild = 1
        } else {
            vf_search_books.displayedChild = 0
            favouriteAdapter.submitList(bookList)
        }
    }

    override fun onItemSelected(bookItemId: String) {
        Navigator.navigateToDetailScreen(this@SearchActivity, bookItemId)
    }

    override fun showErrorMessage(message: String?) {
        Toast.makeText(this@SearchActivity, message, Toast.LENGTH_LONG).show()
    }

}
