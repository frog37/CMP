package com.example.layoutlearn.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel的作用是将数据与UI控制器（如Activity/Fragment）分离。
 * 这样的好处是，当Activity由于屏幕旋转等配置更改而重建时，数据不会丢失。
 */
class LiveDataViewModel : ViewModel() {

    // 1. MutableLiveData 是可变的LiveData，允许我们通过 setValue (主线程) 或 postValue (后台线程) 更新数据。
    // 我们通常将其声明为 private 的，防止外部随意修改数据源。
    private val _counter = MutableLiveData<Int>()
    private val _status = MutableLiveData<String>()

    // 2. LiveData 是只读的。我们将上面的 MutableLiveData 暴露为 LiveData。
    // Activity / Fragment 只能观察 (observe) 这个只读的数据，从而保证了数据的安全性（单向数据流）。
    val counter: LiveData<Int> get() = _counter
    val status: LiveData<String> get() = _status

    init {
        // ViewModel初始化时赋初始值
        _counter.value = 0
        _status.value = "状态：准备就绪"
    }

    /**
     * 增加计数
     */
    fun incrementCounter() {
        // 使用 .value = xxx (本质是 setValue) 必须在 【主线程】 调用
        _counter.value = (_counter.value ?: 0) + 1
        _status.value = "状态：已增加"
    }

    /**
     * 减少计数
     */
    fun decrementCounter() {
        val currentValue = _counter.value ?: 0
        if (currentValue > 0) {
            _counter.value = currentValue - 1
            _status.value = "状态：已减少"
        } else {
            _status.value = "状态：不能小于0"
        }
    }

    /**
     * 模拟耗时的网络请求或后台任务
     */
    fun simulateNetworkRequest() {
        _status.value = "状态：模拟后台加载中..."
        
        // viewModelScope 是ViewModel自带的协程作用域，当ViewModel销毁时，协程会自动取消，避免内存泄漏。
        viewModelScope.launch {
            delay(3000) // 模拟耗时 3 秒
            
            // 在协程内模拟获取到了服务器返回的新数据
            val newRandomValue = (100..999).random()
            
            // 注意：虽然当前环境是在协程中，如果你切换了 Dispatchers.IO，则必须使用 postValue。
            // 原因是 .value = xxx 本质上调用的是 setValue()，它只能在主线程执行；
            // 在后台线程改 LiveData 时使用 postValue，主要是为了避免三个问题：
            // 1. 避免线程违规：后台线程直接 setValue() 可能触发主线程约束错误。
            // 2. 避免观察者在错误线程收到回调：LiveData 往往和 Activity / Fragment / Compose 绑定，通知应统一在主线程分发。
            // 3. 避免内部状态竞争和分发顺序混乱：把更新统一切回主线程后，当前值、版本号、观察者通知过程更容易保持一致。
            // postValue() 的作用就是：允许你在后台线程安全地发起更新，再把真正的赋值和通知切回主线程完成。
            // 它不会阻塞或挂起当前后台线程，而是异步投递后立刻返回；后台线程可以继续往下执行。
            // 但要注意，如果短时间连续多次 postValue，在主线程真正处理之前，中间值可能会被合并。
            // 这里因为默认 launch 是主线程，其实可以用 value，也可以用 postValue。
            // 这里使用 postValue，主要是为了演示后台线程更新 LiveData 时应该采用的写法：
            _counter.postValue(newRandomValue)
            _status.postValue("状态：加载完成！网络数据: $newRandomValue")
        }
    }
}
