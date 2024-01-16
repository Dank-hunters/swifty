package com.example.swiftycompanion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
//import com.squareup.picasso.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.UUID
import com.example.swiftycompanion.BuildConfig


var token = ""
val Cliendid = BuildConfig.CLIENT_ID
val ClientSecret = BuildConfig.CLIENT_SECRET

val redirectUri = "https://www.google.com"
val state = UUID.randomUUID().toString()

var code = ""

class ConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authUrl = intent.getStringExtra("AUTH_URL")
        println("L AUTHURL 2 : ${authUrl}")
        setContentView(R.layout.activity_connection_42)
        val webView: WebView = findViewById(R.id.webview2)
        webView.settings.javaScriptEnabled = true
        webView.clearCache(true)
        webView.clearHistory()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies { succes ->
           if (succes) {

              //  runOnUiThread {
                    webView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            println("je suis pas entrer ;la dedans serieux ?")
                            if (url != null && url.startsWith(redirectUri)) {
                                val uri = Uri.parse(url)
                                code = uri.getQueryParameter("code").toString()
                                setContentView(R.layout.activity_page42)
                                // obtention d une instance retrofit avec mon objet RetrofitClient
                                val oauthService =
                                    RetrofitClient.instance.create(OauthService::class.java)
                                // appel api pour le token d acces
                                val res = oauthService.getAccessToken(
                                    "authorization_code",
                                    Cliendid,
                                    ClientSecret,
                                    code,
                                    redirectUri,
                                    state
                                )
                                //execution de l  appel API
                                res.enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        if (response.isSuccessful) {
                                            val responseString = response.body()!!.string()
                                            try {
                                                // Recuperation des info du json pour le token
                                                val gson = Gson()
                                                val responseModel =
                                                    gson.fromJson(
                                                        responseString,
                                                        TokenResponse::class.java
                                                    )
                                                token = responseModel.access_token
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            val intent =
                                                Intent(applicationContext, AffActivity::class.java)
                                            intent.putExtra("TOKEN_KEY", token!!)
                                            startActivity(intent)
                                            println("j ai le token ? : $token")
                                            finish()

                                        } else {
                                            val errorMsg = response.errorBody()?.string()
                                            Toast.makeText(
                                                applicationContext,
                                                "Erreur : ${errorMsg}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        println("yo ca marche pas du tout la frere revois ta ligne de querry")
                                        Toast.makeText(
                                            applicationContext,
                                            "OKAY PAS FOU",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })

                                return true
                            }
                            return false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            println("l erreur frr $error")
                        }

                    }

                    println("je suis deja la ?")
                    webView.loadUrl(authUrl!!)

                }

            }


//        }

        cookieManager.flush()

    }
}
