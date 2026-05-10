package com.example.intent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.layoutlearn.R

class TargetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        val tvReceivedData = findViewById<TextView>(R.id.tvReceivedData)
        val btnReturnResult = findViewById<Button>(R.id.btnReturnResult)

        // 1. 接收上一个页面通过 Intent 传递过来的数据
        // intent 是 Activity 内部的属性，获取启动该 Activity 的 Intent
        val message = intent.getStringExtra("EXTRA_MESSAGE")
        if (message != null) {
            tvReceivedData.text = "收到的消息: $message"
        } else if (intent.action == "com.example.intent.ACTION_CUSTOM_TEST") {
            // 通过获取这个 Intent 的动作名，我们可以断定这是用 Intent-Filter 唤起的
            tvReceivedData.text = "成功！我是依赖 <intent-filter> 被隐式唤起的！"
            tvReceivedData.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        }

        // 2. 点击按钮返回数据给上一个页面
        btnReturnResult.setOnClickListener {
            // 创建一个新的 Intent 用来携带返回的数据
            val resultIntent = Intent().apply {
                putExtra("RETURN_DATA", "这是从 TargetActivity 返回的数据")
            }
            // 设置结果码为 RESULT_OK，并将携带数据的 Intent 传入
            setResult(Activity.RESULT_OK, resultIntent)
            
            // 销毁当前 Activity，返回上一页
            finish()
        }
    }
}
