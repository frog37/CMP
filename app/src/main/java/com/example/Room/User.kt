package com.example.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 概念1：Entity（实体）
 * 代表数据库中的一张表（Table）。
 * 
 * @Entity 注解告诉 Room 这个类是一个数据库实体。
 * 你可以使用 tableName 属性来自定义表名（默认是类名 User）。
 */
@Entity(tableName = "users")
data class User(
    // @PrimaryKey 注解表示这是表的主键。
    // autoGenerate = true 表示主键由数据库自动递增生成（类似 MySQL 的 AUTO_INCREMENT）。
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,

    // @ColumnInfo 可以用来指定数据表中的列名。如果不写该注解，默认列名就是变量名（firstName）。
    @ColumnInfo(name = "first_name") 
    val firstName: String?,
    
    @ColumnInfo(name = "last_name") 
    val lastName: String?
)
