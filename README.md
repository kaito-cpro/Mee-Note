# Mee-Note
simple note application for Android

# Main Files
MainActivity.java  
CanvasView.java  
activity_main.xml  
AndroidManifest.xml

# Features
* 黒と赤の線を書けます
* 下部のボタンでページの移動、undo、色変更、消しゴム持ち替えができます
* 右側の赤いボタンは座標補正用です
* 右側の水色のボタンはマーカー機能です  
一時的に青い線を書くことができて、もう一度ボタンを押すと青い線は消えます
* 直径が小さい線(パス上の2点間の距離の最大値が小さい線)を書くと点と解釈されて点が強調されて描画されます  
これは数学で点を頻繁に使用するので点を大きく見やすくするための仕様です
<img src="https://user-images.githubusercontent.com/39236776/123450211-75919880-d617-11eb-8428-c59653a6c758.jpg" width=70%>
