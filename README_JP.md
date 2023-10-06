# LizzieYzy - 囲碁AI用グラフィカルユーザーインターフェース
![screenshot_ko](/screenshot_jp.png?raw=true)

LizzieYzyは、 [Lizzie](https://github.com/featurecat/lizzie)を元に改良されたもので、 [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) などの様々なGTPエンジンをロードすることができます。

Lizzieをベースに、いくつかの新機能が追加されました。: **ホークアイ, フラッシュ分析, バッチ分析, 形勢分析, 碁盤同期機能(Windows限定), エンジン同士の対局, 詰碁解析, ダブルエンジンモード, 視覚化されたKataGoの分散トレーニング** 及び、いくつかの詳細な変更により高解像度が完全にサポートされ、システムのスケーリングによって表示がぼやけることがありません。
#
* 新しく追加された機能

  * **ホークアイ**: AIによる勝率・計算量・目差を元に、棋譜上の実際の手と比較し、一致率、勝率変動、目差変動、悪手などの情報を取得し、グラフィック形式で表示されます。

  * **フラッシュ分析**: KataGoの分析エンジンを使用して、複数の棋譜をまとめて高速に分析することができます。

  * **バッチ分析**: GTPエンジンまたはKataGoの分析エンジンを用いて、複数の棋譜をまとめて分析することができます。

  * **形勢分析**: KataGoの `kata-raw-nn` や ZenGTPの `territory` コマンドを使用して、おおまかな地合い判定をします。

  * **碁盤同期機能(C#)**: [リポジトリ](https://github.com/yzyray/readboard) 任意の碁盤の画像をLizzieYzyに取り込むことができます。また、野狐、弈城、新浪のプラットフォームとの連携も可能で、これらは特別に最適化されています。C#言語を使用するため、Windows限定の機能です。

  * **碁盤同期機能(Java)**: [リポジトリ](https://github.com/yzyray/readboard_Boofcv) 任意の碁盤の画像をLizzieYzyに取り込むことができます。

  * **エンジン同士の対局**: エンジン同士の対局が可能です。複数の対局を連続で行うこともできます。着手のための様々なコマンドをサポートしています: `lz-analyze`, `kata-analyze`, `genmove`。複数の対局を行った場合は、終了時にEloレーティングや標準偏差等の統計データが自動的に保存されます。

  * **詰碁解析**: 詰碁の画像をキャプチャーして取り込むことができます。分析を詰碁の部分に集中させるために、他の領域に多くの石を自動的に配置させることができます。上部ツールバーの「分析」→「詰碁フレーム」または「詰碁をキャプチャー」、もしくは上部メニューの「詰碁」を参照してください。

  * **ダブルエンジンモード**: 2つのエンジンをロードして2画面で比較検討することができます。

  * **視覚化されたKataGoの分散トレーニング**: KataGoの分散トレーニングに参加することができます。対局状況（盤面）を見ることができます。

#
 * [LizzieYzyの使い方（英語版）](https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf)
 * [LizzieYzyの使い方（日本語版）](https://www.h-eba.jp/Lizzie/LizzieYzy/manual.html)
 * その他のjarファイルのソースコードのリンク: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [CaptureTsumeGo.jar](https://github.com/yzyray/captureTsumeGo/blob/main/README.md)
