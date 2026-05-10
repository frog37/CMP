package com.example.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * 概念2：DAO (Data Access Object - 数据访问对象)
 * 提供用于访问数据库的方法集合。它把 SQL 查询语句映射到具体的函数上。
 * 
 * @Dao 注解标明这是一个 DAO 接口。
 * 你可以在这里定义所有的数据库增删改查（CRUD）操作。Room 会在编译时自动为你生成这些接口的实现代码。
 */
@Dao
interface UserDao {
    
    // @Query 用于编写自定义的 SQL 查询语句。
    // 在这里我们查询 "users" 表里的所有数据。
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    // 你也可以在 @Query 中使用参数，通过冒号 (:) 的方式去引用函数中的参数
    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    // @Insert 注解用于批量插入数据，这里我们接收一个 List 集合。
    // (注意：在新版 KSP 编译中，若使用 vararg 可能会导致数组类型推断异常，所以用 List 更稳定)
    @Insert
    fun insertAll(users: List<User>)

    // @Update 用于更新现有的数据。它会根据对象的 PrimaryKey (主键) 匹配数据库中的对应行进行修改。
    @Update
    fun update(user: User)

    // @Delete 用于删除对应的数据。同样是依据 PrimaryKey 进行匹配删除。
    @Delete
    fun delete(user: User)
}
