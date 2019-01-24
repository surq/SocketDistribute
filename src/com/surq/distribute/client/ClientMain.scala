package com.surq.distribute.client

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.concurrent.Executors
import scala.collection.mutable.ArrayBuffer
import com.surq.distribute.util.LoadConfFile

object ClientMain {
  val executorsPool = Executors.newCachedThreadPool()
  val souceDataList = ArrayBuffer[String]()
  def main(args: Array[String]): Unit = {
    val properties = LoadConfFile.confFile
    val masterIp = properties.getProperty("master.local.ip").trim
    val serverPort = properties.getProperty("master.server.port").trim.toInt

    //1、 启动worker计算结点
    val client = new Socket(masterIp, serverPort)
    // 2、启动接收用户请求服务
    executorsPool.submit(new WorkerReciverTasker)
    // 动态加载源数据
    val reader = new BufferedReader(new InputStreamReader(client.getInputStream))
    var line = reader.readLine()
    while (line != null) {
      souceDataList += line
      line = reader.readLine
    }
  }
}