# LizzieYzy - 围棋引擎界面(中文,[English](#en),[日本語](https://github-com.translate.goog/yzyray/lizzieyzy/blob/main/README_EN.md?_x_tr_sl=en&_x_tr_tl=ja&_x_tr_pto=wapp),[한국어](#ko))
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

  * **Estimate**: Use Katago(default)'s command:`kata-raw-nn` or ZenGTP's command `territory` to get raw territory,support automatically estimate after each move.

  * **Board Synchronization(C#)**: [Repository](https://github.com/yzyray/readboard) Two mode: foreground(board can't be moved or covered)/backgorund, optimize for FoxWQ、TYGEM、SINA platform allow sync by click a button, when synchronizing from other platform or a picture or a movie you need to select the rigon of the board, support automatically carry moves for both sides(developed by C#, only support windows).

  * **Board Synchronization(Java)**: [Repository](https://github.com/yzyray/readboard_Boofcv) Foreground only, need select the rigon contains the board, support automatically carry moves for both sides.

  * **Engine Game**: Allow a game or multiple games bettween two engines, support load some SGF files as opening books, support various commands:`lz-analyze`, `kata-analyze`, `genmove` to get moves, multiple games will collect some statistics: elo, stdev interval and etc.

  * **Tsumego Analysis**: Support capture tsumego in part of goban, and automatically generate other part of stones help engine analyze in right area, refer to [Analyze]-[Tsumego frame] or [Capture tsumego] or [Tsumego] button in toolbar.

  * **Double Engine Mode**: Support load two engins and analyze synchronously, convenient for comparison.

  * **Visualized KataGo Distributed Training**: Visualized official KataGo training, all games(playing or completed) can be watched.

#
 * [Instruction for use](https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf)
 * Other jar source code links: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [CaptureTsumeGo.jar](https://github.com/yzyray/captureTsumeGo/blob/main/README.md)

#
<span id="ko"></span>
# LizzieYzy - 바둑 대국용 GUI
![screenshot_ko](/screenshot_ko.png?raw=true)

LizzieYzy는 [Lizzie](https://github.com/featurecat/lizzie)를 기반으로 [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) 등의 다른 GTP engine들을 로드할 수 있도록 수정된 그래픽 인터페이스입니다.

Lizzie 기반에 몇 가지 새로운 기능 추가: **Hawk Eye, Flash Analyze, Batch Analyze, Estimate, Board Synchronization(only windows), Engine Game, Upload And Share, Double Engine Mode, Visualized KataGo Distributed Training** 및 몇몇 세부 사항 조정, 고해상도를 완벽하게 지원하여 시스템 스케일링으로 인해 흐릿하게 표시되지 않습니다.
#
* 새로운 기능

  * **Hawk Eye**: 정확도, 승률 차이, 점수 차이, 엔진 후보 수와 실제 착수의 차이를 기반으로 한 실착수를 찾아서 차트에 표시.

  * **Flash Analyze**: Katago의 분석 모드를 사용함. 모든 기보를 병렬로 분석. 승률 그래프 후보를 빠르게 획득. 일괄 분석 지원.

  * **Batch Analyze**: GTP engine 또는 Katago의 분석 모드를 사용한 다수 기보 일괄 분석 지원.

  * **Estimate**: Katago(기본값)의 `kata-raw-nn` 명령 또는 ZenGTP의 `territory` 명령을 사용하여 현재 집 수 계산. 각 착수 후의 집 수 자동 예측을 지원.

  * **Board Synchronization(C#)**: [Repository](https://github.com/yzyray/readboard) 두 가지 모드: 전경(바둑판을 이동하거나 덮을 수 없음)/배경. FoxWQ, TYGEM, SINA 플랫폼에 최적화. 버튼을 클릭하여 동기화를 허용. 다른 플랫폼이나 사진 또는 착수를 통한 동기화 시 바둑판이 포함된 영역을 선택해야 함. 양방향 자동 착수 전달 지원. C#으로 개발되어서 Windows만 지원.

  * **Board Synchronization(Java)**: [Repository](https://github.com/yzyray/readboard_Boofcv) 전경 전용. 바둑판이 포함된 영역을 선택해야 함. 양방향 자동 착수 전달 지원.

  * **Engine Game**: 두 엔진 간의 1회(또는 다회) 대국을 수행. 대국 시작점으로 일부 sgf를 사용할 수 있습니다. 착수를 위한 다양한 명령 지원: `lz-analyze`, `kata-analyze`, `genmove`. 다회 대국시에 몇 가지 통계를 수집합니다: elo, stdev 간격 등.

  * **Upload And Share**: [Repository](https://github.com/yzyray/LizziePlayer) 기보를 [LizziePlayer](http://lizzieyzy.cn)로 업로드하여 휴대폰, 패드와 같은 다른 기기에서 보기 지원. LizziePlayer가 후보수를 표시하고, 승률 그래프, 정확도, 실착수 검색을 지원.

  * **Double Engine Mode**: 2개 엔진을 로드하여 동시 분석을 지원. 비교에 편리합니다.

  * **Visualized KataGo Distributed Training**: 시각화된 공식 KataGo 훈련. 모든 대국(진행중이거나 완료된)을 시청할 수 있습니다.

#
 * [사용 지침(영문)](https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf)
 * 기타 jar 소스 코드 링크: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [CaptureTsumeGo.jar](https://github.com/yzyray/captureTsumeGo/blob/main/README.md)
