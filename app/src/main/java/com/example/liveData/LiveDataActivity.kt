package com.example.layoutlearn.livedata

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.layoutlearn.databinding.ActivityLiveDataBinding

class LiveDataActivity : AppCompatActivity() {

    // 使用 viewModels() 拓展函数，初始化 ViewModel。
    // 这里的 by 是 Kotlin 的“属性委托”语法，意思是：viewModel 这个属性的取值逻辑交给 viewModels() 返回的委托对象管理。
    // 它不是在这一行代码执行时立刻 new 出 LiveDataViewModel，而是采用“懒加载”方式：第一次真正访问 viewModel 时才去取或创建实例。
    // 你可以把它粗略理解成：
    // private val viewModel by lazy { ViewModelProvider(this).get(LiveDataViewModel::class.java) }
    // 但真实底层比这个更完整，里面还包含了 ViewModelStore、Factory 和生命周期托管等机制。
    //
    // 第一次访问 viewModel 时，底层大致会经历下面几步：
    // 1. 调用委托对象的 getValue()。
    // 2. 基于当前 Activity 创建一个 ViewModelProvider。
    // 3. 去当前 Activity 持有的 ViewModelStore 里查找：之前是否已经创建过 LiveDataViewModel。
    // 4. 如果已经有现成实例，就直接返回；如果没有，就通过 Factory 创建一个新的 LiveDataViewModel。
    // 5. 新建出来后，会被存回 ViewModelStore，供后续重复访问时直接复用。
    //
    // 所以它并不是“每次访问都重新实例化”，而是“同一个 Activity 作用域内按需创建一次，然后持续复用”。
    // 这也是为什么即便屏幕旋转导致 Activity 销毁重建，通常依然能拿到同一个 ViewModel 实例：
    // 配置变更发生时，系统会保留这份 ViewModelStore；新 Activity 重建后，再通过 ViewModelProvider 取值时，就会拿回之前那个实例。
    // 只有当 Activity 真正结束（而不是仅仅因为旋转而重建）时，对应的 ViewModelStore 才会被清空，ViewModel 才会进入 onCleared() 生命周期并最终销毁。
    //
    // 一句话总结：by viewModels() 的底层就是通过 ViewModelProvider 从当前 Activity 的 ViewModelStore 取 ViewModel；
    // 有就复用，没有就借助 Factory 创建，并把它的生命周期绑定到当前 Activity 的作用域上。
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
