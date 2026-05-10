package com.example.layoutlearn.composeUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 这是一个用于演示 Jetpack Compose 声明式 UI 的代码示例。
 * 
 * Compose 的核心思想是 "状态（State）驱动 UI"。
 * 1. 当状态改变时，界面会自动触发 "重组 (Recomposition)"，从而更新受影响的部分。
 * 2. 避免了传统 View 体系中需要手动 findView 然后 setText() 等繁琐繁杂的步骤。
 */
class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 setContent 来设置 Compose 页面内容，替代传统的 setContentView(R.layout...)
        setContent {
            MaterialTheme {
                // 调用包含界面的可组合函数 (Composable)
                ShoppingAppDemo()
            }
        }
    }
}

/**
 * 在 Compose 中，使用 @Composable 注解的函数被称为"可组合函数"，
 * 它们是构建界面的基础块(积木)。
 * 
 * 下面创建了一个简单的商品添加演示页。
 */
@Composable
fun ShoppingAppDemo() {
    // 状态声明 (State)
    // mutableIntStateOf 会创建一个可观察的状态，当 count 发生改变时，任何读取到 count 的 Composable 都会重新执行刷新（重组）。
    // remember 的作用是"记住"这个状态，保证在屏幕重组（函数重新执行）时，不会把 count 重置为 0。
    var count by remember { mutableIntStateOf(0) }

    // 【解释：Material Design (质感设计)】
    // Material Design 是 Google 推出的一套设计系统规范。它的核心理念是将手机屏幕里的UI元素隐喻为现实中的"纸层(Material)"。
    // 这意味着控件会有厚度、层叠关系、阴影(光影效果)，并在点击时有涟漪(水波纹)等物理反馈。
    // Compose 的 androidx.compose.material3 库直接实现了这套规范。
    // Surface 就是一张承载内容的"纸"，它会根据主题自动帮我们处理背景色，以及上面文字的默认颜色。
    Surface(
        // modifier (修饰符)：这是 Compose 中极其重要的概念！用来改变控件的尺寸、外边距、点击事件等。
        // Modifier.fillMaxSize() 相当于 XML 里的 match_parent，表示让这层"纸"填满父级（也就是占满整个屏幕）。
        modifier = Modifier.fillMaxSize(),
        
        // color：设置这层对象的背景色。
        // MaterialTheme.colorScheme.background 指的是动态读取当前 Material 主题规范配色的"背景色"。
        // 它非常智能，当用户切换手机的【深色模式 / 浅色模式】时，这个背景颜色会自动随之切换为黑或白。
        color = MaterialTheme.colorScheme.background
    ) {
        // Column 是线性垂直布局容器，类似于 LinearLayout 设置 vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 相当于外边距 padding
            horizontalAlignment = Alignment.CenterHorizontally, // 控件水平居中
            verticalArrangement = Arrangement.Center // 控件垂直居中
        ) {
            // Text 是文本控件，相当于 TextView
            Text(
                text = "声明式 UI 演示",
                fontSize = 24.sp,
                // fontWeight 用于设置字体的粗细。
                // FontWeight.Bold 表示将文字加粗，就相当于传统 XML 写法里的 android:textStyle="bold"
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp)) // Spacer 用于在控件之间制造空隙

            // 包含商品数量的卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp), // 圆角
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // 阴影
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "购物车商品数量", fontSize = 18.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$count", // 这里因为引用了 count 状态，当 count 改变，只有这里的 Text 会重组更新数字
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Row 是水平布局容器，类似于 LinearLayout 设置 horizontal
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp) // 内部子项按 16dp 间距排列
            ) {
                // Button 相当于传统的 Button
                Button(
                    onClick = { if (count > 0) count-- }, // 声明事件：点击时让状态 count 减 1
                    modifier = Modifier.weight(1f) // 占屏幕宽度的比例
                ) {
                    Text("减少", fontSize = 18.sp)
                }

                Button(
                    onClick = { count++ }, // 声明事件：点击时让状态 count 加 1
                    modifier = Modifier.weight(1f)
                ) {
                    Text("增加", fontSize = 18.sp)
                }
            }
        }
    }
}

/**
 * @Preview 注解可以在 Android Studio 内直接预览 UI，不需要运行到手机上。
 */
@Preview(showBackground = true)
@Composable
fun ShoppingAppDemoPreview() {
    MaterialTheme {
        ShoppingAppDemo()
    }
}
