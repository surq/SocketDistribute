package com.surq.distribute.mainFrame

import java.util.concurrent.LinkedBlockingQueue
import java.net.Socket
import com.surq.distribute.util.Util
import java.text.SimpleDateFormat
import com.surq.distribute.util.LoadConfFile

/**
 * 1、 用户实时传入数据处理任务处理
 * 2、发送到对应的worker端内存中
 * 3、落地成文件
 */
class GetUserDataTasker(userQueue: LinkedBlockingQueue[Socket], msgQueue: LinkedBlockingQueue[String]) extends Runnable {
  val date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val properties = LoadConfFile.confFile
  // 客户端是否打印交互日志
  val print_flg = properties.getProperty("request.log.open.flg").trim.toBoolean
  override def run = {
    // 向服务端注册的woker ID
    var workerIndex = 0l
    while (true) {
      // 用户上传数据请求
      val user = userQueue.take
      // 读取用户上传数据内容
      // TCP/IP  多次请求多次握手,只取每一次数据   GET /data=surongquan HTTP/1.1
      val first_line = Util.readHeadSocket(user)
      if (first_line != null && first_line.trim != "") {
        // 截取数据内容“/” 至 “HTTP/1.1“之间的内容
        val data = (first_line.substring(first_line.indexOf('/') + 1, first_line.lastIndexOf('/') - 5)).trim
        // 过滤到favicon.ico 请求
        if (data.trim != "favicon.ico") {
          //---------数据分发---------
          // 选择对应的woker 客户端
          val id = (workerIndex % Main.workerList.size).toInt
          // 1、发送数据
          Main.dataQueueList(id).offer(data.trim)
          // 2、用户实时数据落地文件
          msgQueue.offer(data.trim)
          if (print_flg) printLog("master 收到用户上传数据：", data.trim)
          workerIndex += 1
          // 响应用户上传数据请求结果
          Util.writeOkSocket(user, "ok!")
        } else Util.writeNGSocket(user, "Invalid data !")
      } else {
        Util.writeNGSocket(user, "unnecessary requests !")
      }
      user.close
    }
  }
  def printLog(param1: String, param2: String) = println(date_format.format(System.currentTimeMillis) + "---" + this.getClass.toString() + param1 + param2)
}