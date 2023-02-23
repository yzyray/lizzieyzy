# LizzieYzy - 바둑 대국용 GUI
![screenshot_ko](/screenshot_ko.png?raw=true)

LizzieYzy는 [Lizzie](https://github.com/featurecat/lizzie)를 기반으로 [Katago](https://github.com/lightvector/KataGo)、[LeelaZero](https://github.com/leela-zero/leela-zero)、[Leela](https://github.com/gcp/Leela)、[ZenGTP](https://github.com/yzyray/ZenGTP)、[SAI](http://sai.unich.it)、[Pachi](https://github.com/pasky/pachi) 등의 다른 GTP engine들을 로드할 수 있도록 수정된 그래픽 인터페이스입니다.

Lizzie 기반에 몇 가지 새로운 기능 추가: **Hawk Eye, Flash Analyze, Batch Analyze, Estimate, Board Synchronization(only windows), Engine Game, Upload And Share, Double Engine Mode, Visualized KataGo Distributed Training** 및 몇몇 세부 사항 조정.  레티나 모니터를 지원하며 크기 조정에 의해 흐려지지 않습니다.
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
 * 사용 지침: https://github.com/yzyray/lizzieyzy/blob/main/readme_en.pdf (일본어나 한국어로 번역본을 읽고 있는 경우. 원본 링크로 이동해주세요. 번역된 링크는 작동하지 않습니다)
 * 기타 jar 및 exe 소스 코드 링크: [foxRequestQ.jar](https://github.com/yzyray/FoxRequest) [InVisibleFrame.jar](https://github.com/yzyray/testbuffer) [SubProcessHandler.exe](https://github.com/yzyray/SubProcessHandler)
