package com.become.lottery.adapter

import android.widget.CheckBox
import com.become.lottery.carousel.R
import com.become.lottery.vo.PrizeEntity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by TangYaoRong On 2020/7/2
 */
class ExpectationAdapter(
    data: MutableList<PrizeEntity>? = null
) : BaseQuickAdapter<PrizeEntity, BaseViewHolder>(R.layout.item_expecation, data) {

    override fun convert(holder: BaseViewHolder, item: PrizeEntity) {
        holder.setText(R.id.item_lottery_name, item.name)
        val mCheckBox = (holder.getView<CheckBox>(R.id.item_lottery_name))
        mCheckBox.isChecked = item.checked!!
    }
}