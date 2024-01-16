package com.example.swiftycompanion

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class AffActivity : AppCompatActivity() {
    override fun onCreate(savedInstaceState: Bundle?) {
        super.onCreate(savedInstaceState)

        val token = intent.getStringExtra("TOKEN_KEY")
        setContentView(R.layout.activity_page42)
        getMyInfo(token)
        val searchedButton = findViewById<Button>(R.id.SearchButton)
        searchedButton.setOnClickListener()
        {
            searchedSomeUser(token)
        }

        val userButton = findViewById<Button>(R.id.button)
        userButton.setOnClickListener(
            View.OnClickListener
        {
            getMyInfo(token)
        })
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {

            finish()
        }
    }
    private fun  getMyInfo(token: String?) {

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
                    println(errorMsg)
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

    fun getProjectUsers(id : Int, page : Int = 1, responseToPrint : UserResponse, token: String?, listResponse: MutableList<ProjectDescription> = mutableListOf())
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
                        println(result.substring(start, Integer.min(end, result.length)))
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
                    println(errorMsg)

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("FAIL ON USER PROJET GETTER")
            }
        })

    }

    fun searchedSomeUser(token: String?) {
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
                        getInfo(responseModel[0].id, token)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(applicationContext, "Erreur : ${errorMsg}", Toast.LENGTH_SHORT)
                        .show()
                    println(errorMsg)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "response failled", Toast.LENGTH_SHORT).show()
            }
        })
    }



    @SuppressLint("SetTextI18n")
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
        val nameView = findViewById<TextView>(R.id.NameView)
        val emailView = findViewById<TextView>(R.id.EmailView)
        val levelView = findViewById<TextView>(R.id.LevelView)
        val walletView = findViewById<TextView>(R.id.WalletView)
        nameView.text = "Name  : $name"
        emailView.text = "Email   : $email"
        walletView.text = "Wallet  : $wallet"
        levelView.text = "Level   : " + level.toString()

        //project information
        val listProject = findViewById<ListView>(R.id.ListProject)

        val adapterForProject = PostAdapter(this, R.layout.project_list_view, ArrayList(Project))
        listProject.adapter = adapterForProject
        //skills information
        if (userCursus != null) {
            val listSkills = findViewById<ListView>(R.id.ListSkills)

            val adapterForSkill =
                PostAdapter.PostSkillsAdapter(this, R.layout.skills_list_view, userCursus.skills)
            listSkills.adapter = adapterForSkill
        }
    }

    fun getInfo(studentId: Int, token: String?) {
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

    }
}