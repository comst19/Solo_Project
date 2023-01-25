package com.comst19.webmark

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.comst19.webmark.databinding.AddDialogBinding
import com.comst19.webmark.databinding.EditDialogBinding
import com.comst19.webmark.databinding.ItemWebmarkBinding
import com.comst19.webmark.databinding.MenuDialogBinding
import com.comst19.webmark.db.AppDatabase
import com.comst19.webmark.db.WebMarkDao
import com.comst19.webmark.db.WebMarkEntity

class WebMarkRVAdapter(private var webmarkList : ArrayList<WebMarkEntity>,
                       private val db : AppDatabase,
                       private val listener: OnItemClickListener, private var webmarkListFilter : ArrayList<WebMarkEntity>)
    : RecyclerView.Adapter<WebMarkRVAdapter.ViewHolder>(), Filterable{

    private lateinit var menuDialog : AlertDialog
    private lateinit var editDialog : AlertDialog

    fun addListItem(webmarkItem: WebMarkEntity){
        webmarkList.add(0,webmarkItem)
        webmarkListFilter = webmarkList
    }

    fun menuDialogShow(binding : ItemWebmarkBinding, webmarkItem : WebMarkEntity, position: Int){
        val bindingMenuDialog = MenuDialogBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)

        menuDialog = AlertDialog.Builder(binding.root.context).setView(bindingMenuDialog.root).show()

        bindingMenuDialog.editBtn.setOnClickListener {
            val bindingEditDialog = EditDialogBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)
            bindingEditDialog.urlArea.setText(webmarkItem.url)
            bindingEditDialog.nicknameArea.setText(webmarkItem.nickname)

            editDialog = AlertDialog.Builder(binding.root.context).setView(bindingEditDialog.root).show()

            bindingEditDialog.addBtn.setOnClickListener {
                Thread{
                    val innerLstWebMark = db.getWebMarkDao().getAllWebMark()
                    for(item in innerLstWebMark){
                        if(item.id == webmarkItem.id){
                            item.url = bindingEditDialog.urlArea.text.toString()
                            item.nickname = bindingEditDialog.nicknameArea.text.toString()
                            db.getWebMarkDao().updateWebMark(item)
                        }
                    }
                    webmarkItem.url = bindingEditDialog.urlArea.text.toString()
                    webmarkItem.nickname = bindingEditDialog.nicknameArea.text.toString()
                    webmarkList.set(position, webmarkItem)
                    webmarkListFilter = webmarkList
                    (binding.root.context as Activity).runOnUiThread {
                        notifyDataSetChanged() // 리스트 새로고침
                        Toast.makeText(binding.root.context, "웹마크가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }.start()
                editDialog.dismiss()
                menuDialog.dismiss()
            }
            bindingEditDialog.cancelBtn.setOnClickListener {
                editDialog.dismiss()
            }
        }

        bindingMenuDialog.deleteBtn.setOnClickListener {
            Thread{
                val innerLstWebMark = db.getWebMarkDao().getAllWebMark()
                for(item in innerLstWebMark){
                    if(item.id == webmarkItem.id){
                        db.getWebMarkDao().deleteWebMark(item)
                    }
                }
                webmarkList.remove(webmarkItem)
                webmarkListFilter = webmarkList
                (binding.root.context as Activity).runOnUiThread {
                    notifyDataSetChanged()
                    Toast.makeText(binding.root.context, "웹마크가 제거되었습니다", Toast.LENGTH_SHORT).show()
                }
            }.start()
            menuDialog.dismiss()
        }

        bindingMenuDialog.shareBtn.setOnClickListener {
            listener.onClickShare(webmarkItem.url)
//            val intent = Intent(Intent.ACTION_SEND).apply {
//                type = "text/plain"
//                putExtra(Intent.EXTRA_TEXT,webmarkItem.url)
//            }
//            startActivity(Intent.createChooser(intent, webmarkItem.url))
        }
    }

    inner class ViewHolder(private val binding : ItemWebmarkBinding) : RecyclerView.ViewHolder(binding.root){
//        val saveUrl = binding.urlArea
//        val saveNickname = binding.nicknameArea
//        val root = binding.root

        fun bind(webmarkItem: WebMarkEntity){
            binding.urlArea.setText(webmarkItem.url)
            binding.nicknameArea.setText(webmarkItem.nickname)

            binding.menuBtn.setOnClickListener {
                menuDialogShow(binding, webmarkItem, adapterPosition)
            }

            binding.webMoveBtn.setOnClickListener {
                listener.onClickMove(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemWebmarkBinding = ItemWebmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemViewHolder = ViewHolder(binding)

//        binding.webMoveBtn.setOnClickListener {
//
//            var position = itemViewHolder.adapterPosition
//            Log.w("어댑터", position.toString())
//            listener.onClick(position)
//        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(webmarkList[position])


    }

    override fun getItemCount(): Int {
        return webmarkList.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(charsequence: CharSequence?): FilterResults {

                val filterResults = FilterResults();
                if(charsequence == null || charsequence.length < 0){
                    filterResults.count = webmarkListFilter.size
                    filterResults.values = webmarkListFilter
                }else{

                    var searchChr = charsequence.toString().toLowerCase()
                    val filteringItem = ArrayList<WebMarkEntity>()
                    for(item in webmarkListFilter){
                        if(item.url.contains(searchChr) || item.nickname.contains(searchChr)){
                            filteringItem.add(item)
                        }

                    }
                    filterResults.count = filteringItem.size
                    filterResults.values = filteringItem
                }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                webmarkList = filterResults!!.values as ArrayList<WebMarkEntity>
                notifyDataSetChanged()
            }

        }
    }
}