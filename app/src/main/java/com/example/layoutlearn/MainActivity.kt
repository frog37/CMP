package com.example.layoutlearn

import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // 定义一个布尔变量，用于标记当前是否处于主菜单界面
    // true 表示在主菜单，false 表示在子布局页面
    private var isMainMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启用 Edge-to-Edge 模式，让应用内容延伸到状态栏和导航栏区域
        enableEdgeToEdge()

        // 初始化显示主菜单
        showMainMenu()

        // 注册自定义返回键回调
        // this: 生命周期所有者，确保回调在 Activity 销毁时自动移除，防止内存泄漏
        // object : OnBackPressedCallback(true): 创建回调对象，true 表示默认启用该回调
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 如果当前不在主菜单（即在子页面），则返回主菜单
                if (!isMainMenu) {
                    showMainMenu()
                } else {
                    // 如果当前已经在主菜单，执行系统的默认返回行为（通常是退出应用）

                    // 1. 暂时禁用该回调，防止死循环
                    isEnabled = false
                    // 2. 触发系统的默认返回操作
                    onBackPressedDispatcher.onBackPressed()
                    // 3. 恢复回调启用状态，以便下次使用
                    isEnabled = true
                }
            }
        })
    }

    // 定义显示主菜单的方法
    private fun showMainMenu() {
        // 设置主菜单的布局文件
        setContentView(R.layout.activity_main)
        // 更新状态标记
        isMainMenu = true

        // 获取各个按钮并设置点击监听器
        // 点击后切换到对应的布局示例页面

        // 线性布局按钮
        findViewById<Button>(R.id.btn_linear).setOnClickListener {
            setContentView(R.layout.linear_layout_example)
            isMainMenu = false // 标记已离开主菜单
        }

        // LiveData 按钮
        findViewById<Button>(R.id.btn_livedata).setOnClickListener {
            startActivity(android.content.Intent(this, com.example.layoutlearn.livedata.LiveDataActivity::class.java))
        }

        // 相对布局按钮
        findViewById<Button>(R.id.btn_relative).setOnClickListener {
            setContentView(R.layout.relative_layout_example)
            isMainMenu = false
        }

        // 帧布局按钮
        findViewById<Button>(R.id.btn_frame).setOnClickListener {
            setContentView(R.layout.frame_layout_example)
            isMainMenu = false
        }

        // 约束布局按钮
        findViewById<Button>(R.id.btn_constraint).setOnClickListener {
            setContentView(R.layout.constraint_layout_example)
            isMainMenu = false
        }

        // Intent 学习按钮
        findViewById<Button>(R.id.btn_intent).setOnClickListener {
            startActivity(android.content.Intent(this, com.example.intent.IntentDemoActivity::class.java))
        }
    }
}