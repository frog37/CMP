package com.example.binding

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

/**
 * 这是一个被用于 DataBinding 的用户数据模型类
 * 
 * 我们引入了 ObservableField 和 ObservableInt 让数据具备“可观察(Observable)”的能力。
 * 它的核心意义是：
 * 当你在代码中改变这个 UserModel 里的值(如 user.name.set("李四"))时，
 * XML中绑定了 `@{user.name}` 的 TextView 会【自动感知】到这个变动，并且【自动更新文本】，
 * 你再也不需要手动去调用 textView.text = "李四"！这就叫数据驱动UI！
 */
class UserModel(name: String, age: Int) {
    
    // ObservableField 用于观察对象的改变（比如 String 字符串）
    val name = ObservableField<String>(name)
    
    // ObservableInt 专门用于观察基本数据类型 Int 的改变，性能更好
    val age = ObservableInt(age)
}