package com.example.layoutlearn

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 演示使用 ListView 控件的示例 Activity。
 * ListView 是早期 Android 用于展示纵向滑动列表的控件。
 */
class ListViewExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定对应的 XML 布局文件
        setContentView(R.layout.activity_list_view_example)

        // 通过 findViewById 获取我们在 layout 中定义的 ListView 实例
        val listView = findViewById<ListView>(R.id.myListView)

        // ==========================================
        // 1. 准备数据源 (Data Source)
        // ==========================================
        // 列表需要有数据支撑，这里我们用一个简单的循环创建 20 条字符串数据
        val dataList = mutableListOf<String>()
        for (i in 1..20) {
            dataList.add("ListView 列表项 第 $i 项")
        }

        // ==========================================
        // 2. 准备适配器 (Adapter)
        // ==========================================
        // ListView 本身只负责“展示”，不负责管理数据，数据和控件之间的桥梁就是 Adapter（适配器）。
        // ArrayAdapter 是系统提供的一个简易适配器，适合纯文本的单行列表。
        // 参数1: context (当前Activity)
        // 参数2: item的布局 (这里复用安卓系统自带的简单文本布局 simple_list_item_1)
        // 参数3: 真正需要展示的数据集合
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, 
            dataList
        )

        // ==========================================
        // 3. 将适配器设置给 ListView，完成数据绑定
        // ==========================================
        listView.adapter = adapter

        // ==========================================
        // 4. 设置列表项点击事件监听器 (OnItemClickListener)
        // ==========================================
        // 当用户点击 ListView 上的某一行时触发
        listView.setOnItemClickListener { parent, view, position, id ->
            // position 指的是被点击的项在列表中的位置（索引，从0开始）。
            val clickedItemData = dataList[position]
            // 使用 Toast 弹窗提示用户点击了哪一项
            Toast.makeText(this, "你点击了: $clickedItemData", Toast.LENGTH_SHORT).show()
        }
    }
}
