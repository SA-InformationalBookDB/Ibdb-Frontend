package szarch.bme.hu.ibdb.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_favourites.*
import szarch.bme.hu.ibdb.R
import szarch.bme.hu.ibdb.network.models.book.Book
import szarch.bme.hu.ibdb.ui.base.BaseApplication
import szarch.bme.hu.ibdb.util.Navigator
import javax.inject.Inject

class FavouritesFragment : androidx.fragment.app.Fragment(), FavouriteAdapter.Listener, FavouriteScreen {

    @Inject
    lateinit var favouritePresenter: FavouritePresenter

    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favourites, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favouritePresenter.attachScreen(this)
        setupRecyclerViewAdapter()
        setupRecyclerView()
        getBooks()
    }

    private fun injectFragment() {
        (context?.applicationContext as? BaseApplication)
            ?.injector
            ?.inject(this)
            ?: throw IllegalStateException("InjectedFragment should not be used without an Application that inherits from BaseApplication")
    }

    private fun setupRecyclerViewAdapter() {
        favouriteAdapter = FavouriteAdapter()
        favouriteAdapter.listener = this
    }

    private fun setupRecyclerView() {
        rv_favourite_favourites.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        rv_favourite_favourites.adapter = favouriteAdapter
    }

    private fun getBooks() {
        favouritePresenter.getFavouriteBooks()
    }

    override fun onDestroy() {
        favouritePresenter.detachScreen()
        super.onDestroy()
    }

    override fun showFavouritesBooks(bookList: List<Book>) {
        if (bookList.isEmpty()) {
            vf_favourites.displayedChild = 1
        } else {
            vf_favourites.displayedChild = 0
            favouriteAdapter.submitList(bookList)
        }
    }

    override fun showErrorMeaasge(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(bookItemId: String) {
        Navigator.navigateToDetailScreen(requireActivity(), bookItemId)
    }

    companion object {
        val TAG = FavouritesFragment::class.java.simpleName
    }

}