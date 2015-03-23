操作流程
========

接SDK
=====

#. 用测试游戏母包进行测试

打游戏包
========

#. 打母包

#. 转换SDK包

Under The Hood
==============

母包
----

按照SDK规范写好的，能运行的基本包。

* 资源
* c++ so
* 基本java库

SDK包
-----

* AndroidManifest.xml
* 额外 assets
* 额外 res
* 额外 java (MainActivity.java)

替换
----

* 替换 AndroidManifest.xml
* 合并 assets
* 合并 res
* 合并 java库
