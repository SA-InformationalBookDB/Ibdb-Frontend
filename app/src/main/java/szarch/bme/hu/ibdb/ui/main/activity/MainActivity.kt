package szarch.bme.hu.ibdb.ui.main.activity

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import szarch.bme.hu.ibdb.R
import szarch.bme.hu.ibdb.domain.local.SharedPreferencesProvider
import szarch.bme.hu.ibdb.ui.activities.ActivitiesFragment
import szarch.bme.hu.ibdb.ui.base.BaseApplication
import szarch.bme.hu.ibdb.ui.favourites.FavouritesFragment
import szarch.bme.hu.ibdb.ui.main.fragment.MainFragment
import szarch.bme.hu.ibdb.ui.search.SearchActivity
import szarch.bme.hu.ibdb.ui.upload.UploadFragment
import szarch.bme.hu.ibdb.ui.users.UsersFragment
import szarch.bme.hu.ibdb.util.Navigator
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesProvider: SharedPreferencesProvider

    private lateinit var fragment: androidx.fragment.app.Fragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_main -> {
                showFragment(MainFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favourites -> {
                showFragment(FavouritesFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_upload -> {
                showFragment(UploadFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_activities -> {
                showFragment(ActivitiesFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                showFragment(UsersFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectActivity()
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        showFragment(MainFragment.TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchView: SearchView = menu?.findItem(R.id.menu_main_search)?.actionView as SearchView
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, SearchActivity::class.java)))
        searchView.queryHint = resources.getString(R.string.search_hint)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_main_account -> {
                Navigator.navigateToAccountActivity(this@MainActivity)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        if (fragment.tag == MainFragment.TAG) {
            finish()
        } else {
            navigation.selectedItemId = R.id.navigation_main
        }
    }

    private fun injectActivity() {
        (applicationContext as? BaseApplication)
            ?.injector
            ?.inject(this)
            ?: throw IllegalStateException("InjectedActivity should not be used without an Application that inherits from BaseApplication")
    }

    private fun showFragment(tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            fragment = when (tag) {
                FavouritesFragment.TAG -> FavouritesFragment()
                UploadFragment.TAG -> UploadFragment()
                ActivitiesFragment.TAG -> ActivitiesFragment()
                UsersFragment.TAG -> UsersFragment()
                else -> MainFragment()
            }
        } else {
            fragment = supportFragmentManager.findFragmentByTag(tag)!!
        }
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, tag).addToBackStack(null).commit()
    }

    private fun isUserAuthenticated(): Boolean {
        return sharedPreferencesProvider.getClientAccessToken().isEmpty()
    }

    private fun showAuthenticationError() {
        Toast.makeText(
            this@MainActivity,
            resources.getString(R.string.no_authentication_feature_text),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACCOUNT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commitNowAllowingStateLoss()
            }
        }
    }

    companion object {
        const val ACCOUNT_REQUEST_CODE = 1
    }

}
