package com.example.layoutlearn

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 3. 在 Activity 中使用 Retrofit 发起请求
class RetrofitExampleActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 为了方便演示，这里直接用代码创建一个 TextView 用来展示结果
        resultTextView = TextView(this)
        resultTextView.textSize = 16f
        resultTextView.setPadding(32, 32, 32, 32)
        setContentView(resultTextView)

        resultTextView.text = "正在请求网络数据，请稍候..."

        // 开始获取数据
        fetchData()
    }

    private fun fetchData() {
        // 1. 通过 RetrofitClient 获取 Call 对象
        // Call 对象代表了一次即将被执行的网络请求
        val call = RetrofitClient.instance.getPostById(1)

        // 2. enqueue 表示“异步执行”，并把任务加入队列。这不会阻塞主线程！
        // 因为 Android 不允许在主线程（UI线程）上发起网络请求
        call.enqueue(object : Callback<Post> {
            
            // 请求收到服务器响应时回调
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                // response.isSuccessful 意味着 HTTP 状态码在 200-300 之间
                if (response.isSuccessful) {
                    // body() 就是我们通过 Gson 解析好的 Post 对象
                    val post: Post? = response.body()
                    
                    val resultText = "请求成功！\n\nID: ${post?.id}\n标题: ${post?.title}\n内容: ${post?.body}"
                    resultTextView.text = resultText
                } else {
                    resultTextView.text = "请求失败，错误码: ${response.code()}"
                }
            }

            // 请求失败时回调（比如没网、域名解析失败、DNS错误等）
            override fun onFailure(call: Call<Post>, t: Throwable) {
                resultTextView.text = "网络请求发生异常: \n${t.message}"
                Log.e("RetrofitExample", "请求错误", t)
            }
        })
    }
}