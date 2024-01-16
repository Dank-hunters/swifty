package com.example.swiftycompanion


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.Integer.min
import java.util.UUID

object RetrofitClient {
    val instance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl("https://api.intra.42.fr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton = findViewById<Button>(R.id.button_connect)
        connectButton.setOnClickListener(View.OnClickListener {
            val authUrl =
                "https://api.intra.42.fr/oauth/authorize?client_id=$Cliendid&redirect_uri=${
                    Uri.encode(redirectUri)
                }&response_type=code&state=${Uri.encode(state)}"
            println("L AUTHURL 1 : $authUrl")

            val intent = Intent(applicationContext, ConnectionActivity::class.java)
            intent.putExtra("AUTH_URL", authUrl!!)
            startActivity(intent)
        })
    }
}


/*  setContentView(R.layout.activity_connection_42)
      val webView: WebView = findViewById(R.id.webview2)
      webView.settings.javaScriptEnabled = true
      webView.webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
              if (url != null && url.startsWith(redirectUri)) {
                  val uri = Uri.parse(url)
                  code = uri.getQueryParameter("code").toString()
                  setContentView(R.layout.activity_page42)
                  // obtention d une instance retrofit avec mon objet RetrofitClient
                  val oauthService = RetrofitClient.instance.create(OauthService::class.java)
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
                  res.enqueue(object : retrofit2.Callback<ResponseBody> {
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
                                      gson.fromJson(responseString, TokenResponse::class.java)
                                  token = responseModel.access_token
                              } catch (e: Exception) {
                                  e.printStackTrace()
                              }
                              val intent = Intent(applicationContext, AffActivity::class.java)
                              intent.putExtra("TOKEN_KEY", token!!)
                              startActivity(intent)

                              //staryt Affactivity

                             /* getMyInfo(token)
                              val searchedButton = findViewById<Button>(R.id.SearchButton)
                              searchedButton.setOnClickListener()
                              {
                                  searchedSomeUser(token)
                              }

                              val userButton = findViewById<Button>(R.id.button)
                              userButton.setOnClickListener(View.OnClickListener
                              {
                                  getMyInfo(token)
                              }) */
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
      }
      webView.loadUrl(authUrl)*/

/*  fun getMyInfo(token: String) {

      val userInfoServices = RetrofitClient.instance.create(MyInfosServices::class.java)
      val call = userInfoServices.getMyInfos("Bearer $token")
      var myId: Int
      myId = 0

      call.enqueue(object : retrofit2.Callback<ResponseBody> {
          override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
              if (response.isSuccessful) {
                  val res = response.body()!!.string()
                  println(res)
                  val gson = Gson()
                  val responseModelForProfile = gson.fromJson(res, UserResponse::class.java)
                  myId = responseModelForProfile.id
                  var index = 0
                  while (index < responseModelForProfile.cursus_users.size) {
                      println(responseModelForProfile.cursus_users[index].skills)
                      index++

                  }
                  getProjectUsers(myId, 0, responseModelForProfile, token)

              } else {
                  val errorMsg = response.errorBody()?.string()
                  Toast.makeText(applicationContext, "Erreur : ${errorMsg}", Toast.LENGTH_SHORT)
                      .show()
              }
          }

          override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
              Toast.makeText(
                  applicationContext,
                  "Bof J ai pas reussi a choper les info perso frero",
                  Toast.LENGTH_SHORT
              ).show()
              println("FAILED TO RECIEVED MY INFOS")
          }
      })


  }

  fun getProjectUsers(id : Int, page : Int = 1, responseToPrint : UserResponse, token: String, listResponse: MutableList<ProjectDescription> = mutableListOf())
  {
      val userProjectServices =
          RetrofitClient.instance.create(MyInfosServices::class.java)
      println("MY ID : ${id}")
      val call2 = userProjectServices.getUserProject("Bearer $token", id, page)

      call2.enqueue(object : retrofit2.Callback<ResponseBody> {
          override fun onResponse(
              call: Call<ResponseBody>,
              response: Response<ResponseBody>
          ) {
              if (response.isSuccessful) {
                  val result = response.body()!!.string()

                  println("mes inf0os de projet")
                  val maxLogSize = 1000
                  for (i in 0..result.length / maxLogSize) {
                      val start = i * maxLogSize
                      val end = (i + 1) * maxLogSize
                      println(result.substring(start, min(end, result.length)))
                  }
                  //   println(result)
                  println("FIN")
                  val gson = Gson()
                  val responseModel =
                      gson.fromJson(result, Array<ProjectResponse>::class.java)
              //    var ListResponse: MutableList<ProjectDescription> = mutableListOf()
                  if (responseModel.isNotEmpty()) {
                      println("LA PAGE : $page")
                      val projectsWithCursus21 = responseModel.filter { 21 in it.cursus_ids }
                      projectsWithCursus21.forEach{project ->
                          project.project.final_mark = project.final_mark
                          listResponse.add(project.project)
                      }
                      getProjectUsers(id, page + 1, responseToPrint, token, listResponse)
                      println("Encore un appel de cette merde")
                  }

                      printBasicInfos(responseToPrint, listResponse)

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
              println("FAIL ON USER PROJET GETTER")
          }
      })

  }

  fun searchedSomeUser(token: String) {
      val loginSearched =
          findViewById<EditText>(R.id.LoginSearched).text.toString()
      val userInfoServices =
          RetrofitClient.instance.create(MyInfosServices::class.java)
      val call = userInfoServices.getUserExist("Bearer $token", loginSearched)
      call.enqueue(object : retrofit2.Callback<ResponseBody> {
          override fun onResponse(
              call: Call<ResponseBody>,
              response: Response<ResponseBody>
          ) {
              if (response.isSuccessful) {
                  var res = response.body()!!.string()

                  println(res)
                  val gson = Gson()
                  val responseModel =
                      gson.fromJson(res, Array<UserResponse>::class.java)
                  val first = responseModel.firstOrNull()
                  //check si le tableau est vide !!!!

                  println(first)
                  if (responseModel.isEmpty()) {
                      //login not fund
                      Toast.makeText(
                          applicationContext,
                          "Login ${loginSearched} does not exist",
                          Toast.LENGTH_SHORT
                      ).show()
                  } else {
                      GetInfo(responseModel[0].id, token)
                  }
              } else {
                  val errorMsg = response.errorBody()?.string()
                  Toast.makeText(applicationContext, "Erreur : ${errorMsg}", Toast.LENGTH_SHORT)
                      .show()
              }
          }

          override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
              Toast.makeText(applicationContext, "response failled", Toast.LENGTH_SHORT).show()
          }
      })
  }



  fun printBasicInfos(responseModel: UserResponse, Project : List<ProjectDescription>?) {
      println("LE JSON DE REPONSE :")
      println("------------------------------------")
      println(Project)
      println("------------------------------------")

      val userCursus = responseModel.cursus_users?.find { it.grade == "Member" }
      val level: Double? = userCursus?.level
      val email = responseModel.email
      val image = responseModel.image.link
      val name = responseModel.first_name + " " + responseModel.last_name
      val wallet = responseModel.wallet
      val imageView = findViewById<ImageView>(R.id.imageView2)
      Picasso.get()
          .load(image)
          .into(imageView)
      //profile information
      var nameView = findViewById<TextView>(R.id.NameView)
      var emailView = findViewById<TextView>(R.id.EmailView)
      var levelView = findViewById<TextView>(R.id.LevelView)
      var walletView = findViewById<TextView>(R.id.WalletView)
      nameView.text = "Name  : $name"
      emailView.text = "Email   : $email"
      walletView.text = "Wallet  : $wallet"
      levelView.text = "Level   : " + level.toString()

      //project information
      var listProject = findViewById<ListView>(R.id.ListProject)

      val adapterForProject = PostAdapter(this, R.layout.project_list_view, ArrayList(Project))
      listProject.adapter = adapterForProject
      //skills information
      if (userCursus != null) {
          var listSkills = findViewById<ListView>(R.id.ListSkills)

          val adapterForSkill =
              PostAdapter.PostSkillsAdapter(this, R.layout.skills_list_view, userCursus.skills)
          listSkills.adapter = adapterForSkill
      }
      }

  fun GetInfo(studentId: Int, token: String) {
      //serched information about user with his login
      val userInfoServices =
          RetrofitClient.instance.create(MyInfosServices::class.java)
      val call = userInfoServices.getUserInfos(
          "Bearer $token",
          studentId
      )
      call.enqueue(object : retrofit2.Callback<ResponseBody> {
          override fun onResponse(
              call: Call<ResponseBody>,
              response: Response<ResponseBody>
          ) {
              if (response.isSuccessful) {
                  val result = response.body()!!.string()
                  val gson = Gson()
                  val responseModel = gson.fromJson(result, UserResponse::class.java)
                  getProjectUsers(responseModel.id, 0, responseModel, token)
                //  printBasicInfos(responseModel, null)
              } else {
                  val errorMsg = response.errorBody()?.string()
                  Toast.makeText(applicationContext, "Erreur : ${errorMsg}", Toast.LENGTH_SHORT)
                      .show()
              }
          }
          override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
              Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
          }
      })
  }*/

