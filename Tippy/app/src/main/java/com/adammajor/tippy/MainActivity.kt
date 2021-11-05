package com.adammajor.tippy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import java.math.BigDecimal

//whenever you have any type of logging, the "TAG" is generally the classname
private const val TAG = "MainActivity"

public enum class RATINGS {ONE, TWO, THREE, FOUR, FIVE}
private val INITIAL_TIP_PERCENT = 15;
private val TIP_DESCRIPTIONS = mapOf<RATINGS, String>(
    RATINGS.ONE to "Abominable",
    RATINGS.TWO to "Poor",
    RATINGS.THREE to "Average",
    RATINGS.FOUR to "Great",
    RATINGS.FIVE to "Amazing",
)
private val TIP_RANGES = mapOf<RATINGS, List<Int>>(
    RATINGS.ONE to listOf<Int>(0, 5),
    RATINGS.TWO to listOf<Int>(6, 10),
    RATINGS.THREE to listOf<Int>(11, 20),
    RATINGS.FOUR to listOf<Int>(21, 30),
    RATINGS.FIVE to listOf<Int>(31, 50),
)

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
    }

    private fun setInitialValues() {
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        tvTipDescription.text = TIP_DESCRIPTIONS[RATINGS.ONE]
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
                changeTipDescription()
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
                changeTipDescription()
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
    private fun changeTipDescription() {
        //Figure out which value to use
    }
}