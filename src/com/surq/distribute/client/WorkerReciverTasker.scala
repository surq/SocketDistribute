package com.surq.distribute.client

import java.util.concurrent.LinkedBlockingQueue
import java.net.ServerSocket
import java.net.Socket
import com.surq.distribute.util.LoadConfFile

/**
 * 1、启动接收用户请求服务
 * 2、用户请求押入缓存队列
 * 3、并行运算用户请求
 */
class WorkerReciverTasker extends Runnable {
  val userQueue = new LinkedBlockingQueue[Socket]()
  val properties = LoadConfFile.confFile
  val serverPort = properties.getProperty("client.user.getdata.server.port").trim.toInt

  override def run = {
    try {
      // 1、启动接收用户请求服务
      val serverSocket = new ServerSocket(serverPort)
      Console println "=====接收用户请求服务  is startting.... prot is " + serverPort
      // 启动接收数据处理模块
      ClientMain.executorsPool.execute(new TaskDispatcheTasker(userQueue))
      // 用户队列
      while (true) userQueue.offer(serverSocket.accept)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}