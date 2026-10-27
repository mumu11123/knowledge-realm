# AI 检索库首批专业笔记（v1）

这些片段独立于故事库，初始 V4 状态均为 PENDING。项目维护者于 2026-10-27 在聊天中阅读后明确批准这四段用于检索，V5 将这次审批仅绑定到四段原文的 SHA-256，不适用于不同或修改后的内容。以下是中文原创整理，不是对教材或真题来源的声明。

## K1 · 顺序表与单链表的按下标访问

在定长元素与随机访问内存模型下，顺序表的元素连续存放，可以通过首地址和下标计算目标地址，按下标访问为 O(1)。单链表的结点由指针链接，不能由下标直接计算任意结点地址；从头结点沿后继寻找任意下标的最坏时间为 O(n)。逻辑相邻不代表物理地址相邻。

参考：[OpenDSA · Comparison of List Implementations](https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html)。

## K2 · 插入复杂度的条件

单链表在已知插入位置的前驱结点、且结点分配视为常数代价时，修改链接插入为 O(1)。如果还需要从头查找前驱，查找部分最坏为 O(n)。顺序表中间插入通常需要移动后续元素，最坏为 O(n)。不能笼统地说链表插入永远比顺序表快。

参考：[OpenDSA · Comparison of List Implementations](https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html)。

## K3 · 栈

栈只允许在栈顶一端插入和删除元素，遵循后进先出 LIFO。栈是一种操作受限的线性表，不要求必须使用连续存储。只有在全部元素先入栈、期间没有出栈，然后连续出栈的条件下，完整出栈顺序才是入栈顺序的逆序；不能把这一结论推广为任意入栈出栈交错过程。

参考：[OpenDSA · Stacks](https://opendsa.org/OpenDSA/Books/Catalog/html/StackArray.html)。

## K4 · 普通队列

普通队列只允许在队尾插入、在队头删除，遵循先进先出 FIFO。队列的操作规则与顺序或链式实现分开，不能因为底层使用数组就允许任意位置出队。优先队列按优先级选择元素，不等同于普通 FIFO 队列。

参考：[OpenDSA · Queues](https://opendsa.org/OpenDSA/Books/CS3/html/Queue.html)。

## 审核范围

批准这些笔记仅允许专业知识检索，不意味着原演示故事已正式审核，也不批准任何未来 AI 生成故事。AI 草稿仍需逐个审核后发布。
