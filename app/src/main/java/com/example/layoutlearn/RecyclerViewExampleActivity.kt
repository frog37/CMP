package com.example.layoutlearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 演示使用 RecyclerView 控件的示例 Activity。
 * 这是目前 Android 开发中最常用的展示大数据量列表的主力控件！
 */
class RecyclerViewExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定 XML 布局文件
        setContentView(R.layout.activity_recycler_view_example)

        // 通过 ID 查找到此页面里的 RecyclerView 对象
        val recyclerView = findViewById<RecyclerView>(R.id.myRecyclerView)

        // ==========================================
        // 1. 准备数据源
        // ==========================================
        val dataList = mutableListOf<String>()
        for (i in 1..50) {
            dataList.add("RecyclerView 里的模拟数据 第 $i 个") // 假数据
        }

        // ==========================================
        // 2. 准备 LayoutManager (布局管理器)
        // ==========================================
        // RecyclerView 相比 ListView 强大之处在于它可以自由切换排版。
        // ListView 只能从上到下排着；而 RecyclerView 通过不同的 LayoutManager：
        //   - LinearLayoutManager (负责线性排版，上下滑、左右滑都可以)
        //   - GridLayoutManager (负责网格排版，比如相册九宫格)
        //   - StaggeredGridLayoutManager (负责瀑布流排版)
        
        // 【选项 1】单列纵向列表 (目前默认启用)
//        recyclerView.layoutManager = LinearLayoutManager(this)

        // 【选项 2】单列横向列表 (解开这行注释可以体验)
        // LinearLayoutManager.HORIZONTAL 表示横向滑动，false 表示不反转数据顺序
//         recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 【选项 3】网格布局 (解开这行注释可以体验)
        // 参数 2 表示我们要一行显示 2 列 (也就是九宫格的效果)
//         recyclerView.layoutManager = GridLayoutManager(this, 2)

        // 【选项 4】瀑布流布局 (解开这行注释可以体验)
        // 参数 2 代表要显示 2 列（或行，取决于方向）
        // StaggeredGridLayoutManager.VERTICAL 表示纵向瀑布流。由于它的 Item 高度是不固定的，因此会有错落感。
         recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // ==========================================
        // 3. 准备和设置适配器
        // ==========================================
        // 初始化我们自定义的 MyRecyclerAdapter，并将刚刚造的数据传给它。
        // {} 里面是我们处理列表被点击时候的逻辑，这是在 Kotlin 中很流行的高阶函数的写法。
        val adapter = MyRecyclerAdapter(dataList) { clickedItemData ->
            // 这里是点击事件的具体处理代码。
            Toast.makeText(this, "你点击了: $clickedItemData", Toast.LENGTH_SHORT).show()
        }

        // 将造好的 adapter 设置到控件中。
        recyclerView.adapter = adapter

        // ==========================================
        // (补充) 优势三：灵活动画组件 (ItemAnimator)
        // ==========================================
        // ListView 如果想要实现列表元素的“增添”或“删除”动画，是非常痛苦且性能极差的事。
        // 而 RecyclerView 有着独立的 ItemAnimator 组件。默认的 DefaultItemAnimator 已经自带非常平滑的交互动画。
        // 在 GitHub 上有许许多多强大的第三方 ItemAnimator（比如让列表项“翻滚着出现”），只要在这里替换就行：
        recyclerView.itemAnimator = DefaultItemAnimator()

        // ==========================================
        // (补充) 优势四：独立的装饰组件 (ItemDecoration)
        // ==========================================
        // ListView 只能在 xml 中使用 android:divider 简单地加一条有限支持的下划线分隔。
        // RecyclerView 提供了 ItemDecoration(条目装饰物)。
        // 这意味着你不只可以画分割线：你可以画分组卡片的标题、画旁边的时间轴、
        // 甚至是做类似通讯录里那种在字母滚动时停留的“粘性头部”(Sticky Header)！
        // 这里提供一个最基础的原生间隔线范例（请确保当前布局管理器仍然是纵向的）：
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // ==========================================
        // 重磅优势体验：局部刷新
        // ==========================================
        // ListView 更改数据只能调用 notifyDataSetChanged()全局刷新刷爆屏幕。
        // 而由于 RecyclerView 支持完美的局部刷新，搭配刚设置的 ItemAnimator 组件，动画极为美观。
        
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnRemove = findViewById<Button>(R.id.btnRemove)

        btnAdd.setOnClickListener {
            // 在第 1 个位置（即第二行）插入一条数据
            dataList.add(1, "我是刚刚插入的新数据！！")
            // ✨ 这一局代码就是精髓：定向通知 Adapter 第1个位置有新数据插入！自带向下的挤开动画！
            adapter.notifyItemInserted(1)
        }

        btnRemove.setOnClickListener {
            if (dataList.isNotEmpty()) {
                // 删除第 0 项数据（第一行）
                dataList.removeAt(0)
                // ✨ 定向通知 Adapter 第 0 项数据被移除了！下面的数据会自动带着动画升上来填补空缺！
                adapter.notifyItemRemoved(0)
            } else {
                Toast.makeText(this, "数据已删空", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// ==========================================
// 适配器 (Adapter) 部分 - 核心难点
// ==========================================
/**
 * 这个类负责三件事：
 * 1. 知道数据有多少个 (getItemCount)
 * 2. 把每一行 (Item) 的专属 XML 页面给变成 View 对象 (onCreateViewHolder)
 * 3. 把最新的数据放到每一行具体的 TextView/ImageView 等控件里面 (onBindViewHolder)
 *
 * 泛型参数 <MyRecyclerAdapter.MyViewHolder> 要求我们提供自己定义的 ViewHolder。
 */
class MyRecyclerAdapter(
    private val dataList: List<String>,            // 这是传进来的数据列表
    private val onItemClick: (String) -> Unit      // 这是一个点击事件回调函数
) : RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    /**
     * ViewHolder (视图持有者) 模式
     * 作用：在最初创建每一行 View 的时候，一次性执行费时耗力的 findViewById；
     * 等到这一行滑动出屏幕，被复用时，就不用再去找一遍这些控件在哪里了，以提高滑动性能。
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 在内部将这一行布局(itemView)里的需要变化内容的控件缓存成变量。
        val textView: TextView = itemView.findViewById(R.id.itemText)
        // 如果你的 item_recycler_view 里面还有其他元素要改变，你可以继续加，比如：
        // val imageView: ImageView = itemView.findViewById(R.id.itemImage)
    }

    /**
     * (重写方法 1/3)
     * 系统发现当前如果没有多余的、可以被复用的 View 时，就会调用这个方法来请求一个新的格子。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // 使用 LayoutInflater 这个类将我们之前写好的 XML 布局加载到内存，变成真正的 View 对象。
        // 第一个参数是我们定义的 item_recycler_view.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        // 将这个 view 打包在 ViewHolder 里面返回给系统缓存。
        return MyViewHolder(view)
    }

    /**
     * (重写方法 2/3)
     * 系统要把某一行的数据展示在屏幕上时调用。
     * 此时 ViewHolder(包含里面的TextView等) 是现成的，但是它的数据（文字等）还是旧的，或者为空的。
     * 你要做的仅仅是：通过 position 去数据源里拿到要展示的数据，然后赋值给 ViewHolder 里面的控件。
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // 1. 获取对应行数（position）的数据
        val data = dataList[position]
        
        // 2. 把数据设置到对应的控件上 (也就是我们刚刚缓存在 ViewHolder 里的 textView)
        holder.textView.text = data

        // 3. （这一步可选）如果希望这整个 item 点击有反应
        // 我们可以给外面的大 View (holder.itemView) 设置监听器
        holder.itemView.setOnClickListener {
            // 当点击时，呼叫一下外面的使用方，顺便把点中的数据送出去
            onItemClick(data)
        }
    }

    /**
     * (重写方法 3/3)
     * 告诉系统一共有多少数据。这里直接返回 DataList 数组的长度就行。如果为0，那你的列表就是空的！
     */
    override fun getItemCount(): Int {
        return dataList.size
    }
}
