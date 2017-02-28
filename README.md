#swordfish 编译方法

##1. 下载DataX源码
```
$ git clone https://github.com/baifendian/swordfish.git
```

##2. 通过maven打包
```
  $ cd  {swordfish_source_code_home}
  $ mvn -U clean package assembly:assembly -Dmaven.test.skip=true
```

打包成功，日志显示如下：
```
  [INFO] BUILD SUCCESS
  [INFO] -----------------------------------------------------------------
  [INFO] Total time: 4.385 s
  [INFO] Finished at: 2017-02-27T16:13:50+08:00
  [INFO] Final Memory: 52M/314M
  [INFO] -----------------------------------------------------------------
```