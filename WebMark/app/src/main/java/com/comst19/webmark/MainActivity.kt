package com.comst19.webmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnCreateContextMenuListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.comst19.webmark.databinding.ActivityMainBinding
import com.comst19.webmark.databinding.AddDialogBinding
import com.comst19.webmark.databinding.ItemWebmarkBinding
import com.comst19.webmark.databinding.MenuDialogBinding
import com.comst19.webmark.db.AppDatabase
import com.comst19.webmark.db.WebMarkDao
import com.comst19.webmark.db.WebMarkEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() , OnItemClickListener {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding : ActivityMainBinding

    private lateinit var db : AppDatabase
    private lateinit var webmarkDao: WebMarkDao
    private lateinit var webmarkList : ArrayList<WebMarkEntity>
    private lateinit var webmarkListFilter : ArrayList<WebMarkEntity>
    private lateinit var adapter : WebMarkRVAdapter
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        webmarkDao = db.getWebMarkDao()

        setSupportActionBar(binding.bar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        getAllWebMarkList()

        binding.writeBtn.setOnClickListener {
            addDialogShow()
        }


    }

    private fun getAllWebMarkList() {
        Thread{
            webmarkList = ArrayList(webmarkDao.getAllWebMark())
            webmarkListFilter = ArrayList(webmarkDao.getAllWebMark())
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView(){
        runOnUiThread{
            adapter = WebMarkRVAdapter(webmarkList, db, this, webmarkListFilter)
            binding.RV.adapter = adapter
            binding.RV.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun addDialogShow(){
        val bindingAddDialog = AddDialogBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)

        dialog = AlertDialog.Builder(this).setView(bindingAddDialog.root).setCancelable(false).show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingAddDialog.addBtn.setOnClickListener {

            val url = bindingAddDialog.urlArea.text.toString()
            var nickname = bindingAddDialog.nicknameArea.text.toString()

            if(nickname.isBlank()){
                nickname = url
            }
            if(url.isBlank()){
                Toast.makeText(this,"url 항목을 채워주세요", Toast.LENGTH_SHORT).show()
            }else{
                Thread{
                    adapter.addListItem(WebMarkEntity(null, url, nickname))
                    webmarkDao.insertWebMark(WebMarkEntity(null, url, nickname))
                    runOnUiThread {
                        Toast.makeText(this,"WebMark에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        adapter.notifyDataSetChanged()
                    }
                }.start()

            }

        }
        bindingAddDialog.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)

        val menuItem = menu!!.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(filterString: String?): Boolean {
                adapter.filter.filter(filterString)
                return true
            }

            override fun onQueryTextChange(filterString: String?): Boolean {
                adapter.filter.filter(filterString)
                return true
            }

        })

        return true
//        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    override fun onClickMove(position: Int) {
        val intent = Intent(this,WebShowActivity::class.java)
        Log.w(TAG, position.toString())
        intent.putExtra("url", webmarkList[position].url)
        startActivity(intent)
    }

    override fun onClickShare(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT,url)
        }
        startActivity(Intent.createChooser(intent, url))
    }

    override fun onRestart() {
        super.onRestart()
        getAllWebMarkList()
    }

}