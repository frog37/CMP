package com.example.layoutlearn.example.cmp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 什么是 KMP 和 CMP？
 * 
 * 1. KMP (Kotlin Multiplatform):
 *    侧重于【业务逻辑】的代码共享。你可以把网络请求、数据模型、数据库访问和业务规则等
 *    纯逻辑部分只写一次（使用 Kotlin），然后在 Android、iOS、Desktop 和 Web 等平台共享。
 *    每个平台的 UI 仍需使用原生方式独立开发（例如 Android 用 XML/Jetpack Compose，iOS 用 SwiftUI）。
 * 
 * 2. CMP (Compose Multiplatform):
 *    基于 KMP 做出的进一步扩展，侧重于【UI 界面的完全代码共享】。
 *    也就是说，除了共享业务逻辑（基于 KMP），你还可以使用类似 Jetpack Compose 的语法
 *    直接编写一套 UI 代码（使用声明式 UI），这套 UI 代码会自动在 Android、iOS、Desktop 和 Web 上运行。
 *    在底层：
 *    - Android 会直接调用 Android 原生的 Jetpack Compose，拥有原生性能。
 *    - iOS 使用了基于 Skia 引擎的自绘机制。
 * 
 * 下面的代码展示了一个 CMP 常见的跨平台组件写法示例。
 * 注：当前工程是纯 Android 工程，因此在此仅使用 Jetpack Compose 进行效果演示，
 * CMP 的 UI 代码（Compose Multiplatform API）几乎与 Android 的 Jetpack Compose 完全一致。
 */

class CMPDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Surface 是一个基础的 Material 容器组件，它通常用于设置页面的底层背景色，同时也能处理形状、阴影等。
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CMPDemoScreen()
                }
            }
        }
    }
}

// 声明我们选择明确使用带有 @ExperimentalMaterial3Api 标记的 API（例如此页面中的 TopAppBar 默认目前就是实验性的）
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CMPDemoScreen() {
    // =========================================================
    // 【👇 CMP (Compose Multiplatform) 的部分从这里开始 👇】
    // 整个界面的声明式 UI 构建代码（下面的 Scaffold, Column, Text, Button 等），
    // 在真实架构中会放在 commonMain 共享层。
    // Android、iOS 等所有平台直接共用这一套 "UI 绘制逻辑"，这就是 CMP 的核心理念。
    // =========================================================
    val context = LocalContext.current
    
    // =========================================================
    // 【👇 KMP (Kotlin Multiplatform) 的部分体现（业务逻辑） 👇】
    // 这种纯粹的数据变化、状态管理、业务逻辑（例如网络请求回来后该怎么赋值），
    // 属于非 UI 渲染的部分，不论用不用 CMP，这部分原本就是 KMP (共享逻辑) 的核心。
    // =========================================================
    // 假设这些是用 KMP 共享的业务模型数据
    var clickCount by remember { mutableStateOf(0) }
    
    // Scaffold 是 Material Design 的基础布局脚手架
    // 它提供了诸如顶栏 (TopAppBar)、底栏 (BottomAppBar)、悬浮按钮 (FAB) 等标准组件的槽位。
    Scaffold(
        topBar = {
            // 定义顶栏组件
            TopAppBar(
                title = { Text("KMP vs CMP 演示") }
            )
        }
    ) { paddingValues -> 
        // 详细解析: `) { paddingValues -> ` 是什么意思？
        // 1. 语法层面 (尾随 Lambda):
        //    Scaffold 的最后一个参数叫做 `content`，它的类型是一个接收 PaddingValues 并返回 Unit 的函数。
        //    在 Kotlin 中，如果函数的最后一个参数是 Lambda 表达式，你可以把大括号 {...} 提取到圆括号外面。
        //    这种写法非常优雅，它本质上就等于: Scaffold(topBar = {...}, content = { paddingValues -> ... })。
        // 2. 机制层面 (安全内边距传递):
        //    Scaffold 会帮我们放置顶部导航栏 (TopAppBar)、底部导航栏、悬浮按钮等元素。
        //    它在测量完这些边缘组件的高度后，会把它们占据的空间打包成 `paddingValues` 暴露给这个代码块里面。
        //    由于 Scaffold 默认允许内容延伸到全屏，如果不去使用这个 paddingValues，
        //    我们的主内容区域（下面的 Column）默认就会绘制在屏幕的最顶端，导致顶部的文字被 TopAppBar 完全遮盖。
        //    因此，必须把 paddingValues 传递给内部的第一层容器（通过 Modifier.padding()），让内部容器主动避开这些遮挡区域。

        // Column 是一列垂直排列的布局容器（类似传统 Android XML 里垂直方向的 LinearLayout）
        Column(
            modifier = Modifier
                .fillMaxSize() // 填充所有的剩余可用空间
                .padding(paddingValues) // 应用 Scaffold 传入的内边距，避开 topBar
                .padding(16.dp), // 额外增加外围的 16dp 边距
            horizontalAlignment = Alignment.CenterHorizontally, // 声明子元素水平方向居中对齐
            verticalArrangement = Arrangement.Center            // 声明子元素垂直方向居中铺排
        ) {
            // 普通展示文本
            Text(
                text = "欢迎体验 Compose Multiplatform 的魅力",
                style = MaterialTheme.typography.titleMedium // 应用 Material 3 预设的排版字体样式
            )
            
            // Spacer 用于在组件之间留出视觉空白，这里留出 16dp 的垂直高度
            Spacer(modifier = Modifier.height(16.dp))

            // 展示特定平台的方法 (在真实 CMP 中使用 expect/actual 机制实现)
            // 在这里我们用一个模拟的平台信息函数
            Text(
                text = "当前运行平台: ${getPlatformName()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary // 使用当前主题自带的主色调展示
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 包含点击回调作用的按钮组件
            Button(onClick = { 
                // 每次点击的时候去修改状态变量 clickCount
                // 状态一旦改变，Compose 框架就会自动触发重组(Recompoistion)，仅刷新读取了它的相关的 UI 节点
                clickCount++
                showToast(context, "点击了 $clickCount 次") 
            }) {
                Text("点我测试业务逻辑 ($clickCount)")
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Card 组件类似于以前的 CardView，能提供一个带圆角、能绘制阴影等 Material 视效的卡片容器
            Card(
                modifier = Modifier.fillMaxWidth() // 允许此卡片内容充满横向最大宽度
            ) {
                // 卡片内部再次包含一层垂直布局控制文案的排版
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("💡 小贴士：", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• KMP：只共享逻辑代码不共享 UI。")
                    Text("• CMP：不仅共享业务逻辑，还可以通过 Compose 一套代码编写所有平台的 UI 界面。")
                }
            }
        }
    }
}

/**
 * 在真实的 KMP/CMP 项目中，如果涉及和平台绑定的特定 API（如获取系统版本号）。
 * 我们会使用 `expect` 和 `actual` 关键字。这是 Kotlin 跨平台开发处理平台差异的核心机制：
 * 
 * 1. 【expect】（预期）：定义在共享代码层 (commonMain) 中。类似于一个接口或方法声明，
 *    它告诉编译器：“我声明了一个函数（或类），但它的具体实现由各个运行平台自己去完成。”
 *    示例： expect fun getPlatformName(): String
 * 
 * 2. 【actual】（实际）：定义在各自的平台模块中 (如 androidMain, iosMain)。
 *    它负责兑现 `expect` 的契约，利用该平台底层的原生 API 来提供真正的实现。
 *    Android 实现示例： actual fun getPlatformName() = "Android ${Build.VERSION.RELEASE}"
 *    iOS 实现示例：     actual fun getPlatformName() = UIDevice.currentDevice.systemName + " " + UIDevice.currentDevice.systemVersion
 * 
 * 机制原理：
 * 利用这套机制，你在 commonMain(共享逻辑) 里就可以放心地直接调用 getPlatformName()。
 * 编译器在最终打包 Android App 时，会自动把这个调用桥接到 Android 的实际实现上；打包 iOS 时同理。
 * 这样既保证了代码的最大化共享，又不丢失调用底层原生特殊能力（比如蓝牙、GPS、系统版本）的灵活性。
 * 
 * ===============================================================
 * 假设这是一个真正的 KMP / CMP 多平台工程，目录结构和代码将如下所示：
 * ===============================================================
 * 
 * 📂 composeApp/src/commonMain/kotlin/.../Platform.kt
 * -------------------------------------------------------------
 * // 1. 【通用层声明】不关心具体平台如何实现，只规定返回类型和参数。
 * // 注意：通用层不能有 Android 特有的 Context 参与，所以 Toast 的方法签名去掉了 Context。
 * expect fun getPlatformName(): String
 * expect fun showToast(message: String)
 *
 * 📂 composeApp/src/androidMain/kotlin/.../Platform.android.kt
 * -------------------------------------------------------------
 * // 2. 【Android 层实现】可以直接调用所有的 Android SDK (如 Build, Toast)
 * actual fun getPlatformName(): String {
 *     return "Android ${Build.VERSION.RELEASE}"
 * }
 * actual fun showToast(message: String) {
 *     // 在真实的 KMP 中，Android 的 Context 通常会在 Application 初始化时持存在一个单例中
 *     // Toast.makeText(GlobalAppContext.get(), message, Toast.LENGTH_SHORT).show()
 * }
 *
 * 📂 composeApp/src/iosMain/kotlin/.../Platform.ios.kt
 * -------------------------------------------------------------
 * // 3. 【iOS 层实现】可以通过 Kotlin Native 直接调用 Apple UIKit/Foundation API
 * // 
 * // 【答疑：Kotlin 能调用 iOS 的包吗？】
 * // 答案是：完全可以！这正是 Kotlin Multiplatform 的黑科技所在（基于 Kotlin/Native 技术）。
 * // Kotlin/Native 内部提供了一个名为 cinterop 的跨语言互操作工具，它在编译时会读取 
 * // Apple 的 Objective-C/C 框架头文件，并把它们自动映射、包装成 Kotlin 的类和方法。
 * // 
 * // 因此，在 iosMain 模块的 Kotlin 文件首部，你可以直接这样导包：
 * // import platform.UIKit.UIAlertController
 * // import platform.UIKit.UIDevice
 * // import platform.Foundation.*
 * // 
 * // 这意味着，尽管你敲的是 Kotlin 语法，实际上你是在以"第一公民"的身份直接调用
 * // iOS 系统底层的原生 API。没有任何性能损耗的中间桥接损耗。
 * //
 * actual fun getPlatformName(): String {
 *     // 这里的 UIDevice 就是由 platform.UIKit 提供的纯正 iOS 原生类
 *     return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion()
 * }
 * actual fun showToast(message: String) {
 *     // 调用 iOS 原生 UI (比如 UIAlertController 或者自定义库) 弹出提示
 * }
 * ===============================================================
 * 
 * 此处受限于当前只是纯 Android 工程，我们仅提供一个普通的函数来模拟理解这一特性：
 */
// =========================================================
// 【👇 这也是 KMP (Kotlin Multiplatform) 的典型体现 👇】
// KMP 的一大核心是：通过 expect/actual 将通用的、纯业务性质的方法签名
// (如获取系统版本号、发起蓝牙请求、读写本地加密文件等非界面操作)
// 桥接到各平台自己的原生底层 API 上去实现对接。
// 这个过程完全是针对“系统功能逻辑”、“平台生态差异”的一套处理方式。
// =========================================================
fun getPlatformName(): String {
    return "Android ${Build.VERSION.RELEASE}"
}

// Android 特定的 UI 交互（Toast）。
// 在真正的 CMP 中，你会看到 commonMain 中声明的 `expect fun showToast(message: String)` 是没有 context 参数的，因为 iOS 完全没有 Context 概念。
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * 用于 Android Studio 中的实时预览。
 * 添加 @Preview 注解后，可以直接在 Android Studio 的 Split/Design 视图中看到 UI 的渲染效果，
 * 而不需要每次都运行到真机或者模拟器上。
 */
@Preview(showBackground = true)
@Composable
fun CMPDemoScreenPreview() {
    MaterialTheme {
        CMPDemoScreen()
    }
}
