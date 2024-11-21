package sdk.chat.app.xmpp.telco

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import sdk.chat.core.session.ChatSDK
import sdk.chat.demo.xmpp.R
import sdk.chat.ui.activities.BaseActivity
import sdk.guru.common.RX
import java.util.concurrent.TimeUnit


class BrilliantOTPVerificationActivity: BaseActivity(), OTPListener {

    private var otp: OtpTextView? = null
    private var verifyButton: Button? = null
    private var sentTo: TextView? = null
    private var resendCode: TextView? = null
    private var changeNumber: TextView? = null
    private var tvFailure: TextView? = null
    private var phoneNumber: String? = null
    private var timerDispoable: Disposable? = null
    private var otpDispoable: Disposable? = null
    private var currentTime = 60
    private var otpRequested = false
    private var resendCount = 0

    override fun getLayout(): Int {
        return R.layout.activity_brilliant_otp_verification
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneNumber = intent.getStringExtra("phone-number")

        otp = findViewById(R.id.otpView)
        verifyButton = findViewById(R.id.verifyButton)
        tvFailure = findViewById(R.id.tvFailure)
        sentTo = findViewById(R.id.tvHint)
        sentTo?.text = String.format(getString(R.string.code_has_been_sent__), phoneNumber)
        resendCode = findViewById(R.id.tvCountdown)

        changeNumber = findViewById(R.id.tvChangeNumber)
        changeNumber?.setOnClickListener {
            changeNumber()
        }

        verifyButton?.setOnClickListener {
            next()
        }

        findViewById<ImageView>(R.id.backArrow)?.setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.backText)?.setOnClickListener {
            finish()
        }

        validate()

        otp?.otpListener = this


        if (phoneNumber != null) {
            sendOTP()
        } else {
            finish()
        }

    }

    fun next() {
        if (Brilliant.shared().api().verifyOTP(otp?.otp)) {
            // Register the user
            phoneNumber?.let {
                Brilliant.shared().api.register(it, "123").observeOn(RX.main()).subscribe({
                    startNextActivity()
                }, {
                    it.message?.let { message ->
                        showToast(message)
                    }
                })
            }
        }
    }

//    fun startNextActivity(phoneNumber: String) {
//        phoneNumber?.let {
//            Brilliant.shared().api.registerToFreeswitch(it).observeOn(RX.main()).subscribe({
//                startNextActivity()
//            }, {
//                it.message?.let { message ->
//                    showToast(message)
//                }
//            })
//        }
//    }

    fun startNextActivity() {
        val intent = Intent(this, BrilliantIntroActivity::class.java)
        ChatSDK.ui().startActivity(this, intent)
    }

    fun sendOTP() {
        phoneNumber?.substring(1).orEmpty().let {
            otpRequested = true
            otpDispoable?.dispose()
            startTimer()
            if (Brilliant.shared().debug) {
                Brilliant.shared().api().otp = "0000"
            } else {
                otpDispoable = Brilliant.shared().api().sendOTP(it).observeOn(RX.main()).subscribe {

                }
            }
        }
    }

    fun startTimer() {
        timerDispoable?.dispose()
        currentTime = 60
        timerDispoable = Observable.interval(1, TimeUnit.SECONDS).observeOn(RX.main()).subscribe {
            currentTime -= 1
            updateTimerText()
            if (currentTime < 0) {
                timerFinished()
            }
        }
        updateTimerText()
    }

    fun updateTimerText() {
        if (otpRequested) {
            resendCode?.visibility = VISIBLE
            if (timerDispoable != null) {
                resendCode?.text = toHTML(String.format(getString(R.string.resend__), "<font color='#FB7066'>$currentTime</font>"))
                resendCode?.setOnClickListener {}
            } else {
                resendCode?.text = getString(R.string.resend_otp)
                resendCode?.setOnClickListener {
                    resendCode()
                }
            }
        } else {
            resendCode?.visibility = INVISIBLE
        }
    }

    fun toHTML(text: String): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            return Html.fromHtml(text)
        }
    }

    fun timerFinished() {
        timerDispoable?.dispose()
        timerDispoable = null
        updateTimerText()
    }

    fun validate() {
        val valid = phoneNumber != null && Brilliant.shared().api().verifyOTP(otp?.otp)
        verifyButton?.isEnabled = valid
        verifyButton?.alpha = if(valid) 1.0f else 0.5f
    }

    fun otpRequested() {
        otpDispoable?.dispose()
        otpDispoable = null
    }

    fun updateBottomText() {
        if (resendCount > 0) {
            changeNumber?.visibility = INVISIBLE
            tvFailure?.visibility = VISIBLE
        } else {
            changeNumber?.visibility = VISIBLE
            tvFailure?.visibility = INVISIBLE
        }
    }

    fun resendCode() {
        otp?.setOTP("")
        resendCount += 1
        updateBottomText()
        otpDispoable?.dispose()
        otpRequested = true
        startTimer()
        otpDispoable = Brilliant.shared().api().resendOTP().observeOn(RX.main()).subscribe {

        }
    }

    fun changeNumber() {
        finish()
    }

    override fun onInteractionListener() {
        validate()
    }

    override fun onOTPComplete(otp: String) {
    }

}