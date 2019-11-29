萌幻Cos Android
=========================
[![Build Status](https://travis-ci.org/walkthehorizon/MicroWallPager.svg?branch=master)](https://travis-ci.org/walkthehorizon/MicroWallPager)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

一个基于Android开发的轻量且精致的Cos美图浏览客户端

简介
------------

**后台**：为该项目提供后台相关的服务，如接口，数据管理后台，基于Django,暂未开源

**数据**：基于Scrapy自动化爬取网络，部署在阿里云ECS上，每日会根据计划自动化爬取相关数据，暂未开源  

**官网**：基于React，已开源，暂时用于app的下载，app分享支持以及联动还原分享页场景，有兴趣可点击查看[micro-react](https://github.com/walkthehorizon/micro-react)


开始使用
---------------
[点击下载安装应用](https://wmmt119.top/)

截图
-----------

![image](screenshots/555b2a6665253a9c0ba59cdf78bb2e2.jpg)
![image](screenshots/27cb095c5eda51e6e56e40cec2491fb.jpg)
![image](screenshots/2a1b4f9a10cd5d4cff6733beff77f2b.jpg)

核心库
--------------
* [Glide][0] - Glide是一个快速高效的Android图片加载库，注重于平滑的滚动。
* [android-gif-drawable][1] - 一个基于giflib高效加载gif图片的开源库，对于gif的性能表现极好，因为同一时间只会有一帧的内存消耗。
* [subsampling-scale-image-view][2] - 一个高度可配置，易于扩展的深度缩放视图，可显示巨大图像而不会丢失细节。
* [BigImageViewer][3] - BigImageViewer封装了android-gif-drawable和subsampling-scale-image-view的使用细节，
提供对Fresco, Glide, and Picasso的支持。
* [lottie][4] - Lottie是适用于Android和iOS的移动库，它可以使用Bodymovin解析以json格式导出的Adobe After Effects动画，并在移动设备上进行本地渲染！

[0]: https://github.com/bumptech/glide
[1]: https://github.com/koral--/android-gif-drawable
[2]: https://github.com/davemorrissey/subsampling-scale-image-view
[3]: https://github.com/Piasy/BigImageViewer
[4]: https://github.com/airbnb/lottie-android

未来...
-----------------
目前主要有以下计划：
- 图片的压缩和裁剪问题，在有限的条件下，目前这方面都仍有很大的改进空间
- 将该项目完全升级为Kotlin
- 基于Flutter实现该项目，以期提供对ios系统的支持

此外，如果你有好的建议，请提交新的 [issue](https://github.com/walkthehorizon/MicroWallPager/issues)

为什么下载要收费？
-------------------

萌幻Cos致力于打造一个自由开源的Cos图文浏览环境，图源来自于爬虫自动化收集，但为防图片被下载后用于非法用途，所以增加下载限制，即下载需要扣除少量看豆。
此外，该项目完全由我个人在工作之余独立维护，每个月都会产生服务器，数据库，存储，流量等额外的费用，目前完全由我个人承担，
如果你觉得这个项目对你有用，可适当投喂或点击star以支持萌幻Cos未来更好更强壮的发展，谢！

支持
-------
QQ：1308311472  
微信：walkthehorizon

如果你发现应用内有任何错误，请创建并提交 [issue](https://github.com/walkthehorizon/MicroWallPager/issues)

历史版本
------------------------
- *1.1.0*   
暂无计划

- *1.0.0*  
初版,已发布


License
-------

Copyright 2018 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
