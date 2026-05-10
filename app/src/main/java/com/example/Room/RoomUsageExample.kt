package com.example.Room

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 示例类：如何初始化并使用 Room 数据库。
 */
class RoomUsageExample {

    suspend fun demonstrateRoomUsage(context: Context): String {
        val logBuilder = StringBuilder()

        val db = Room.databaseBuilder(
            // 参数1: Context 环境。数据库涉及到本地文件读写，需要系统环境。
            // 强烈建议使用 applicationContext 而不是 Activity 的 context，防止因为数据库长期存活导致 Activity 内存泄漏。
            context.applicationContext,
            // 参数2: 定义数据库结构的 Class 对象。Room 框架会根据这个类自动生成底层的实现代码。
            AppDatabase::class.java, 
            // 参数3: 数据库的文件名。Room 会在手机的内部隐蔽存储中建立一个叫这个名字的 SQLite 数据库文件。
            "my-database"
        )
        // 这个方法允许强制覆盖已有数据（仅供学习测试，真实项目中通常用 Migration 进行版本迁移）
        .fallbackToDestructiveMigration()
        .build()

        val userDao = db.userDao()

        // 【语法解释：withContext(Dispatchers.IO) 是 Kotlin 协程中用于切换线程的挂起函数】
        // 1. 因为 Room 规定不能在主线程（处理界面的线程）读写数据库以免卡死手机，所以我们要切到后台。
        // 2. Dispatchers.IO 专门指定了一个非常适合做磁盘 I/O（文件读写、数据库、网络访问）的后台线程池。
        // 3. 后面的 {} 叫做闭包（Lambda表达式）。花括号里面的代码全部会在这个后台线程中运行。
        // 4. 前面的 return 意味着等后台的闭包把代码全执行完后，会把闭包的最后一行（logBuilder.toString()）自动抛回到最外层作为函数的返回值。
        return withContext(Dispatchers.IO) {
            try {
                // 清理旧数据，方便重复查看结果
                val oldData = userDao.getAll()
                for (oldUser in oldData) {
                    userDao.delete(oldUser)
                }
                logBuilder.append("👉 [清理操作] 已清空过去的 ${oldData.size} 条数据。\n\n")

                // --- 增加 (Insert) ---
                val user1 = User(firstName = "三", lastName = "张")
                val user2 = User(firstName = "四", lastName = "李")
                userDao.insertAll(listOf(user1, user2)) 
                logBuilder.append("👉 [插入操作] 成功增加了张三、李四两条用户数据。\n\n")
                
                // --- 查询 (Query) ---
                val allUsers = userDao.getAll()
                logBuilder.append("👉 [查询操作] 当前用户：\n")
                for (user in allUsers) {
                    logBuilder.append("   - 姓名: ${user.lastName}${user.firstName} (数据库分配的主键 ID: ${user.id})\n")
                }
                logBuilder.append("\n")

                // --- 修改 (Update) ---
                if (allUsers.isNotEmpty()) {
                    val firstUser = allUsers[0]
                    val updatedUser = firstUser.copy(firstName = "更新的名字")
                    userDao.update(updatedUser) 
                    logBuilder.append("👉 [修改操作] 将名字 '${firstUser.lastName}${firstUser.firstName}' 改成了 '${updatedUser.lastName}${updatedUser.firstName}' (ID=${firstUser.id})\n\n")
                }

                // --- 删除 (Delete) ---
                if (allUsers.size > 1) {
                    val secondUser = allUsers[1]
                    userDao.delete(secondUser)
                    logBuilder.append("👉 [删除操作] 抹去了由数据库分配给李四的用户数据 (ID=${secondUser.id})\n\n")
                }

                // --- 确认 ---
                val finalUsers = userDao.getAll()
                logBuilder.append("✅ [最终验证] 当前还留在数据库的用户有：\n")
                for (user in finalUsers) {
                    logBuilder.append("   - ${user.lastName}${user.firstName} (ID: ${user.id})\n")
                }
            } catch(e: Exception) {
                logBuilder.append("\n❌ 发生异常: ${e.message}")
            }
            
            // 将文本结果返回到外层渲染UI
            logBuilder.toString()
        }
    }
}
