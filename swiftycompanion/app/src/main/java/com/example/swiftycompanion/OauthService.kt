package com.example.swiftycompanion

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface OauthService {
    @POST("oauth/token")
    fun getAccessToken(
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("state") state: String
    ): Call<ResponseBody>
}

interface MyInfosServices {
    @GET("v2/me")
    fun getMyInfos(
        @Header("Authorization") accessToken: String
    ): Call<ResponseBody>

    @GET("v2/me/projects")
    fun getMyProject(
        @Header("Authorization")
        accessToken: String
    ): Call<ResponseBody>

    @GET("/v2/users")
    fun getUserExist(
        @Header("Authorization")
        accessToken: String,
        @Query("filter[login]") user: String
    ): Call<ResponseBody>

    @GET("/v2/users/{id}")
    fun getUserInfos(
        @Header("Authorization")
        accessToken: String,
        @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("v2/users/{id}/projects_users")
    fun getUserProject(
        @Header("Authorization")
        accessToken: String,
        @Path("id") id : Int,
        @Query("page") page: Int
    ): Call<ResponseBody>

}

/*Dans cet exemple, @Query("filter") est utilisé pour ajouter un paramètre de requête filter
à l'URL. La valeur de user sera utilisée comme valeur pour ce paramètre. L'URL finale ressemblera
à /v2/users?filter=valeur_de_user.

Assurez-vous que le nom du paramètre de requête (filter dans cet exemple) correspond à ce que
 votre API attend. Si vous n'êtes pas sûr du nom exact du paramètre, vous devrez le vérifier
  dans la documentation de l'API que vous utilisez.






*/

