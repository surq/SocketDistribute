package com.surq.distribute.mainFrame

import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.ExecutorCompletionService
import java.net.InetSocketAddress
import java.util.concurrent.LinkedBlockingQueue
import com.surq.distribute.util.LoadConfFile
import com.surq.distribute.util.Util

/**
 * 服务端启动入口
 * @author 宿荣全
 * @date 2019-01-16
 * 1、启动服务masker服务
 * 2、启动woker 注册服务
 * 3、 启运用户上传数据模块
 * 4、启运用户数据交互模块
 */
object Main {

  // 所有工作的worker结点
  val workerList = ArrayBuffer[Socket]()
  val executorsPool = Executors.newCachedThreadPool()
  val completionService = new ExecutorCompletionService[String](executorsPool)
  // 本地数据、实时数据在主服务中的缓存队列
  val dataQueueList = ArrayBuffer[LinkedBlockingQueue[String]]()
  val workerIPList = ArrayBuffer[String]()
  // 系统配置属性文件
  val properties = LoadConfFile.confFile
  val serverPort = properties.getProperty("master.server.port").trim.toInt
  val waitRegistTime = properties.getProperty("wait.regist.timeMillis").trim.toInt
  val sourcePath = properties.getProperty("source.data.path").trim
  
  def main(args: Array[String]): Unit = {
    // 1、启动服务masker监听服务
    val serverSocket = new ServerSocket(serverPort)
    Console println "=====【master 服务】  is startting.... prot is " + serverPort
    // 2、启动woker 注册服务
    executorsPool.execute(new WorkerRegister(serverSocket, workerList))
    Console println "=====【等待 worker 前来注册】......"
    // 等待worker注册
    Thread.sleep(waitRegistTime)
    Console println "=====等待" + waitRegistTime / 1000 + "秒，收到" + workerList.size + "个workers注册，信息如下："
    //3、 打印worker 端信息
    workerList.map(client => {
      val machineInfo = Util.getMachineInfo(client)
      Console println "client实例信息" + machineInfo._1
      workerIPList += machineInfo._2
    })

    if (workerList.size > 0) {
      Console println "=====【客户端数据分发服务】 is startting...."
      // 每个client对应一个数据队列
      workerList.map(socket => dataQueueList += new LinkedBlockingQueue[String]())
      // 4、启动本地数据分发模块
      workerList.zip(dataQueueList).map(sender => executorsPool.submit(new SendDataTasker(sender._1, sender._2)))
      Console println "=====【本地数据源加载服务】 is startting...."
      // 加载数据源文件
      val sourceDir = new File(sourcePath)
      val sourceFileList = sourceDir.listFiles.filter(f => !f.getName.startsWith("."))
      // 5、启动源数据加载模块
      sourceFileList.map(file => completionService.submit(new SourceReceiverTasker(file, dataQueueList)))
      val souceCounts = (for (idex <- 0 until sourceFileList.size) yield completionService.take.get.toInt).toList
      Console println "=====本地数据源加载完毕，共加载[" + souceCounts.sum + "]条。"
      while (dataQueueList.map(q => q.size).sum != 0) Thread.sleep(300)
      Console println "=====向客户端分发本地数据源完毕。"

      // 6、 启运用户上传数据模块
      executorsPool.execute(new ReceiverUserDataMainTasker)
      // 7、启运用户数据交互模块
      executorsPool.execute(new AchieveUserDataTasker)
    } else executorsPool.shutdownNow()

  }
}