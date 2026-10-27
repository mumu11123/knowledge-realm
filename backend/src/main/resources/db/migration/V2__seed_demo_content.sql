-- Original demo narratives. Sources are conceptual references, not claims of textbook/exam review.
SET @source = '原创演示；概念参考 OpenDSA https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html 、Stack.html、Queue.html；未经过教材/考纲审核';
SET @version = 'V0.1 演示内容；未绑定指定教材或考纲版本';

INSERT INTO subject(id,code,name,description,availability,sort_order) VALUES
(1,'data-structures','数据结构','从一段故事，认识数据之间的关系。','OPEN',1),
(2,'computer-organization','计算机组成原理','走进计算机内部，探索运算与存储。','COMING_SOON',2),
(3,'operating-systems','操作系统','理解资源如何有序协作。','COMING_SOON',3),
(4,'computer-networks','计算机网络','跟随一条消息，连接更大的世界。','COMING_SOON',4);

INSERT INTO chapter(id,subject_id,parent_id,name,sort_order) VALUES
(1,1,NULL,'绪论和基本概念',1),(2,1,NULL,'线性表',2),(3,1,NULL,'栈、队列和数组',3),
(4,1,NULL,'树与二叉树',4),(5,1,NULL,'图',5),(6,1,NULL,'查找',6),(7,1,NULL,'排序',7);

INSERT INTO knowledge_point(id,chapter_id,name,introduction,definition,plain_explanation,key_conclusions,misconceptions,resource_status,review_status,content_source,syllabus_version) VALUES
(201,2,'顺序表与单链表','同样是一列数据，存储方式可以很不同。','顺序表用一段连续存储空间依次存放线性表元素；单链表由数据域和指向后继的指针域构成结点，通过链接表示线性关系。','顺序表像连续编号的格子；单链表像各自带有下一站地址的纸条。生活类比不等同于实际内存布局。','在定长元素和随机访问模型下，顺序表按下标访问为 O(1)；单链表从头查找任意下标的最坏情况为 O(n)。顺序表中间插入最坏需要 O(n) 次移动；单链表在已知前驱且结点分配视为常数时，链接插入为 O(1)，但查找前驱通常需要 O(n)。','不能脱离操作条件说链表总比顺序表快；逻辑相邻不代表物理相邻。','DEMO','DRAFT',@source,@version),
(301,3,'栈：后进先出','从一叠托盘里，发现受限的访问顺序。','栈是仅允许在一端进行插入和删除操作的线性表；这一端称为栈顶，另一端称为栈底。','只从顶端放入和取出托盘：最后放上的，会最先被取出。','后进先出（LIFO）是访问规则，与采用数组还是链表实现无关。本题顺序入栈且期间没有出栈，因此全部入栈后再连续出栈的顺序与入栈顺序相反。','并非任何入栈、出栈交错序列都只是完整逆序；栈也不要求连续内存。','DEMO','DRAFT',@source,@version),
(302,3,'队列：先进先出','在取餐窗口，让先到的人先被服务。','队列是只允许在一端插入、在另一端删除的线性表；插入端称为队尾，删除端称为队头。','在队尾加入，在队头离开，先进入的先离开。','普通队列遵循先进先出（FIFO）；入队和出队是不同端的操作。物理存储既可以使用顺序结构，也可以使用链式结构。','优先队列不是本题普通 FIFO 队列；现实排队有插队和优先服务，本题明确排除这些情况。','DEMO','DRAFT',@source,@version);

-- Full chapter skeleton, not a claim of complete syllabus coverage. Empty entries never unlock cards.
INSERT INTO knowledge_point(id,chapter_id,name,introduction,definition,plain_explanation,key_conclusions,misconceptions,content_source,syllabus_version) VALUES
(101,1,'数据、数据元素与数据对象','建立数据结构的基本术语。','','','','',@source,@version),
(102,1,'逻辑结构与存储结构','区分数据关系与实际存储。','','','','',@source,@version),
(103,1,'算法与时间、空间复杂度','在明确条件下分析算法代价。','','','','',@source,@version),
(202,2,'线性表的定义与基本操作','认识线性表的逻辑特征。','','','','',@source,@version),
(203,2,'双链表与循环链表','进一步探索链式结构。','','','','',@source,@version),
(303,3,'栈与队列的应用','表达式、递归与层次处理。','','','','',@source,@version),
(304,3,'数组与特殊矩阵','下标映射与压缩存储目录。','','','','',@source,@version),
(305,3,'串的基本概念与模式匹配','串操作与模式匹配目录。','','','','',@source,@version),
(401,4,'树与二叉树的基本概念','从层次关系认识树。','','','','',@source,@version),
(402,4,'二叉树的存储与遍历','理解结点访问次序。','','','','',@source,@version),
(403,4,'线索二叉树','利用空指针记录遍历关系。','','','','',@source,@version),
(404,4,'树、森林与二叉树转换','不同层次表示的联系。','','','','',@source,@version),
(405,4,'哈夫曼树与编码','带权路径与编码目录。','','','','',@source,@version),
(501,5,'图的基本概念与存储','认识顶点、边和存储方式。','','','','',@source,@version),
(502,5,'图的遍历','深度优先与广度优先。','','','','',@source,@version),
(503,5,'最小生成树','连通图的生成树目录。','','','','',@source,@version),
(504,5,'最短路径','路径与边权的约束。','','','','',@source,@version),
(505,5,'拓扑排序与关键路径','有向无环图中的任务关系。','','','','',@source,@version),
(601,6,'顺序查找与折半查找','查找条件与比较次数。','','','','',@source,@version),
(602,6,'二叉排序树与平衡二叉树','有序查找树目录。','','','','',@source,@version),
(603,6,'B 树与 B+ 树','多路平衡查找树目录。','','','','',@source,@version),
(604,6,'散列表','散列函数、冲突与查找。','','','','',@source,@version),
(701,7,'插入排序与希尔排序','基于插入的排序目录。','','','','',@source,@version),
(702,7,'冒泡排序与快速排序','基于交换的排序目录。','','','','',@source,@version),
(703,7,'选择排序与堆排序','选择与堆结构目录。','','','','',@source,@version),
(704,7,'归并排序与基数排序','归并与分配策略目录。','','','','',@source,@version),
(705,7,'外部排序与性能比较','内外存条件与排序指标。','','','','',@source,@version);

INSERT INTO knowledge_relation VALUES
(201,102,'PREREQUISITE'),(201,202,'PREREQUISITE'),(301,202,'PREREQUISITE'),(302,202,'PREREQUISITE'),
(301,302,'RELATED'),(302,301,'RELATED'),(201,203,'RELATED');

INSERT INTO story(id,title,scene,introduction,illustration,resource_status,review_status,content_source,syllabus_version) VALUES
(1,'星光书屋的寻书委托','傍晚 · 星光书屋','书屋给每本书分配了固定大小的记录，编号从 0 连续递增。店长需要频繁按编号读取任意一条记录，今天不做插入或删除。你要为计算机中的记录选择一种存储结构。窗外亮起第一盏灯，店长把设计本递给了你。','library','DEMO','DRAFT',@source,@version),
(2,'云朵咖啡馆的托盘','午后 · 云朵咖啡馆','打烊前，你将编号 A、B、C 的三个托盘按 A → B → C 的顺序逐个放到同一叠托盘的顶端，中间没有取出任何托盘。现在只能从顶端逐个拿取，且不允许从中间抽走。第一位客人走到柜台前。','cafe','DEMO','DRAFT',@source,@version),
(3,'月台前的热可可','清晨 · 森林小站','小站的热可可窗口刚刚开放。小禾、阿岚、木木依次来到队尾，所有人都还没被服务。规则是先到先服务，只在队尾加入、队头离开，没有插队或优先服务。你负责叫下一位旅客取餐。','station','DEMO','DRAFT',@source,@version);

INSERT INTO story_knowledge VALUES (1,201),(2,301),(3,302);
INSERT INTO question(id,story_id,prompt,sort_order) VALUES
(11,1,'在定长记录和随机访问模型下，要按编号在 O(1) 时间直接访问任意记录，下列哪种结构最符合需求？',1),
(21,2,'连续取出这三个托盘，顺序应该是什么？',1),
(31,3,'按照当前规则，第一位应该叫谁？这种规则对应哪种结构？',1);

INSERT INTO question_option(id,question_id,label,name,term,plain_meaning,is_correct,reason,misconception,feedback,content_source,syllabus_version) VALUES
(111,11,'A','顺序表','顺序表','定长元素连续存放，可通过首地址和下标定位。',TRUE,'连续存储且元素大小固定时，可以计算目标地址，按下标访问为 O(1)。本题不涉及扩容或插入删除。','随机访问指按下标直接定位，并非随机挑选；也不意味着按内容搜索为 O(1)。','你在设计本上画出连续编号的记录格。店长按编号直接定位，找书清单很快准备好了。',@source,@version),
(112,11,'B','单链表','单链表','结点通过指向后继的链接连接。',FALSE,'一般需要从头沿链接寻找任意编号对应的结点，最坏为 O(n)，不能满足任意下标 O(1) 访问。','已知结点后的链接插入可为 O(1)，不等于按位置查找也为 O(1)。','你沿着每条记录的下一站逐个寻找。店长发现：要找靠后的编号，还是得从头走一遍。',@source,@version),
(113,11,'C','栈','栈','从同一端放入和取出，后进先出。',FALSE,'栈的抽象接口限制在栈顶操作，不提供任意下标随机访问操作。','栈可用数组实现，但数组的随机访问能力不属于栈抽象接口。','你把记录当成一叠纸，只能先拿到顶上那份。店长想直接找中间编号，这套规则帮不上忙。',@source,@version),
(114,11,'D','队列','队列','队尾进入、队头离开，先进先出。',FALSE,'普通队列提供两端受限操作，不提供任意编号直接访问的抽象操作。','按到达次序处理，和按编号定位是两种不同需求。','你让记录依次排队，但店长要的编号还在队伍中间。排队规则适合依次处理，不适合这次任务。',@source,@version),
(211,21,'A','A → B → C','先进先出顺序','最先放入的先取出。',FALSE,'A 位于底部，题目规定只能从顶端拿取。该顺序符合普通队列的先进先出，而不是当前栈规则。','托盘入栈时没有交错出栈，不能先取出最底下的 A。','你试着取 A，却发现它压在最下面。只从顶端取，必须先拿走上面的托盘。',@source,@version),
(212,21,'B','C → B → A','栈的后进先出','最后放到顶端的 C 最先取出。',TRUE,'C 最后入栈处于栈顶，取走 C 后 B 成为栈顶，最后才轮到 A。','这里逆序成立的条件是全部入栈后才连续出栈；不要推广到任意交错操作。','你轻轻拿起最上面的 C，接着是 B，最后是 A。三个托盘顺利摆上了柜台。',@source,@version),
(213,21,'C','B → C → A','违反栈顶约束的序列','尝试先拿中间的 B。',FALSE,'全部入栈后 B 被 C 压住，第一次出栈不可能是 B。','现实可以抽取中间物体，但抽象栈不允许这样的操作。','B 被 C 压着。从中间抽走虽然现实中可能做到，却违反了这次只能从顶端拿的规则。',@source,@version),
(214,21,'D','C → A → B','违反后续栈顶约束的序列','先取 C 后尝试越过 B 取 A。',FALSE,'第一步 C 正确，但取出 C 后 B 才是栈顶，不能跳过 B 直接取 A。','检查整个操作序列，不能只看第一个出栈元素。','拿走 C 之后，你看见栈顶变成了 B。想拿 A，还得先经过 B。',@source,@version),
(311,31,'A','小禾；队列','队列的先进先出','最早进入队伍的人先离开。',TRUE,'小禾最先入队且尚未出队，因此当前位于队头；先到先服务符合普通 FIFO 队列。','先进先出说的是入队与出队的相对顺序，并不表示内存必须连续。','你叫到小禾的名字。热可可从窗口递出，队伍向前移动了一步，每个人都知道自己会按顺序被叫到。',@source,@version),
(312,31,'B','木木；栈','栈的后进先出','最后到的人最先离开。',FALSE,'木木最后到，先叫木木相当于采用后进先出，与本题先到先服务冲突。','栈与队列的区别是操作规则，不是队伍看起来是否排成一行。','木木指了指前面的两位旅客：他们来得更早。你意识到，最后到先服务并不是窗口的规则。',@source,@version),
(313,31,'C','阿岚；优先队列','优先队列','按照指定优先级选择元素。',FALSE,'本题没有给阿岚更高优先级，并明确没有优先服务，不能自行增加排序规则。','优先队列按优先级出队，不能与普通 FIFO 队列混为一谈。','阿岚有些意外：这里没有特殊优先级。你重新翻看规则，发现大家约定的是先到先服务。',@source,@version),
(314,31,'D','任意一人；顺序表','顺序表与访问规则','顺序表是存储方式，不能单独决定服务顺序。',FALSE,'即使队列底层用顺序结构实现，仍必须遵守队头出队。任意叫人不符合规则。','不要混淆存储结构与抽象数据类型的访问约束。','你准备随意叫一位旅客，大家却提醒你看队头。怎样存放名单，不能改变已经约定的先后顺序。',@source,@version);

INSERT INTO knowledge_card(id,knowledge_id,diagram_type,content_source,syllabus_version) VALUES
(1,201,'list',@source,@version),(2,301,'stack',@source,@version),(3,302,'queue',@source,@version);
