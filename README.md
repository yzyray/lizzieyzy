# LizzieYzy - 围棋引擎界面
![screenshot](/screenshot.png?raw=true)

LizzieYzy 是一个引擎界面,修改自[Lizzie](https://github.com/featurecat/lizzie),可加载围棋引擎:[Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi)以及其他标准GTP引擎。

在Lizzie的基础上增加了一些新功能:`鹰眼分析`,`闪电分析`,`批量分析`,`形势判断`,`棋盘同步(仅支持windows)`,`引擎对局`,`上传分享`,`双引擎模式`,以及一些细节修改,可完美支持高分辨率,不会因为系统缩放而显示模糊

## 新功能

`鹰眼分析`: 根据AI的选点胜率、计算量、目差,与棋谱中实际落子做比较,得出吻合度,胜率波动,目差波动,失误手等信息并以图表化的形式展示

`闪电分析`: 使用Katago的analysis模式,并行分析整个棋谱,快速得出胜率图,选点等信息,支持批量分析

`批量分析`: 支持打包棋谱按顺序使用GTP引擎分析,或使用Katago的analysis模式分析

`形式判断`: 使用Katago(默认)的`kata-raw-nn`命令或ZenGTP的`territory`命令获取粗略的领地判断,支持每一步自动形势判断

`棋盘同步`: [相关仓库](https://github.com/yzyray/readboard) 前台(不可移动,遮挡)/后台(不占用鼠标,可遮挡)两种模式,特别优化了野狐、弈城、新浪平台可一键同步,其他平台或图片动画等需框选棋盘(将棋盘选在内即可,比棋盘大很多也没关系),支持双向同步、自动落子(溜狗),采用C#语言,因此只支持Windows

`引擎对局`: 两个引擎之间的单盘/多盘对局,可加载多个SGF作为开局,支持使用不同命令获取引擎选点:`lz-analyze`、`kata-analyze`、`genmove`,多盘对局将会自动计算elo、标准差区间等信息

`上传分享`: [相关仓库](https://github.com/yzyray/LizziePlayer) 支持上传棋谱到[LizziePlayer](http://lizzieyzy.cn)上,然后在手机、平板等移动端观看,LizziePlayer会展示选点、胜率图、吻合度等信息,可查找失误手

`双引擎模式`: 支持同时加载两个引擎并同步分析对比

##

Maven编译所需的额外文件上传在[这里](https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA?_at_=1623121084609#list/path=%2Fsharelink875943949-871813486621563%2F公开整合包%2Flib(编译用)&parentPath=%2Fsharelink875943949-871813486621563)

##

# LizzieYzy - Interface Of Go Engine

LizzieYzy is a graphical interface modified from [Lizzie](https://github.com/featurecat/lizzie),allowing load various engine: [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) or other GTP engines.

Add some new features on Lizzie's basis:`Hawk Eye`,`Flash Analyze`,`Batch Analyze`,`Estimate`,`Board Synchronization(only windows)`,`Engine Game`,`Upload And Share`,`Double Engine Mode` and ajust some details,support retina monitor,won't get fuzzy by scaled.

## New features

`Hawk Eye`: Get accuracy,winrate difference,score difference,blunder move based on the difference bettween engine candidates and actually move,and displayed in chart.

`Flash Analyze`: Depend on Katago's analysis mode,analyze all kifus in parallel,get winrate graph candidates rapidly,support batch analyze.

`Batch Analyze`: Support batch analyze kifus by GTP engin,or Katago's analysis mode.

`Estimate`: Use Katago(default)'s command:`kata-raw-nn or` ZenGTP's command `territory` to get raw territory,support automatically estimate after each move.	

`Board Synchronization`: [Repositories](https://github.com/yzyray/readboard) Two mode: foreground(board can't be moved or covered)/backgorund,optimize for FoxWQ、TYGEM、SINA platform allow sync by click a button,when sync from other platform or a picture or a movie you need select the rigon contains the board,support automatically carry moves for both sides,developed by C#,so only support windows.

`Engine Game`: Allow a game or multiple games bettween two engines,can use some sgfs as opening books,support various commands:`lz-analyze`,`kata-analyze`,`genmove` to get moves,multiple games will collect some statistics: elo,stdev interval and etc.

`Upload And Share`: [Repositories](https://github.com/yzyray/LizziePlayer) Support upload kifu to [LizziePlayer](http://lizzieyzy.cn),and view on other device like cellphone pad,LizziePlayer will display candidates,winrate graph,accuracy,and can search blunder moves.

`Double Engine Mode`: Support load two engins and analyze synchronously,convenient for comparison.

##

Put necessary jars for building maven [here](https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA?_at_=1623121084609#list/path=%2Fsharelink875943949-871813486621563%2F公开整合包%2Flib(编译用)&parentPath=%2Fsharelink875943949-871813486621563)
