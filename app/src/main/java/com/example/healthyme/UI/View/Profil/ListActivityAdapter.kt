package com.example.healthyme.UI.View.Profil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.healthyme.databinding.ListItemProfilBinding

class ListActivityAdapter(
    context: Context,
    private val menuList: List<ListActivityModel>
) : ArrayAdapter<ListActivityModel>(context, 0, menuList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view = convertView ?: LayoutInflater.from(context)
//            .inflate(R.layout.list_item, parent, false)
//
//        val menuItem = getItem(position)
//        val imageView: ImageView = view.findViewById(R.id.imageView)
//        val textName: TextView = view.findViewById(R.id.textName)
//        val textDesc: TextView = view.findViewById(R.id.textDesc)
//
//        menuItem?.let {
//            imageView.setImageResource(0)
//            textName.text = it.name
//            textDesc.text = it.desc
//        }
//        return view
        val binding: ListItemProfilBinding

        // Gunakan binding untuk inflating atau mengakses view
        if (convertView == null) {
            binding = ListItemProfilBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding // Simpan binding dalam tag view untuk daur ulang
        } else {
            binding = convertView.tag as ListItemProfilBinding
        }

        val menuItem = getItem(position)
        menuItem?.let {
            binding.imageView.setImageResource(it.image) // Sesuaikan resource imagenya
            binding.textName.text = it.name
            //binding.textDesc.text = it.desc

        }
        return binding.root
    }

}