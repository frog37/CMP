package com.example.intent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.layoutlearn.R

class IntentDemoActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView

    // 使用较新的 Activity Result API 来替代过时的 startActivityForResult
    // 我们注册一个 contract，用来处理目标 Activity 返回的结果
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 获取返回的 Intent
            val data: Intent? = result.data
            // 提取数据
            val returnedString = data?.getStringExtra("RETURN_DATA")
            tvResult.text = "拿到返回结果: $returnedString"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_demo)
        tvResult = findViewById(R.id.tvResult)

        // 1. 显式 Intent (Explicit Intent)
        // 明确指定要启动的组件 (TargetActivity::class.java)
        findViewById<Button>(R.id.btnExplicitIntent).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }

        // 2. 携带数据的 Intent
        // 通过 putExtra 将键值对存入 intent，目标页面可以通过该键获取数据
        findViewById<Button>(R.id.btnIntentWithData).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            intent.putExtra("EXTRA_MESSAGE", "你好，我是 Intent 传来的数据！")
            startActivity(intent)
        }

        // 3. 跳转并期望返回结果的方法 (依赖 Activity Result API)
        findViewById<Button>(R.id.btnIntentForResult).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            // 调用上面注册好的 launcher
            startForResult.launch(intent)
        }

        // 4. 隐式 Intent (Action + Data)
        // 声明 ACTION_VIEW 意图，并附带 Url 数据，系统会匹配合适的浏览器
        findViewById<Button>(R.id.btnImplicitIntent).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.android.com")
            }
            startActivity(intent)
        }

        // 4.5 隐式 Intent (依靠 Intent-Filter 唤起我们自己的 TargetActivity)
        // 注意：这里没有指名道姓需要哪个 Activity，完全是根据它声明的 action 进行匹配的
        findViewById<Button>(R.id.btnIntentFilterTest).setOnClickListener {
            // 通过刚才在 AndroidManifest.xml 里面定义好的过滤规则 (Action 字符串) 去发送意图
            val intent = Intent("com.example.intent.ACTION_CUSTOM_TEST")
            // 系统收到这个请求后，会全局广播，然后你的 TargetActivity 中的 intent-filter 正好吻合！
            // 于是我们的 TargetActivity 就被唤起了
            startActivity(intent)
        }

        // 5. 隐式 Intent (Action + Type + Extras)
        // 使用 ACTION_SEND 来发送/分享数据，指定数据类型为纯文本，并在 Extras 中装载文本内容
        findViewById<Button>(R.id.btnActionSend).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // 这里体现了 Type
                putExtra(Intent.EXTRA_TEXT, "这是一段通过 Intent 分享的文本内容！") // 这里体现了 Extras
            }
            // 使用 Intent.createChooser 提供一个更友好的应用选择器界面
            startActivity(Intent.createChooser(intent, "分享到..."))
        }

        // 6. 隐式 Intent (Action + Category)
        // 模拟按下 Home 键返回桌面的行为
        findViewById<Button>(R.id.btnCategoryHome).setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME) // 这里体现了 Category
            }
            startActivity(intent)
        }
    }
}
