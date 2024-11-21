package sdk.chat.app.xmpp.telco

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import sdk.chat.core.session.ChatSDK
import sdk.chat.demo.xmpp.R
import kotlin.random.Random

class BrilliantAPI {

    var otp: String? = null
    var to: String? = null
    var url = "https://appsrv.intercloud.com.bd/test/api/VendorOTP/SendOTP"
    var xmppURL = "http://103.209.42.15:5443/api/register"//"http://36.255.71.143:5443/api/register"
    var freeswitchURL = "http://103.248.13.73:5070"

    public fun sendOTP(to: String): Completable {
        this.otp = Random.nextInt(1000, 10000).toString()
        Log.d( "sendOTP: ", this.otp!!)
        this.to = to
        return resendOTP()
    }

    public fun resendOTP(): Completable {
        this.otp?.let {otp ->
            this.to?.let { to ->
                return this.impl_sendOTP(to, otp)
            }
        }
        return Completable.error(Exception(ChatSDK.getString(R.string.otp_or_phone_invalid)))
    }

    public fun verifyOTP(reply: String?): Boolean {
        return reply == this.otp
    }

    public fun clearOTP() {
        otp = null
    }

    fun impl_sendOTP(to: String, otp: String): Completable {
        return Completable.create {

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val json = """
            {
                "from": "TelcoBright",
                "to": "$to",
                "Content": "${String.format(ChatSDK.getString(R.string.your_otp_is__), otp)}",
            }
            """.trimIndent()
            val body = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("login", "TelcoBright")
                .addHeader("password", "IO&3(DF&")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    it.onError(IOException("Unexpected code $response"))
                } else {
                    it.onComplete()
                }
            }
        }
    }

    fun register(user: String, password: String): Completable {
        return Completable.create {

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            var json =
                JSONObject(mapOf("user" to user, "host" to "localhost", "password" to password))
//            val body = json.toRequestBody(mediaType)
            val body = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
//                .header("Content-Type", "application/json")
                .url(xmppURL)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                // 10090 is already registered code

//                 TODO: Testing

                if (!response.isSuccessful && response.code != 10090 && response.code != 409) {
                    it.onError(IOException("Unexpected code $response"))
                } else {
//                     Cache the credentials
                    ChatSDK.shared().keyStorage.save(user, password)

                    it.onComplete()
                }
            }
        }
    }

    fun registerToFreeswitch(user_id: String): Completable {
        return Completable.create {

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            var json = JSONObject(mapOf("user_id" to user_id))
//            val body = json.toRequestBody(mediaType)
            val body = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
//                .header("Content-Type", "application/json")
                .url(freeswitchURL + "/createXmlUserProfile")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody)

                val status = jsonResponse.optString("status")
                val did = jsonResponse.optString("did")
                if (responseBody != null) {
                    Log.i("registerToFreeswitch: ", responseBody)
                }
                if (!response.isSuccessful && response.code != 302 && response.code != 201) {
                    it.onError(IOException("Unexpected code $response"))
                } else if (status == "success") {
                    ChatSDK.shared().keyStorage.put("fs_user_id", did)

                    val get = ChatSDK.shared().getKeyStorage().get("fs_user_id")
                    it.onComplete()
                } else {
                    it.onError(IOException("Unexpected code $response"))
                }
            }
        }
    }
    fun addBalance(userId: String, amount: Double): Completable {
        return Completable.create { emitter ->

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val json = JSONObject(mapOf("userId" to userId, "amount" to amount))
            val body = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$freeswitchURL/topup")
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        emitter.onComplete()
                    } else {
                        val errorBody = responseBody ?: "Unknown error"
                        emitter.onError(IOException("Unexpected code: ${response.code}, body: $errorBody"))
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun checkBalance(userId: String): Single<String> {
        return Single.create { emitter ->

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val json = JSONObject(mapOf("userId" to userId))
            val body = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$freeswitchURL/check-balance")
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        emitter.onSuccess(responseBody)
                    } else {
                        val errorBody = responseBody ?: "Unknown error"
                        emitter.onError(IOException("Unexpected code: $response.code, body: $errorBody"))
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}