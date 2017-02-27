#swordfish 编译方法

##1. 下载DataX源码：
```
$ git clone https://github.com/baifendian/swordfish.git
```

##2. 通过maven打包：
```
  $ cd  {swordfish_source_code_home}
  $ mvn clean package assembly:assembly -Dmaven.test.skip=true
```

打包成功，日志显示如下：
```
  [INFO] BUILD SUCCESS
  [INFO] -----------------------------------------------------------------
  [INFO] Total time: 08:12 min
  [INFO] Finished at: 2015-12-13T16:26:48+08:00
  [INFO] Final Memory: 133M/960M
  [INFO] -----------------------------------------------------------------
```