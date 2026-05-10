package com.example.layoutlearn.livedata

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.layoutlearn.databinding.ActivityLiveDataBinding

class LiveDataActivity : AppCompatActivity() {

    // 使用 lazy 委托结合 viewModels() 拓展函数，初始化ViewModel。
    // 这会将 ViewModel 的生命周期与当前 Activity 绑定。
    // 即便屏幕旋转导致 Activity 销毁重建，依然会返回同一个 ViewModel 实例。
    private val viewModel: LiveDataViewModel by viewModels()

    // 视图绑定 (ViewBinding) 懒加载形式，避免手写 findViewById
    private lateinit var binding: ActivityLiveDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 ViewBinding 并设置内容视图
        binding = ActivityLiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 设置观察者 (Observers) 监听 LiveData 的变化
        setupObservers()

        // 2. 设置按钮的点击事件 (发送意图给ViewModel处理)
        setupClickListeners()
    }

    /**
     * 注册 LiveData 观察者
     */
    private fun setupObservers() {
        // 观察 counter (整型 LiveData)
        // 参数1: LifecycleOwner (当前Activity的生命周期所有者，也就是 this)
        // 参数2: Observer，当 LiveData 数据发生变化时被回调
        // 好处：LiveData 会感知 Activity 的生命周期。当Activity在后台不可见时，不会触发更新，从而节省资源和避免崩溃。
        viewModel.counter.observe(this, Observer { count ->
            // UI 会在主线程更新
            binding.tvCounter.text = "当前数字：$count"
        })

        // 观察 status (字符串 LiveData)
        viewModel.status.observe(this, Observer { newStatus ->
            binding.tvStatus.text = newStatus
        })
    }

    /**
     * 初始化按钮点击事件，所有逻辑转移给 ViewModel 处理，保持Activity职责单一（即只负责更新UI界面）。
     */
    private fun setupClickListeners() {
        binding.btnIncrement.setOnClickListener {
            viewModel.incrementCounter()
        }

        binding.btnDecrement.setOnClickListener {
            viewModel.decrementCounter()
        }

        binding.btnSimulateNetwork.setOnClickListener {
            viewModel.simulateNetworkRequest()
        }
    }
}
