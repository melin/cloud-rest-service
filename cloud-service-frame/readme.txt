开发环境使用jetty plugin运行
mvn jetty:run

测试和生产环境使用嵌入式tomcat运行，使用下面命令打包iflySatis-0.0.1-SNAPSHOT-bin.tar.gz，解压后、执行bin/server.sh 启动服务。
mvn install assembly:single

更新java代码文件版权信息
mvn license:format