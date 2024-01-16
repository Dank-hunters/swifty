package com.example.swiftycompanion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String
)

data class LoginResponse (
    @SerializedName("status_code")
    var statusCode: Int,

    @SerializedName("auth_token")
    var authToken: String,

    @SerializedName("user")
    var user: User
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val created_at: Long,
    val secret_valid_until: Long
)

data class CursusUser(
    var grade: String?,
    val level: Double,
    val skills: List<Skill>
)
data class Skill(
    val id: Int,
    val name: String,
    val level: Double
)

data class ImageInfo(
    val link : String
)
data class UserResponse (
    val id : Int,
    val email : String,
    val login : String,
    val first_name : String,
    val last_name : String,
    val wallet : Int,
    val cursus_users : List<CursusUser>,
    val image : ImageInfo
)

data class CursusProject(
    val id: Int,
    val projects: List<ProjectResponse>
    // Autres champs si nécessaires...
)

data class ProjectResponse(
    val id : String,
    val validated: Boolean,
    val status : String,
    val project : ProjectDescription,
    val final_mark: Int,
    val cursus_ids: List<Int>
)

data class ProjectDescription(
    var id: Int,
    val name: String,
    var final_mark : Int,
    val slug : String
)

class PostAdapter(
    var mcontext : Context,
    var resource : Int,
    var values: ArrayList<ProjectDescription>,
): ArrayAdapter<ProjectDescription>(mcontext, resource, values) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val project = values[position]
        val projectView =
            convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val ProjectName = projectView.findViewById<TextView>(R.id.NameProjectView)
        val ProjectLevel = projectView.findViewById<TextView>(R.id.LevelProjectView)
        println("petit debugg heinon vas voir le ame : " + project.final_mark)

        ProjectName?.text = project.name
        ProjectLevel?.text = project.final_mark.toString()
            return projectView
    }

    class PostSkillsAdapter(
        var mcontext: Context,
        var resource: Int,
        var values: List<Skill>
    ) : ArrayAdapter<Skill>(mcontext, resource, values) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val project = values[position]
            val projectView =
                convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            val ProjectName = projectView.findViewById<TextView>(R.id.NameSkillsView)
            val ProjectLevel = projectView.findViewById<TextView>(R.id.LevelSkillsView)
            println("petit debugg heinon vas voir le ame : " + project.name)

            ProjectName?.text = project.name
            ProjectLevel?.text = project.level.toString()
            return projectView
        }

    }
}