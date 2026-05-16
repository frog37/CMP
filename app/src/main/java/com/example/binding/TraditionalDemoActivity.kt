package com.example.binding

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.layoutlearn.R

class TraditionalDemoActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvTraditionalText: TextView
    private lateinit var btnChangeData: Button

    // 传统方式里，数据只是普通变量；它们变化后，界面不会自动刷新。
    // 所以每次改完数据，都必须手动把最新值重新设置到对应的 View 上。
    private var userName = "张三"
    private var userAge = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 传统写法先加载 XML 布局，再通过 findViewById 一个个拿到控件。
        setContentView(R.layout.activity_traditional_demo)

        tvName = findViewById(R.id.tv_name)
        tvAge = findViewById(R.id.tv_age)
        tvTraditionalText = findViewById(R.id.tv_traditional_text)
        btnChangeData = findViewById(R.id.btn_change_data)

        // 初始化界面时，要主动把当前数据同步到 UI。
        updateUserInfo()
        tvTraditionalText.text = "-> 这句话是传统方式通过 findViewById 手动赋值的！"

        btnChangeData.setOnClickListener {
            userName = "李四 (已更新)"
            userAge += 1

            // 传统方式的关键特点：数据变了，不会自动驱动界面更新。
            // 如果这里忘了手动刷新，屏幕上显示的内容就会和真实数据不同步。
            updateUserInfo()
            tvTraditionalText.text = "-> 点了一下按钮！我的年龄现在长了一岁变成 ${userAge} 岁了!"
        }
    }

    // 把数据逐个写回控件，是传统方式里最常见的界面更新手法。
    // 当关联控件变多时，这种写法会更繁琐，也更容易漏改某个 View。
    private fun updateUserInfo() {
        tvName.text = "用户名：$userName"
        tvAge.text = "年龄：${userAge}岁"
    }
}
