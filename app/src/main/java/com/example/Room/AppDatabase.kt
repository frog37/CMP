package com.example.Room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 概念3：Database（数据库）
 * 该类用于代表整个数据库应用，充当了底层 SQLite 数据库的接入点。
 * 
 * @Database 注解需要包含以下重要参数：
 * 1. entities: 这是一个数组，包含了你在这个数据库中使用的所有实体类（Entity）。Room 会为这些实体类建表。
 * 2. version: 数据库的版本号。如果日后你增加了表，或者修改了表结构（比如增加字段），你需要提升版本号，并告诉 Room 如何迁移(Migration)旧数据。
 * 
 * 注意：
 * - 数据库类必须是 abstract（抽象的），并且必须继承自 RoomDatabase。
 */
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    
    // 你必须在这里声明一个无参的抽象函数，用来返回对应的 Dao 对象。
    // Room 在编译时会自动生成该类的实现并实例化 UserDao。
    abstract fun userDao(): UserDao
}
