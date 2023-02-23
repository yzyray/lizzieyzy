# LizzieYzy - GUI for Game of Go
![screenshot_en](/screenshot_en.png?raw=true)

LizzieYzy is a graphical interface modified from [Lizzie](https://github.com/featurecat/lizzie),allow loading various engines like: [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) or other GTP engines.

Add some new features on Lizzie's basis: **Hawk Eye, Flash Analyze, Batch Analyze, Estimate, Board Synchronization(only windows), Engine Game, Upload And Share, Double Engine Mode, Visualized KataGo Distributed Training** and ajust some details,support retina monitor,won't get fuzzy by scaled.
#
* New features

  * **Hawk Eye**: Get accuracy,winrate difference,score difference,blunder move based on the difference bettween engine candidates and actually move,and displayed in chart.

  * **Flash Analyze**: Depend on Katago's analysis mode,analyze all kifus in parallel,get winrate graph candidates rapidly,support batch analyze.

  * **Batch Analyze**: Support batch analyze kifus by GTP engine or Katago's analysis mode.

  * **Estimate**: Use Katago(default)'s command:`kata-raw-nn` or ZenGTP's command `territory` to get raw territory,support automatically estimate after each move.

  * **Board Synchronization(C#)**: [Repository](https://github.com/yzyray/readboard) Two mode: foreground(board can't be moved or covered)/backgorund,optimize for FoxWQ、TYGEM、SINA platform allow sync by click a button,when sync from other platform or a picture or a movie you need select the rigon contains the board,support automatically carry moves for both sides,developed by C#,so only support windows.

  * **Board Synchronization(Java)**: [Repository](https://github.com/yzyray/readboard_Boofcv) Foreground only,need select the rigon contains the board,support automatically carry moves for both sides.

  * **Engine Game**: Allow a game or multiple games bettween two engines,can use some sgfs as opening books,support various commands:`lz-analyze`,`kata-analyze`,`genmove` to get moves,multiple games will collect some statistics: elo,stdev interval and etc.

  * **Upload And Share**: [Repository](https://github.com/yzyray/LizziePlayer) Support upload kifu to [LizziePlayer](http://lizzieyzy.cn),and view on other device like cellphone pad,LizziePlayer will display candidates,winrate graph,accuracy,and can search blunder moves.

  * **Double Engine Mode**: Support load two engins and analyze synchronously,convenient for comparison.

  * **Visualized KataGo Distributed Training**: Visualized official KataGo training,all games(playing or completed) can be watched.

#
 * Instruction for use: https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf (If you are reading under translate to Japanese or Korean,please go to original link, translated link will not work)
 * Other jar and exe source code links: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [SubProcessHandler.exe](https://github.com/yzyray/SubProcessHandler)
