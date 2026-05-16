package com.example.binding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// 下面这一行会引入我们通过开启 ViewBinding/DataBinding 功能后，由编译器自动生成的一个绑定类。
// 这个类的命名规则是：把你的 XML 布局文件名 activity_binding_demo 按照大驼峰命名规则，并在末尾加上 Binding
import com.example.layoutlearn.databinding.ActivityBindingDemoBinding

class BindingDemoActivity : AppCompatActivity() {

    /**
     * 声明一个 Binding 类型的变量
     * ActivityBindingDemoBinding 是根据布局文件 activity_binding_demo.xml 自动生成的类。
     * 只要你创建或修改了那个 xml，编译器就会自动更新这个类（可能需要先进行 Build -> Make Project 构建操作）。
     */
    private lateinit var binding: ActivityBindingDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化 Binding 实例
        // 传统方式：setContentView(R.layout.activity_binding_demo)
        // 现在使用 Binding 的方式：通过 inflate 解析布局并获取 binding 实例。
        binding = ActivityBindingDemoBinding.inflate(layoutInflater)
        
        // 2. 将 binding 中的根视图(root)设置为此 Activity 所需展示的内容视图
        setContentView(binding.root)

        // =========================================================================
        // 【概念一】演示 DataBinding (数据驱动：关注数据！)
        // =========================================================================
        val userItem = UserModel("张三", 25)
        
        // 我们通过 XML 中的 <data> 标签定义过一个 name="user" 的变量，
        // 对应在 binding 对象上就会自动生成一个 setUser() 的方法（在 Kotlin 中可以直接用 .user）
        // 绑定后，XML 中对应写着 @{user.xxx} 的视图就会自动加载这套数据！
        binding.user = userItem

        // =========================================================================
        // 【概念二】演示 ViewBinding (视图驱动：优雅拿到控件！)
        // =========================================================================
        // 如果是以前，我们要改变文本可能会写：
        // findViewById<TextView>(R.id.tv_view_binding_text).text = "..." 
        // 
        // 但是使用 ViewBinding 后，XML 里面带 ID (tv_view_binding_text) 的控件
        // 都会被自动映射成了 binding 对象的属性（转换下划线为小驼峰命名：tvViewBindingText）。
        // 这样可以避免类型强转，不会引起 NullPointerException！
        binding.tvViewBindingText.text = "-> 这句话是利用 ViewBinding 通过 ID 在代码中直接赋值的！"
        
        
        // =========================================================================
        // 点击按钮后演示 “数据驱动界面” 带来的便利
        // （依然是用 ViewBinding 从绑定对象中拿出按钮 btn_change_data）
        // =========================================================================
        binding.btnChangeData.setOnClickListener {
            // DataBinding 的魅力瞬间：
            // 我们【仅仅修改了数据模型里面的数据内容】，并没有写任何一行代码去获取 TextView 让他修改！
            // 但是你发现手机屏幕上的 TextView 自动把名字和年龄变了！
            userItem.name.set("李四 (已更新)")
            userItem.age.set(userItem.age.get() + 1)
            
            // 下面再结合展示一个用 ViewBinding 手动去操控 UI
            binding.tvViewBindingText.text = "-> 点了一下按钮！我的年龄现在长了一岁变成 ${userItem.age.get()} 岁了!"
        }
    }
}