package com.surq.distribute.util

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.collection.mutable.ArrayBuffer
import java.io.PrintStream
import java.net.InetSocketAddress

object Util {
  
//  def main(arg:Array[String])={
//    val ss  = this.getClass
//    Console println    this.getClass.toString()
//  }
  /**
   * 读取 socket 所有内容
   */
  def readSocket(client: Socket) = {
    // 所有内容 有两次请求，还有一次是GET /favicon.ico HTTP/1.1
    //      GET /surq8 HTTP/1.1
    //      Host: localhost:9011
    //      Connection: keep-alive
    //      Upgrade-Insecure-Requests: 1
    //      User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    //      Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    //      Accept-Encoding: gzip, deflate, br
    //      Accept-Language: zh-CN,zh;q=0.9
    val contentList = ArrayBuffer[String]()
    val reader = new BufferedReader(new InputStreamReader(client.getInputStream))
    var first_line = reader.readLine()
    while (first_line.trim() != "") {
      contentList += first_line
      first_line = reader.readLine()
    }
    contentList.toList
  }

  /**
   * 仅读取Socket的头条内容:URL的全部内容
   */
  def readHeadSocket(client: Socket) = (new BufferedReader(new InputStreamReader(client.getInputStream))).readLine()

  /**
   * 返回Socket成功信息
   */
  def writeOkSocket(client: Socket, content: String) = {
    val write = new PrintStream(client.getOutputStream(), true)
    write.println("HTTP/1.1 200 OK \n\n")
    write.println(content)
  }

  /**
   * 返回Socket失败信息
   */
  def writeNGSocket(client: Socket, content: String) = {
    val write = new PrintStream(client.getOutputStream(), true)
    write.println("HTTP/1.1 400 Bad Request \n\n")
    write.println(content)
  }
/**
 * 通过socket返回机器信息
 * 返回值：（机器信息，IP）
 */
 def getMachineInfo(client: Socket) = {
    val machineInfo = client.getRemoteSocketAddress.asInstanceOf[InetSocketAddress]
    val machineAddress = machineInfo.getAddress
    val hostName = machineAddress.getHostName
    val hostIp = machineAddress.getHostAddress
    val port = machineInfo.getPort
    ("hostName:["+hostName+"] ip:["+hostIp+":" +port+"]",hostIp )
  }
}