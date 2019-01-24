package com.surq.distribute.mainFrame

import java.util.concurrent.Callable
import java.net.Socket
import java.io.PrintStream
import java.io.BufferedReader
import java.io.InputStreamReader
import com.surq.distribute.util.LoadConfFile
import java.text.SimpleDateFormat

/**
 * 把请求发向客户端，并返回单个客户端的结果
 */
class GetResultTasker(workerIp: String, param: String) extends Callable[String] {
  val date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val properties = LoadConfFile.confFile
  val serverPort = properties.getProperty("client.user.getdata.server.port").trim.toInt
  // master是否打印交互日志
  val print_flg = properties.getProperty("request.log.open.flg").trim.toBoolean
  override def call = {
    val client = new Socket(workerIp, serverPort)
    // 把用户请求发送到worker端
    val writer = new PrintStream(client.getOutputStream(), true)
    writer.println(param)
    if (print_flg) printLog(workerIp + "参数：", param)
    val reader = new BufferedReader(new InputStreamReader(client.getInputStream))
    val resultStr = reader.readLine
    if (print_flg) printLog(workerIp + "返回：", resultStr)
    client.close
    resultStr
  }

  def printLog(param1: String, param2: String) = println(date_format.format(System.currentTimeMillis) + "---" + this.getClass.toString() + param1 + param2)
}