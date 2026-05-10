package com.example.Room

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.layoutlearn.R
import kotlinx.coroutines.launch

/**
 * 这是一个安卓的 Activity 页面，作为 Room 数据库演示的独立入口
 */
class RoomDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定刚刚创建的 XML 布局
        setContentView(R.layout.activity_room_demo)

        val btnRunDb = findViewById<Button>(R.id.btnRunDb)
        val tvDbLog = findViewById<TextView>(R.id.tvDbLog)

        // 设置点击事件
        btnRunDb.setOnClickListener {
            tvDbLog.text = "正在后台线程执行数据库操作...\n请稍候..."
            btnRunDb.isEnabled = false // 防止重复点击

            // 使用 lifecycleScope.launch 开启一个专门为 Activity 生命周期管理的协程
            lifecycleScope.launch {
                // 这个操作在 IO 线程中耗时运行
                val example = RoomUsageExample()
                val logResult = example.demonstrateRoomUsage(this@RoomDemoActivity)

                // 运行完毕后，把构建出来的字符串日志，打印在主界面的 TextView 上
                tvDbLog.text = logResult
                btnRunDb.isEnabled = true
            }
        }
    }
}
