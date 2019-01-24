package com.surq.distribute.mainFrame

import java.util.concurrent.LinkedBlockingQueue
import java.net.ServerSocket
import java.net.Socket
import com.surq.distribute.util.LoadConfFile
import java.util.concurrent.ExecutorCompletionService

/**
 * 1、启动用户数据交互后台服务
 * 2、把用户请求押入队列
 * 3、并发处理用户请求
 */
class AchieveUserDataTasker extends Runnable {
    val userQueue = new LinkedBlockingQueue[Socket]()
      // 系统配置属性文件
  val properties = LoadConfFile.confFile
  val serverPort = properties.getProperty("master.user.getdata.server.port").trim.toInt
    val completionService = new ExecutorCompletionService[String](Main.executorsPool)
  override def run = {
    // 1、启动服务dispatcher服务
    val serverSocket = new ServerSocket(serverPort)
    Console println "=====【用户数据交互后台服务】  is startting.... prot is " + serverPort
    // 启动接收数据处理模块
    Main.executorsPool.execute(new RequestDispatcheTasker(userQueue))
    // 用户队列
    while (true) userQueue.offer(serverSocket.accept)
  }
}