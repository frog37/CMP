package com.example.layoutlearn

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// 1. 定义 API 接口
// Retrofit 的精髓在于把 REST API 请求转化为原生的 Kotlin/Java 接口
interface ApiService {

    // @GET 注解表示这是一个 HTTP GET 请求
    // "posts" 是基于 BaseUrl 的相对路径
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    // @Path 注解可以用来动态替换 URL 路径中的变量
    // 当调用 getPostById(1) 时，最终请求的地址会是 ".../posts/1"
    @GET("posts/{id}")
    fun getPostById(@Path("id") postId: Int): Call<Post>
}