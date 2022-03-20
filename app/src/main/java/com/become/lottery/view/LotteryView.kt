package com.become.lottery.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.animation.DecelerateInterpolator
import com.become.lottery.carousel.R
import com.become.lottery.utils.Util
import com.become.lottery.vo.PrizeEntity
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by TangYaoRong On 2020/6/22
 */
class LotteryView : View {

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mCenterCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mCenterTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mSize: Int = 8//有几个奖项

    private var mTextSize: Float = Util.spToPx(12).toFloat()//字体大小

    private var mCenterTextSize: Float = Util.spToPx(24).toFloat()//中间抽奖字体大小

    private var mRadius: Float = Util.dpToPx(140).toFloat()//外圆半径

    private var mInnerRadius: Float = Util.dpToPx(30).toFloat()//内圆半径

    private var mCenterCircleRadius: Float = Util.dpToPx(1).toFloat()//边线宽度

    private var mBorderColor: Int = Color.parseColor("#F3EBFD")//边框颜色

    private var mCenterX: Float = width / 2.toFloat()

    private var mCenterY: Float = height / 2.toFloat()

    private lateinit var mContext: Context

    private var cRotateDegree = 0f//这个主要是用来控制旋转角度

    private val cDurationTime = 6000L//旋转时间

    private var cTurns = 4 //转的圈数

    private var cWinning = 1 //中奖的内容

    private var cPiece = 360f / mSize // 单块奖品的角度

    private var cPieceMiddleAngel = 0f //单块奖品正中心角度

    //属性动画初始化
    private var animator =
        ValueAnimator.ofFloat(0f, (360f - (cPiece * (cWinning - 1))) + (360f * cTurns))

    private lateinit var mOnLotteryFinishListener: OnLotteryFinishListener

    constructor(context: Context?) : super(context) {
        if (context != null) {
            mContext = context
        }
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (context != null) {
            mContext = context
        }
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (context != null) {
            mContext = context
        }
        init()
    }

    private fun init() {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mCenterCircleRadius
        mPaint.color = mBorderColor
        mPaint.textSize = mTextSize

        mCenterCirclePaint.style = Paint.Style.FILL_AND_STROKE
        mCenterCirclePaint.strokeWidth = mCenterCircleRadius
        mCenterCirclePaint.color = mBorderColor

        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = mBorderColor
        mTextPaint.textSize = mTextSize
        mTextPaint.textAlign = Paint.Align.CENTER

        mCenterTextPaint.style = Paint.Style.FILL_AND_STROKE
        mCenterTextPaint.color = mBorderColor
        mCenterTextPaint.textSize = mCenterTextSize
    }

    private fun startRotate() {
        animator.setFloatValues(0f, (360f - (cPiece * (cWinning - 1))) + (360f * cTurns))
        animator.duration = cDurationTime
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animationValue ->
            cRotateDegree = animationValue.animatedValue as Float
            when (animationValue.animatedValue) {
                (360f - (cPiece * (cWinning - 1))) + (360f * cTurns) -> {
                    mOnLotteryFinishListener.onFinish(getLottery())
                }
            }
            invalidate()
        }
        animator.start()
    }

    private lateinit var prize: ArrayList<PrizeEntity>

    fun setPrizeList(prize: ArrayList<PrizeEntity>) {
        mSize = prize.size
        this.prize = ArrayList()
        this.prize = prize
        cPiece = 360f / mSize
        //360f / mSize /2 为了指针正中奖品中心
        cPieceMiddleAngel = cPiece / 2
        invalidate()
    }

    fun setPrizeCheck(cWinning: Int) {
        this.cWinning = cWinning
        invalidate()
    }

    /**
     * 根据旋转的角度反推中奖的结果
     * */
    private fun getLottery(): Int {
        return cWinning
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawColor(Color.parseColor("#83AF9B"))
        //画外圆
        canvas?.drawCircle(mCenterX, mCenterY, mRadius, mPaint)
        val mRectF = RectF()
        mRectF.top = width * 0.25f
        mRectF.left = height * 0.25f
        mRectF.right = width * 0.75f
        mRectF.bottom = height * 0.75f
        for (index in 0 until mSize) {
            //角度偏移量
            val degree = (cPiece * index) + cRotateDegree
            //求外圆上对应边界的点坐标
            val x = mCenterX + mRadius * cos(degree * PI / 180).toFloat()
            val y = mCenterY + mRadius * sin(degree * PI / 180).toFloat()
            //求内圆上对应边界的点坐标
            val cInnerTextTextX = mCenterX + mInnerRadius * cos(degree * PI / 180).toFloat()
            val cInnerTextY = mCenterY + mInnerRadius * sin(degree * PI / 180).toFloat()
            //依次画线
            canvas?.save()
            //默认旋转-90° 方便计算指针指向奖品正中间
            canvas?.rotate(-90f - cPieceMiddleAngel, mCenterX, mCenterY)
            canvas?.drawLine(cInnerTextTextX, cInnerTextY, x, y, mPaint)
            //设置path
            val mPath = Path()
            mPath.addArc(mRectF, degree, cPiece)
            canvas?.drawTextOnPath(
                prize[index].name.toString(),
                mPath,
                0f,
                0f,
                mTextPaint
            )
            // drawTextOnPath是参考下面这部分来的思路
//            canvas?.drawArc(
//                mRectF,
//                degree + cPiece / 4,
//                cPieceMiddleAngel,
//                true,
//                mTextPaint
//            )
            canvas?.restore()
        }
        val cCenterMetrics = mCenterTextPaint.fontMetrics
        canvas?.drawText(
            "抽奖",
            mCenterX - (mCenterTextPaint.measureText("抽奖") / 2),
            mCenterY + (cCenterMetrics.descent - cCenterMetrics.ascent) / 2 - cCenterMetrics.descent,
            mCenterTextPaint
        )
        //画内圆
        canvas?.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint)
        val lotteryCheckedMap =
            BitmapFactory.decodeResource(mContext.resources, R.mipmap.icon_lottery_check)
        val bitmapWidth = lotteryCheckedMap.width
        val bitmapHeight = lotteryCheckedMap.height

        canvas?.drawBitmap(
            lotteryCheckedMap,
            mCenterX - bitmapWidth / 2,
            mCenterY - (bitmapHeight * 0.79f) - mInnerRadius,
            mPaint
        )//画指向三角形Res图片
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mCenterX = width / 2.toFloat()
        mCenterY = height / 2.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureSpec(suggestedMinimumWidth, widthMeasureSpec),
            measureSpec(suggestedMinimumWidth, widthMeasureSpec)
        )
    }

    private fun measureSpec(minValue: Int, spec: Int): Int {
        var tempValue = minValue
        val model = getMode(spec)
        val size = getSize(spec)
        when (model) {
            AT_MOST -> {
                tempValue = Util.dpToPx(300)
            }
            EXACTLY -> {
                tempValue = size
            }
            UNSPECIFIED -> {
                tempValue = Util.dpToPx(300)
            }
        }
        return tempValue
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startRotate()
            }
        }

        return super.onTouchEvent(event)
    }

    fun setOnLotteryFinishListener(mOnLotteryFinishListener: OnLotteryFinishListener) {
        this.mOnLotteryFinishListener = mOnLotteryFinishListener
    }

    interface OnLotteryFinishListener {
        fun onFinish(index: Int)
    }
}