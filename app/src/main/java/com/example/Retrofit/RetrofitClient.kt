package com.example.layoutlearn

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 2. 创建 Retrofit 客户端（通常使用单例模式，因为Retrofit实例非常消耗资源）
object RetrofitClient {
    // Base URL 是服务器的根地址，所有的接口都会拼接到它后面
    // 注意：Retrofit 要求 baseUrl 必须以 "/" 结尾
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // lazy 保证了 instance 只在第一次被调用时才初始化
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 添加 Gson 转换器，让 Retrofit 自动把 JSON 文本解析成 Post 等数据类
            .addConverterFactory(GsonConverterFactory.create()) 
            .build()
        
        // create() 方法会利用动态代理，自动生接口 ApiService 的实现类
        retrofit.create(ApiService::class.java)
    }
}