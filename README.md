## 音视频入门学习：

### 1.在 Android 平台使用 AudioRecord 和 AudioTrack API 对音频 PCM 数据的采集和播放，并实现读写音频 wav 文件。

知识点：

1.了解什么是PCM

PCM 就是将声波信号符号化，因为在计算机中只有0和1的存在。PCM 没有经过任何编码与压缩。

2.了解什么是wav 文件

wav 文件是在PCM的基础上，经过初步编码添加头部信息，就变成了wav文件。

在Android Api中，提供了两种api进行音频开发。



### 2.SurfaceView和TextureView的对比

##### SurfaceView：

它继承自类View，因此它本质上是一个View。但是它在服务端有单独的一个service，所以可以在子线程中更新画面。因为SurfaceView不在我们传统的view hierachy结构中，所以view的一些属性，比如平移，缩放等变换也不能使用。

##### 优点和缺点：

1.可以在单独的线程中进行UI更新，使用双缓冲技术，可以是2D画面更加刘畅

2.不能使用view的一些属性，比如平移，缩放等变换。

##### TextureView：





