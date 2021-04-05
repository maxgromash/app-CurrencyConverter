package com.mscorp.exchange

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mscorp.exchange.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var currentSend: Double = 76.44
    private var currentGet: Double = 1.0
    private var rates: HashMap<String, Double>? = null
    private var singleItems = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPrefs()


        viewModel = MainViewModel(applicationContext)

        viewModel.getData().observe(this, {
            if (it?.isNullOrEmpty() == true) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                rates = it[0].rates
                currentGet = rates?.getValue(binding.textViewCurRateGet.text.toString())!!
                currentSend = rates?.getValue(binding.textViewCurRateSend.text.toString())!!
                singleItems = rates!!.keys.toTypedArray()
                singleItems.sort()
            }
        })

        setupListeners()
    }

    private fun getPrefs(){
        val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        val send = prefs.getString("send", "RUB")
        val get = prefs.getString("get", "EUR")

        binding.textViewCurRateGet.text = get
        binding.textViewCurRateSend.text = send
    }

    private fun updateGet() {
        if (!binding.editTextSend.text.isNullOrBlank())
            binding.editTextGet
                .setText(
                    String.format(
                        Locale.US,
                        "%.2f",
                        binding.editTextSend.text.toString().toDouble() / (currentSend / currentGet)
                    )
                )
    }

    private fun updateSend() {
        if (!binding.editTextGet.text.isNullOrBlank())
            binding.editTextSend
                .setText(
                    String.format(
                        Locale.US,
                        "%.2f",
                        binding.editTextGet.text.toString()
                            .toDouble() / (currentGet / currentSend)
                    )
                )
    }

    private fun setupListeners() {

        binding.editTextSend.setOnEditorActionListener { v, keyCode, event ->
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                binding.editTextSend.clearFocus()
                updateGet()
            }
            false
        }

        binding.editTextGet.setOnEditorActionListener { v, keyCode, event ->
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                binding.editTextGet.clearFocus()
                updateSend()
            }
            false
        }

        binding.textViewCurRateGet.setOnClickListener {
            onRateChooseClicked(Type.GET)
        }

        binding.imageViewRatesNamesGet.setOnClickListener {
            onRateChooseClicked(Type.GET)
        }

        binding.imageViewRatesNamesSend.setOnClickListener {
            onRateChooseClicked(Type.SEND)
        }

        binding.textViewCurRateSend.setOnClickListener {
            onRateChooseClicked(Type.SEND)
        }

    }

    private fun onRateChooseClicked(type: Type) {
        if (rates?.isNotEmpty() == true) {
            //Тут использовался !!, так как не работает smartcast с HashMap
            val startCheckedItem: Int
            if (type == Type.GET)
                startCheckedItem = singleItems.indexOf(binding.textViewCurRateGet.text.toString())
            else
                startCheckedItem = singleItems.indexOf(binding.textViewCurRateSend.text.toString())

            MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Choose a currency")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("OK") { dialog, which ->

                    val lw: ListView = (dialog as AlertDialog).listView
                    val checkedItem = lw.adapter.getItem(lw.checkedItemPosition) as String

                    when (type) {
                        Type.SEND -> {
                            binding.textViewCurRateSend.text = checkedItem
                            currentSend = rates!!.getValue(checkedItem)
                            updateSend()
                        }
                        Type.GET -> {
                            binding.textViewCurRateGet.text = checkedItem
                            currentGet = rates!!.getValue(checkedItem)
                            updateGet()
                        }
                    }
                }
                .setSingleChoiceItems(singleItems, startCheckedItem, null)
                .show()

        } else
            Toast.makeText(this, "Data is loading", Toast.LENGTH_SHORT).show()
    }

    enum class Type {
        SEND, GET
    }

    override fun onStop() {
        super.onStop()
        val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
        editor.putString("send", binding.textViewCurRateSend.text.toString())
        editor.putString("get", binding.textViewCurRateGet.text.toString())
        editor.apply()
    }


    companion object {
        const val MY_PREFS_NAME ="LastRates"
    }
}