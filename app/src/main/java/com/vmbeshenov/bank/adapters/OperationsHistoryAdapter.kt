package com.vmbeshenov.bank.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.data.OperationsHistory
import com.vmbeshenov.bank.databinding.ItemOperationsHistoryBinding
import java.time.LocalDate

class OperationsHistoryAdapter: RecyclerView.Adapter<OperationsHistoryAdapter.HistoryHolder>() {

    class HistoryHolder(item: View): RecyclerView.ViewHolder(item){

        private val binding = ItemOperationsHistoryBinding.bind(item)

        fun bind(operationsHistory: OperationsHistory) = with(binding){
            val date = LocalDate.parse(operationsHistory.date)
            val month = translateMonth(date.month.toString())
            if (date.year == 2022){
                operationsHistoryDate.text = "${date.dayOfMonth}, $month"
            }
            else{
                operationsHistoryDate.text = "${date.dayOfMonth} $month, ${date.year}"
            }
            operationsHistorySum.text = operationsHistory.credit.toString()
            if(operationsHistory.credit!! > 0.0){
                operationsHistoryTitle.textSize = 18.0F
                operationsHistoryImage.setImageResource(R.drawable.ic_forward)
                operationsHistorySumPlus.visibility = View.VISIBLE
                operationsHistorySum.setTextColor(Color.parseColor("#FF018786"))
                operationsHistorySumSymbol.setTextColor(Color.parseColor("#FF018786"))
            }
            else if(operationsHistory.category == "Платежи и переводы" && operationsHistory.credit!! < 0.0){
                operationsHistoryTitle.textSize = 18.0F
                operationsHistoryImage.setImageResource(R.drawable.ic_forward)
                operationsHistorySumPlus.visibility = View.GONE
                operationsHistorySum.setTextColor(Color.parseColor("#FF000000"))
                operationsHistorySumSymbol.setTextColor(Color.parseColor("#FF000000"))
            }
            else{
                operationsHistoryTitle.textSize = 20.0F
                operationsHistorySumPlus.visibility = View.GONE
                operationsHistoryImage.setImageResource(R.drawable.ic_shop_operation)
                operationsHistorySum.setTextColor(Color.parseColor("#FF000000"))
                operationsHistorySumSymbol.setTextColor(Color.parseColor("#FF000000"))
            }
            operationsHistoryCategory.text = operationsHistory.category
            operationsHistoryTitle.text = operationsHistory.destination
        }
        private fun translateMonth(month: String): String {
            var result = ""
            when(month){
                "JANUARY" -> result = "Январь"
                "FEBRUARY" -> result = "Февраль"
                "MARCH" -> result = "Март"
                "APRIL" -> result = "Апрель"
                "MAY" -> result = "Май"
                "JUNE" -> result = "Июнь"
                "JULY" -> result = "Июль"
                "AUGUST" -> result = "Август"
                "SEPTEMBER" -> result = "Сентябрь"
                "OCTOBER" -> result = "Октябрь"
                "NOVEMBER" -> result = "Ноябрь"
                "DECEMBER" -> result = "Декабрь"
            }
            return result
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_operations_history, parent, false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.bind(OperationsHistory.operations[position])
    }

    override fun getItemCount(): Int {
        return OperationsHistory.operations.size
    }

}