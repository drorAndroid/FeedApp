package com.dror.feedapp.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.dror.feedapp.R
import com.dror.feedapp.model.FeedMessage
import kotlin.collections.ArrayList


class FeedMessagesAdapter: RecyclerView.Adapter<FeedMessagesAdapter.ItemViewHolder>(), Filterable {
    private var originalMessages: MutableList<FeedMessage> = mutableListOf()
    private var messages: MutableList<FeedMessage> = mutableListOf()

    private var valueFilter: ValueFilter? = null


    fun update(feedMessage: FeedMessage) {
        this.originalMessages.add(0, feedMessage)
        this.messages.add(0, feedMessage)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        var weightTextView: TextView = itemView.findViewById(R.id.weightTextView)
        var backgroundColorLayout: RelativeLayout = itemView.findViewById(R.id.backgroundColorLayout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_message, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = messages.get(position)
        item.let { feedMessage ->
            val color = Color.parseColor(item.bagColor)
            val bgShape = holder.backgroundColorLayout.background as GradientDrawable
            bgShape.mutate()
            bgShape.setColor(color)
            holder.nameTextView.text = item.name
            holder.weightTextView.text = String.format("%.1f${feedMessage.units}", feedMessage.weight)
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }


    private inner class ValueFilter : Filter() {
        private var prevLength = 0

        //Invoked in a worker thread to filter the data according to the constraint.
        override fun performFiltering(constraint: CharSequence): FilterResults {

            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList: ArrayList<FeedMessage> = ArrayList()

                val search = constraint.toString()
                val searchDouble = search.toDouble()
                for(i in originalMessages) {
                    if(i.weight > searchDouble) {
                        filterList.add(i)
                    }
                }

                results.count = filterList.size
                results.values = filterList
                prevLength = constraint.length
            }
            else {
                results.count = originalMessages.size
                results.values = originalMessages
            }


            return results
        }

        //Invoked in the UI thread to publish the filtering results in the user interface.
        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            messages = results.values as MutableList<FeedMessage>
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter? {
        if (valueFilter == null) {
            valueFilter = ValueFilter()
        }
        return valueFilter
    }
}