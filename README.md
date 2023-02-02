# LizzieYzy - 围棋引擎界面(中文,[English](#en),[日本語](https://github-com.translate.goog/yzyray/lizzieyzy/blob/main/README_EN.md?_x_tr_sl=en&_x_tr_tl=ja&_x_tr_pto=wapp),[한국어](https://github-com.translate.goog/yzyray/lizzieyzy/blob/main/README_EN.md?_x_tr_sl=en&_x_tr_tl=ko&_x_tr_pto=wapp)
![screenshot](/screenshot.png?raw=true)

LizzieYzy 是一个引擎界面,修改自[Lizzie](https://github.com/featurecat/lizzie),可加载围棋引擎:[Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi)以及其他标准GTP引擎。

在Lizzie的基础上增加了一些新功能:**鹰眼分析,闪电分析,批量分析,形势判断,棋盘同步,引擎对局,死活题分析,双引擎模式,可视化KataGo分布式训练**,以及一些细节修改,可完美支持高分辨率,不会因为系统缩放而显示模糊
#
* 新功能

  * **鹰眼分析**: 根据AI的选点胜率、计算量、目差,与棋谱中实际落子做比较,得出吻合度,胜率波动,目差波动,失误手等信息并以图表化的形式展示

  * **闪电分析**: 使用Katago的analysis模式,并行分析整个棋谱,快速得出胜率图,选点等信息,支持批量分析

  * **批量分析**: 支持打包棋谱按顺序使用GTP引擎分析,或使用Katago的analysis模式分析

  * **形式判断**: 使用Katago(默认)的`kata-raw-nn`命令或ZenGTP的`territory`命令获取粗略的领地判断,支持每一步自动形势判断

  * **棋盘同步(C#)**: [相关仓库](https://github.com/yzyray/readboard)前台(不可移动,遮挡)/后台(不占用鼠标,可遮挡)两种模式,特别优化了野狐、弈城、新浪平台可一键同步,其他平台或图片动画等需框选棋盘(将棋盘选在内即可,比棋盘大很多也没关系),支持双向同步、自动落子(溜狗),采用C#语言,因此只支持Windows

  * **棋盘同步(Java)**: [相关仓库](https://github.com/yzyray/readboard_Boofcv)仅前台,需框选棋盘(选择比棋盘大一些的区域),支持双向同步、自动落子(溜狗)

  * **引擎对局**: 两个引擎之间的单盘/多盘对局,可加载多个SGF作为开局,支持使用不同命令获取引擎选点:`lz-analyze`、`kata-analyze`、`genmove`,多盘对局将会自动计算elo、标准差区间等信息

  * **死活题分析**: 支持抓取局部棋盘上的死活题,并自动生成死活题框架以便AI在正确的范围内思考,详见[菜单]-[分析]-[死活题]以及[抓取死活题],或工具栏的最右侧[死活]按钮

  * **双引擎模式**: 支持同时加载两个引擎并同步分析对比

  * **可视化KataGo分布式训练**: 将KataGo官方的分布式训练可视化,可以看到每一局正在进行和已经训练完成的对局
#
 * [使用简介](https://github.com/yzyray/lizzieyzy/blob/main/readme_cn.pdf) 
 * 其他用到的jar代码链接: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [CaptureTsumeGo.jar](https://github.com/yzyray/captureTsumeGo/blob/main/README.md)

#
<span id="en"></span>
# LizzieYzy - GUI for Game of Go
![screenshot_en](/screenshot_en.png?raw=true)

LizzieYzy is a graphical interface modified from [Lizzie](https://github.com/featurecat/lizzie),allow loading various engines like: [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) or other GTP engines.

Add some new features on Lizzie's basis: **Hawk Eye, Flash Analyze, Batch Analyze, Estimate, Board Synchronization(only windows), Engine Game, Tsumego Frame, Double Engine Mode, Visualized KataGo Distributed Training** and ajust some details, support retina monitor, won't get fuzzy by scaled.
#
* New features

  * **Hawk Eye**: Get accuracy, winrate difference, score difference, blunder move based on the difference bettween engine candidates and actually move and displayed in chart.

  * **Flash Analyze**: Depend on Katago's analysis mode, analyze all kifus in parallel and get winrate graph candidates rapidly, support batch analyze.

  * **Batch Analyze**: Support batch analyze kifus by GTP engine or Katago's analysis mode.

  * **Estimate**: Use Katago(default)'s command:`kata-raw-nn or` ZenGTP's command `territory` to get raw territory, support automatically estimate after each move.	

  * **Board Synchronization(C#)**: [Repository](https://github.com/yzyray/readboard) Two mode: foreground(board can't be moved or covered)/backgorund, optimize for FoxWQ、TYGEM、SINA platform allow sync by click a button, when synchronizing from other platform or a picture or a movie you need to select the rigon of the board, support automatically carry moves for both sides(developed by C#, only support windows).

  * **Board Synchronization(Java)**: [Repository](https://github.com/yzyray/readboard_Boofcv) Foreground only, need select the rigon contains the board, support automatically carry moves for both sides.

  * **Engine Game**: Allow a game or multiple games bettween two engines, support load some SGF files as opening books, support various commands:`lz-analyze`, `kata-analyze`, `genmove` to get moves, multiple games will collect some statistics: elo, stdev interval and etc.

  * **Tsumego Analysis**: Support capture tsumego in part of goban, and automatically generate other part of stones help engine analyze in right area, refer to [Analyze]-[Tsumego frame] or [Capture tsumego] or [Tsumego] button in toolbar. 

  * **Double Engine Mode**: Support load two engins and analyze synchronously, convenient for comparison.

  * **Visualized KataGo Distributed Training**: Visualized official KataGo training, all games(playing or completed) can be watched.

#
 * [Instruction for use](https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf)
 * Other jar source code links: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [CaptureTsumeGo.jar](https://github.com/yzyray/captureTsumeGo/blob/main/README.md)
