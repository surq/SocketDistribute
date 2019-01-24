package com.surq.distribute.mainFrame

import java.net.Socket
import java.io.PrintStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.LinkedBlockingQueue
import com.surq.distribute.taskers.WorkerCalculate
import com.surq.distribute.util.{Util,LoadConfFile}

/**
 * 把用户上传的参数分发到各个worker机器上进行计算，
 * 汇总各结点计算结果返回到用户
 */
class SendRequestTasker(user: Socket) extends Runnable {
    val properties = LoadConfFile.confFile
  val separator = properties.getProperty("result.data.show.split").trim
  override def run = {
    // 读取用户传入参数
    // TCP/IP  多次请求多次握手,只取每一次数据   GET /data=surongquan HTTP/1.1
    val first_line = Util.readHeadSocket(user).trim
    if (first_line != null && first_line != "") {
      // 截取数据内容“/” 至 “HTTP/1.1“之间的内容
      val param = (first_line.substring(first_line.indexOf('/') + 1, first_line.lastIndexOf('/') - 5)).trim
      // 过滤到favicon.ico 请求
      val result = if (param.trim != "favicon.ico") {
        // 发送到各个worker并行运算，合并返回计算结果
        val futureList  = Main.workerIPList.map(ip=> Main.completionService.submit(new GetResultTasker(ip, param)))
        (for (index <- 0 until futureList.size) yield Main.completionService.take.get ).toList
      } else List[String]()
      // 返回执行结果
      Util.writeOkSocket(user, result.mkString(separator))
    } else Util.writeNGSocket(user, "Bad Request")
    user.close
  }
}