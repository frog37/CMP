package com.example.layoutlearn

// 这是数据模型（Data Model）
// data class 的属性需要和服务器返回的 JSON 字段名对应
// Retrofit 结合 Gson 会自动将 JSON 里的数据填充到这些属性中
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)