# ScaleSketchPenPad
可以缩放的绘画板工具
#Android绘画板SketchPad工具

<font size=3>周末没什么事情，写了一个画图板Demo（Sketchpad），发出来与大家分享



1. 线条：可以选择颜色
2. 橡皮擦：擦除
3. undo ：撤销功能
4. 背景图片
5. 自定义相机，拍照返回
6. 保存最终图像



如果只是简单的绘图板，那么就没必要拿出来分享了

<font size=4>7. 最重要的是，整个工具可以**放缩（scale）**操作

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/master/scalesketchpad.png)


<font size=4>借此分享 我对**Android事件传递机制**的看法

>默认您对dispatchTouchEvent，onTouchEvent有初步的认知

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/5d1d176264273d6621efc85aa2945ee0894be405/importantOntouch.png)

##绘图板介绍

<font size=5>**初始界面**

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/09404f2b7db3339c18ecfb46a4f28195e57c1e4a/skechpen1.png)


<font size=5>**选择画笔**

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/7b058ac3c64c709bb2f494791b8301870857dd01/skechpen2.png)


<font size=5>**选择粗细**

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/7b058ac3c64c709bb2f494791b8301870857dd01/skechpen3.png)


<font size=5>**放缩操作**

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/master/scalesketchpad.png)

<font size=5>**自定义相机**

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/7b058ac3c64c709bb2f494791b8301870857dd01/skechpen4.jpg)


##详细设计

![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/da965320fd468a54f8d98698ffdf2927c17f7c35/umlborderview.png)


![](https://raw.githubusercontent.com/ShaunSheep/BlogGifRes/da965320fd468a54f8d98698ffdf2927c17f7c35/borderview.png)

##如何实现？

技术点：

1. 画笔的细节不说了，可以去Paint  API找
2. 橡皮擦的效果 Paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

	PorterDuff.Mode一共有16八种混合模式，我全都试过，有兴趣的可以试试效果
	
	本项目使用 PorterDuff.Mode.DST_CLEAR 实现橡皮擦效果

3. 绘制过程使用双缓存，具体可以看LineView中onDraw实现，以解决“黑线条”的Bug
4.  分层思想：

	- Borderview负责手势的分发dispatch和处理ontouch
	- Borderview中包含Imageview展示图片
                    Lineview画线操作
    - LineView负责接收单手手势，创建MarkPath对象，完成单手操作
    - MarkPath是最终具备绘画能力的对象，所有的绘画功能都定制在MarkPath中，包含每个Point的设计，Path的设计，详情看Demo
    

5. 手势细节：

  - 双手缩放：Borderview的dispatchTouchEvent分发手势
  	 -   通过 case MotionEvent.ACTION_DOWN:
                return mLineView.onTouchEvent(event);将手势传递给LineView，
	 - 在MotionEvent.ACTION_UP时，控制LineView和Imageview缩放
  - 单手触摸画线的细节：业务逻辑都在onTouch中
 		- 一是双手触摸（MotionEvent.ACTION_POINTER_DOWN:）的时候break结束此次手势操作
		- 二是单手触摸，通过（MotionEvent.ACTION_MOVE）进行画线操作

6. 加入缩放效果后的难点：
	- 背景图片如何居中，
	- 背景如何不越界（超出屏幕范围）
	- 缩放后，线条（Path）如何保持原有的宽度（StrockWidth）进行绘制
	- 缩放后，绘制线条偏移解决方案
5. MarkPath所体现的面向对象的思想
	
	本实例中，具备绘画功能的是MarkPath对象，Borderview和Lineview只是分发处理手势事件而已
  

6. 自定义相机 详情可以看我的博客：

	[http://blog.csdn.net/chivalrousman/article/details/51890716](http://blog.csdn.net/chivalrousman/article/details/51890716 "自定义相机")

7. 读取系统相册的图片 可以参考我的博客:

	[http://blog.csdn.net/chivalrousman/article/details/51771164](http://blog.csdn.net/chivalrousman/article/details/51771164 "读取系统相册")

10. 写本项目的兴趣，来源于6年前的一篇博客：

	
	[http://blog.sina.com.cn/s/blog_5f8817250100lqk0.html](http://blog.sina.com.cn/s/blog_5f8817250100lqk0.html)

