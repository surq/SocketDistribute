package com.surq.distribute.mainFrame

import java.net.ServerSocket
import java.util.concurrent.LinkedBlockingQueue
import java.net.Socket
import com.surq.distribute.util.LoadConfFile

/**
 * 1、启动用户实时数据上传后台服务
 * 2、把用户请求押入队列
 * 3、并发处理用户实时传入的源数据
 */
class ReceiverUserDataMainTasker extends Runnable {
  // 系统配置属性文件
  val properties = LoadConfFile.confFile
  val serverPort = properties.getProperty("master.receiver.data.server.port").trim.toInt
  val userQueue = new LinkedBlockingQueue[Socket]()
  val msgQueue = new LinkedBlockingQueue[String]()
  override def run = {
    // 1、启动服务dispatcher服务
    val serverSocket = new ServerSocket(serverPort)
    Console println "=====【实时接收用户数据服务】 is startting.... prot is " + serverPort
    // 2、实时用户上传数据落地文件模块
    Main.executorsPool.execute(new UserDataToFileTasker(msgQueue))
    // 3、启动接收数据处理模块
    Main.executorsPool.execute(new GetUserDataTasker(userQueue, msgQueue))
    // 用户队列
    while (true) userQueue.offer(serverSocket.accept)
  }
}