package com.become.lottery.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.become.lottery.adapter.ExpectationAdapter
import com.become.lottery.carousel.R
import com.become.lottery.mock.LotteryData
import com.become.lottery.view.LotteryView
import com.become.lottery.vo.PrizeEntity
import kotlinx.android.synthetic.main.activity_lottery.*

/**
 * Created by TangYaoRong On 2020/6/28
 */
class LotteryActivity : Activity(), LotteryView.OnLotteryFinishListener {

    private lateinit var mExpectation: RecyclerView
    private var prizeArray: ArrayList<PrizeEntity> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery)
        initView()
        initData()
    }

    private fun initView() {
        lottery_view.setOnLotteryFinishListener(this)
        mExpectation = findViewById(R.id.lottery_expectation_list)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        prizeArray.clear()
        prizeArray = LotteryData.getInstance().data
        lottery_view.setPrizeList(prizeArray)
        val mAdapter = ExpectationAdapter(prizeArray)
        mExpectation.layoutManager = GridLayoutManager(this, 3)
        mExpectation.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            for (index in 0 until prizeArray.size) {
                prizeArray[index].checked = position == index
                if (index == position)
                    lottery_view.setPrizeCheck(position + 1)
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onFinish(index: Int) {
        lottery_fetch.text = (prizeArray[index - 1].name.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        prizeArray = ArrayList()
        lottery_view.setPrizeList(prizeArray)
    }
}