package com.example.layoutlearn.looper

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 这是一个演示 Handler 和 Looper 机制的 Activity。
 * 
 * 核心概念：
 * 1. Looper (轮询器): 负责管理线程的消息队列 (MessageQueue)。它会不断地从消息队列中取出消息并分发给对应的 Handler。
 *    - 主线程（UI 线程）默认已经有了一个 Looper，即 Looper.getMainLooper()。
 *    - 普通后台线程如果在没有调用 Looper.prepare() 和 Looper.loop() 的情况下是没有 Looper 的。
 * 
 * 2. Handler (处理者): 负责发送消息 (sendMessage) 和处理消息 (handleMessage)。
 *    - Handler 必须绑定到一个特定的 Looper 才能工作。
 *    - 它可以通过 bind 到的目标线程的 Looper，将消息投递到那个线程的消息队列中。
 * 
 * 3. MessageQueue (消息队列): 存放通过 Handler 发送过来的消息（由 Looper 管理，开发者通常不需要直接操作它）。
 * 
 * 下面的代码展示了两种最常见的 Handler/Looper 使用场景：
 * - 场景一：后台线程处理完耗时任务后，通过绑定主线程 Looper 的 Handler 将结果发送回主线程更新 UI。
 * - 场景二：创建一个自带 Looper 的自定义后台线程，并通过绑定了该后台 Looper 的 Handler向其发送需要串行处理的消息。
 */
class LooperActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var mainHandler: Handler
    private var backgroundThreadHandler: Handler? = null

    // 抑制 Lint 警告："SetTextI18n"。
    // 原因：在 Android 开发中直接给 TextView 设置硬编码的字符串或拼接字符串 (如 text = "内容" + 变量) 会触发警告。
    // 规范做法：为了支持多语言(国际化 i18n)，建议将字符串提取到 res/values/strings.xml 中并通过占位符格式化。
    // 这里因为是学习 Demo，为了代码直观可读，直接写死了中文，所以加上此注解让编译器不再报黄警告。
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- UI 初始化 (为了简单起见，使用纯代码构建UI，而非XML) ---
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }
        statusTextView = TextView(this).apply {
            text = "准备就绪"
            textSize = 18f
            setPadding(0, 0, 0, 50)
        }
        val btnRunOnMain = Button(this).apply { text = "后台任务 -> 主线程更新UI" }
        val btnRunOnBg = Button(this).apply { text = "向自定义后台 Looper 发送任务" }
        
        rootLayout.addView(statusTextView)
        rootLayout.addView(btnRunOnMain)
        rootLayout.addView(btnRunOnBg)
        setContentView(rootLayout)

        // ==========================================================
        // 场景一：创建一个绑定到【主线程(Main Thread)】的 Handler
        // ==========================================================
        // 因为这段代码运行在 onCreate (属于主线程)，所以如果直接 new Handler()
        // 它的默认 Looper 就是当前线程的 Looper，也就是主线程的 Looper。
        // 为了清晰，我们显式传入 Looper.getMainLooper()
        mainHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                // 这个方法将在 Main Looper 所在的线程（即主 UI 线程）被调用
                // 因此在这里更新 UI 是绝对安全的
                when (msg.what) {
                    MSG_UPDATE_UI -> {
                        val textData = msg.obj as String
                        // 为什么给 text 赋值后屏幕上的字就跟着变了？
                        // 1. Kotlin 的语法 `statusTextView.text = ...` 底层等价于调用了 `TextView.setText(...)` 方法。
                        // 2. 在 `setText(...)` 方法内部，TextView 除了更新保存的字符串内容外，还会自动调用 `invalidate()` (请求重绘制) 和/或 `requestLayout()` (重新计算大小和位置)。
                        // 3. 这会向 Android 系统的屏幕渲染引擎发出一个“脏(Dirty)标记”，告诉系统该 View 需要重新绘制。
                        // 4. 因为当前代码已经运行在【主线程(UI线程)】上，所以 Android 在随后的 UI 绘制周期中，就能安全并顺利地把这行新字画在屏幕上。
                        // 
                        // 补充问题：如果不在主线程更改这个 statusTextView，UI 页面还能同步改变吗？
                        // 答：不能！不仅不能同步改变，程序还会直接崩溃（闪退）。
                        // Android 的 UI 框架是非线程安全的。为了防止不同的线程同时修改 UI 造成画面撕裂或逻辑混乱，
                        // 底层（ViewRootImpl）在每次修改 UI 开始重绘前，都会检查当前线程是不是最初创建视图的主线程。
                        // 如果发现你在子线程里尝试修改 TextView，会无情地抛出这个著名异常：
                        // CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                        statusTextView.text = "主线程UI已更新：$textData"
                    }
                }
            }
        }

        btnRunOnMain.setOnClickListener {
            statusTextView.text = "正在后台执行耗时任务..."
            simulateBackgroundTask()
        }

        // ==========================================================
        // 场景二：创建一个自带 Looper 的后台线程
        // ==========================================================
        startBackgroundLooperThread()

        btnRunOnBg.setOnClickListener {
            // 我们通过 backgroundThreadHandler 发送消息
            // 消息将会进入后台线程的消息队列，并在后台线程依次执行
            val msg = Message.obtain().apply {
                what = MSG_BG_TASK
                obj = "后台任务_ID_${System.currentTimeMillis() % 1000}"
            }
            backgroundThreadHandler?.sendMessage(msg)
            statusTextView.text = "已将任务投递给带Looper的后台线程\n(请查看 Logcat 或等待回传)"
        }
    }

    /**
     * 模拟一个常见的需求：后台线程做网络请求或进行繁重的计算，
     * 算完之后想把结果显示在屏幕上（UI 更新）。
     */
    private fun simulateBackgroundTask() {
        Thread {
            // 这是一个普通的工作线程，它没有自己的 Looper，不能用来显示Toast或做UI相关的操作
            Thread.sleep(2000) // 模拟耗时2秒
            
            // 耗时任务完成，准备发送消息给主线程
            val msg = Message.obtain().apply {
                what = MSG_UPDATE_UI
                obj = "我是来自普通后台线程的数据"
            }
            // 利用绑定在主线程的 mainHandler 发送消息即可！
            // 底层原理：消息被添加到了主线程的 MessageQueue 中，主线程的 Looper 轮询到它后提取出来，并调用 mainHandler.handleMessage
            mainHandler.sendMessage(msg)
        }.start()
    }

    /**
     * 创建一个一直存活、有自己的消息队列的后台线程。
     * 这类似于 Android 提供的 HandlerThread 类的底层实现。
     */
    private fun startBackgroundLooperThread() {
        Thread {
            // 步骤 1：为当前（后台）线程准备一个 Looper 和 MessageQueue
            Looper.prepare()

            // 步骤 2：创建一个绑定到当前（后台）线程 Looper 的 Handler
            // 因为在当前线程调用了 Looper.prepare()，可以直接使用 `Looper.myLooper()` 获取它。
            // 
            // 【疑问解答】：为什么点进 `Looper.myLooper()` 源码看，里面写的是 `throw new RuntimeException("Stub!");`？
            // 答：因为你在 Android Studio 中点击查看到的 `android.jar` 里面的源码是“桩代码（Stub）”。
            // Android SDK 为了减少体积，提供给开发者编译用的 `android.jar` 只有方法的签名（空壳），里面全是 `throw new RuntimeException("Stub!")`。
            // 真正的实现在手机（或模拟器）的 Android 系统 ROM 里。在设备上运行时的真正源码其实长这样：
            // public static @Nullable Looper myLooper() { return sThreadLocal.get(); }
            // 它是通过 ThreadLocal 获取当前线程绑定的那个 Looper 实例的。
            //
            // 补充说明：!! 是 Kotlin 中的“非空断言操作符” (Not-null assertion operator)。
            // 因为 Looper.myLooper() 可能返回 null (返回值类型是 Looper?)，但此时由于我们上一步刚调用过 Looper.prepare()，
            // 逻辑上它百分之百不为 null。所以用 !! 强行告诉编译器：“相信我，它绝对不是 null，直接解包拿去用。如果万一是 null 你直接抛空指针异常崩溃算了。”
            backgroundThreadHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    // 这里的代码是在后台线程执行的！不能直接更新UI。
                    if (msg.what == MSG_BG_TASK) {
                        val taskName = msg.obj as String
                        // 模拟后台线程顺序处理任务
                        Thread.sleep(1500) // 模拟耗时
                        
                        // 由于处于后台线程不能修改 TextView，
                        // 如果要在处理完毕后告诉用户，还需要再用 mainHandler 转发给主线程！
                        mainHandler.post {
                            statusTextView.text = "后台 Looper 刚处理完了: $taskName"
                        }
                    }
                }
            }

            // 步骤 3：让 Looper 开始死循环轮询。
            // 此时该线程将卡在 loop() 里面，不断从 MessageQueue 取消息分发。
            // 除非显式调用 looper.quit()，否则这之后的代码将永远不会被执行。
            Looper.loop()
            
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 好习惯：在 Activity 销毁时，清理掉等待中的消息，防止引起内存泄漏 (Memory Leak)
        // 比如 Activity 关闭了，但是后台线程还没执行完，等它发送消息回来时 Activity 已经销毁，就会造成泄漏。
        mainHandler.removeCallbacksAndMessages(null)
        
        // 退出后台线程的 Looper (让 loop() 循环结束，从而让线程正常结束)
        backgroundThreadHandler?.looper?.quit()
    }

    companion object {
        const val MSG_UPDATE_UI = 1
        const val MSG_BG_TASK = 2
    }
}