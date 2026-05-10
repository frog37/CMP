package com.example.layoutlearn.composeUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 这个页面专门用来直观地演示什么是 Material Design（MD 设计规范）。
 * MDC (Material Design Components) 是一套非常完善的组件库。
 */
class MaterialDesignDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MaterialDesignDemo()
            }
        }
    }
}

/**
 * 演示了 Material Design 中最核心的规范组件：
 * 1. Scaffold (脚手架)：帮你快速搭建一个符合 Material 规范的页面结构，包括顶部导航栏、悬浮按钮、底部导航等。
 * 2. TopAppBar：遵循 MD 规范的顶部应用标题栏。
 * 3. FloatingActionButton (FAB)：标志性的悬浮按钮，代表页面的主要操作。
 * 4. Card：具备"高度 (elevation/阴影)" 的材质卡片，表现出前后层级。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDesignDemo() {
    // Scaffold 是 Material 设计规范里最经典的页面布局结构容器
    Scaffold(
        topBar = {
            // 顶部导航栏也是标准的 Material Design 元素
            TopAppBar(
                title = { Text("Material Design 演示") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Material Design 的灵魂：代表页面核心动作的悬浮按钮
            FloatingActionButton(onClick = { /* TODO */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "什么是 Material Design (质感设计)？",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "你可以把它理解为现实生活中的纸片叠加。\n" +
                        "在屏幕上，有的纸离屏幕底层近，有的离我们眼睛近。" +
                        "通过阴影 (Elevation) 表达距离感（层叠高度）。" +
                        "此外它还统一定义了配色 (Primary, Secondary) 和排版字体 (Typography)。",
                style = MaterialTheme.typography.bodyMedium
            )

            // Card：典型的 Material 元素，一张由于阴影而悬浮于背景之上的"卡片"
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // 通过 8dp 的阴影高度，营造出真实卡片的悬浮感
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "这是一张材质卡片", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "这张卡片因为配置了阴影(elevation)，看起来就像悬浮在刚才那段文字上方一样，这就叫质感设计。")
                }
            }

            // Material Button：自带点击的水波纹涟漪效果 (Ripple)，也是典型的 MD 视觉反馈
            Button(onClick = { }) {
                Text("点我有水波纹涟漪")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaterialDesignDemoPreview() {
    MaterialTheme {
        MaterialDesignDemo()
    }
}
