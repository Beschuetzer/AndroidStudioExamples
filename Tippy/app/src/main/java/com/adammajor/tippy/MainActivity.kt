package com.adammajor.tippy

import android.animation.ArgbEvaluator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.pow

//whenever you have any type of logging, the "TAG" is generally the classname
private const val TAG = "MainActivity"

public enum class RATINGS {ONE, TWO, THREE, FOUR, FIVE}
private const val INITIAL_TIP_PERCENT = 15;
private const val MAX_TIP_PERCENT = 100
private val TIP_DESCRIPTIONS = mapOf<RATINGS, String>(
    RATINGS.ONE to "Abominable",
    RATINGS.TWO to "Poor",
    RATINGS.THREE to "Average",
    RATINGS.FOUR to "Great",
    RATINGS.FIVE to "Amazing",
)

private val TIP_RANGES_MIN = mapOf<RATINGS, Int>(
    RATINGS.ONE to 0,
    RATINGS.TWO to 6,
    RATINGS.THREE to 11,
    RATINGS.FOUR to 21,
    RATINGS.FIVE to 31,
)

private val TIP_RANGES_MAX = mapOf<RATINGS, Int>(
    RATINGS.ONE to (TIP_RANGES_MIN[RATINGS.TWO]!!.minus(1)),
    RATINGS.TWO to (TIP_RANGES_MIN[RATINGS.THREE]!!.minus(1)),
    RATINGS.THREE to (TIP_RANGES_MIN[RATINGS.FOUR]!!.minus(1)),
    RATINGS.FOUR to (TIP_RANGES_MIN[RATINGS.FIVE]!!.minus(1)),
    RATINGS.FIVE to MAX_TIP_PERCENT
)

private var TIP_COLORS = mapOf<RATINGS, Int>()


class MainActivity : AppCompatActivity() {

    //lateinit means we are initializing somewhere outside of the constructor
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //getting references to the "widgets"/controls/items in UI
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        setInitialValues()
        addSeekBarListener()
        addTipBaseListener()
        changeTipDescriptionColor(INITIAL_TIP_PERCENT)
        changeTipDescriptionWord(INITIAL_TIP_PERCENT)
    }

    private fun setInitialValues() {
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        tvTipDescription.text = TIP_DESCRIPTIONS[RATINGS.ONE]
        seekBarTip.max = MAX_TIP_PERCENT



        val subDividingColors = GetSubDividingColors("abcabc", "123abc").colors(3);
        TIP_COLORS = mapOf<RATINGS, Int>(
            RATINGS.ONE to ContextCompat.getColor(this, R.color.color_rating_worst),
            RATINGS.TWO to Color.parseColor(subDividingColors[0]),
            RATINGS.THREE to Color.parseColor(subDividingColors[1]),
            RATINGS.FOUR to Color.parseColor(subDividingColors[2]),
            RATINGS.FIVE to ContextCompat.getColor(this, R.color.color_rating_best)
        )

        //test cases
//        GetSubDividingColors("abcabc", "123abc");
//        GetSubDividingColors("#abcabc", "123abc");
//        GetSubDividingColors("abcabc", "#123abc");
//        GetSubDividingColors("#abcabc", "#123fbc");
//        GetSubDividingColors("abCabF", "123bcF");
//        GetSubDividingColors("#abcabc", "12Fabc");
//        GetSubDividingColors("abcabc", "#12Fabc");
//        GetSubDividingColors("#abcabc", "#123FbF");
    }

    private fun addSeekBarListener() {
        //adding event listener to seekBar change
        //by creating an anonymouse class that implements the interface
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Log.i sends as info log to Logcat (see panel on sidebar [integrated logging])
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                computeTipAndTotal()
                changeTipDescriptionWord(progress)
                changeTipDescriptionColor(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun addTipBaseListener() {
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }
        })
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text == null || etBaseAmount.text.toString() == "") {
            tvTotalAmount.text = ""
            tvTipAmount.text = ""
            return
        };

        //1.  Get the value of the base and tip percent
        var baseAmount = etBaseAmount.text.toString().toBigDecimal()
        var tipPercent = seekBarTip.progress.toBigDecimal()

        //2.  Compute the tip and total
        var tipAmount = baseAmount.multiply(tipPercent).divide(BigDecimal(100))
        var totalAfterTip = tipAmount + baseAmount
        Log.i(TAG, "baseAmount = $baseAmount")
        Log.i(TAG, "tipPercent = $tipPercent")
        Log.i(TAG, "tipAmount = $tipAmount")
        Log.i(TAG, "totalAfterTip = $totalAfterTip")

        //3.  Update UI
        tvTipAmount.text = String.format("%.2f", tipAmount);
        tvTotalAmount.text = String.format("%.2f", totalAfterTip);
    }

    private fun changeTipDescriptionWord(tipPercent: Int) {
        //using the when construct to assign values based on preference
        //the '!!' part goes after an expression to ensure Kotlin that the values provided will not be null (we are defining them above)
        val descriptionToUse = when (tipPercent) {
            in TIP_RANGES_MIN[RATINGS.ONE]!!..TIP_RANGES_MAX[RATINGS.ONE]!! -> TIP_DESCRIPTIONS[RATINGS.ONE]
            in TIP_RANGES_MIN[RATINGS.TWO]!!..TIP_RANGES_MAX[RATINGS.TWO]!! -> TIP_DESCRIPTIONS[RATINGS.TWO]
            in TIP_RANGES_MIN[RATINGS.THREE]!!..TIP_RANGES_MAX[RATINGS.THREE]!! -> TIP_DESCRIPTIONS[RATINGS.THREE]
            in TIP_RANGES_MIN[RATINGS.FOUR]!!..TIP_RANGES_MAX[RATINGS.FOUR]!! -> TIP_DESCRIPTIONS[RATINGS.FOUR]
            else -> TIP_DESCRIPTIONS[RATINGS.FIVE]
        }

        tvTipDescription.text = descriptionToUse
    }

    private fun changeTipDescriptionColor(tipPercent: Int) {
        //region quantum coloring (changes at breakpoints)
        val colorToUse = when (tipPercent) {
            in TIP_RANGES_MIN[RATINGS.ONE]!!..TIP_RANGES_MAX[RATINGS.ONE]!! -> TIP_COLORS[RATINGS.ONE]
            in TIP_RANGES_MIN[RATINGS.TWO]!!..TIP_RANGES_MAX[RATINGS.TWO]!! -> TIP_COLORS[RATINGS.TWO]
            in TIP_RANGES_MIN[RATINGS.THREE]!!..TIP_RANGES_MAX[RATINGS.THREE]!! -> TIP_COLORS[RATINGS.THREE]
            in TIP_RANGES_MIN[RATINGS.FOUR]!!..TIP_RANGES_MAX[RATINGS.FOUR]!! -> TIP_COLORS[RATINGS.FOUR]
            in TIP_RANGES_MIN[RATINGS.FIVE]!!..TIP_RANGES_MAX[RATINGS.FIVE]!! -> TIP_COLORS[RATINGS.FIVE]
            else -> "0x000000"
        }

        tvTipDescription.setTextColor(colorToUse as Int);

        //region if you want interpolated coloring
//        Log.i(TAG, "tipPercent = ${tipPercent.toFloat()}");
//        //ArgbEvaluator() use interpolation to get an rgba value between two rgba values using a percent (0 - 1) as float
//        val colorToUse = ArgbEvaluator().evaluate(
//            tipPercent.toFloat() / seekBarTip.max,
//            ContextCompat.getColor(this, R.color.color_worst),
//            ContextCompat.getColor(this, R.color.color_best)
//        )
//        tvTipDescription.setTextColor(colorToUse as Int)
        //endregion
    }

}