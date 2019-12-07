package app.kojima.jiko.boox.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import app.kojima.jiko.boox.R

class SettingAdapter(val context: Context, val items: List<String>): BaseAdapter(){
    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        lateinit var viewHolder: ViewHolder
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(view == null) {
            view = layoutInflater.inflate(R.layout.setting_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.settingText.text = items[p0]
        if(items[p0].equals("サインアウト")) {
            viewHolder.settingText.setTextColor(context.resources.getColor(R.color.danger))
        }
        return view!!
    }

    override fun getItem(p0: Int): String = items[p0]
    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = items.size
    data class ViewHolder(val view: View) {
        val settingText = view.findViewById<TextView>(R.id.settingText)
    }
}