package com.surq.distribute.mainFrame

import java.util.concurrent.Callable
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import java.io.PrintStream
/**
 * 1、数据发送服务
 * 2、把数据从主服务拉到worker内存中
 */
class SendDataTasker(client: Socket, dataQueue: LinkedBlockingQueue[String]) extends Callable[Boolean] {
  override def call = {
    val writer = new PrintStream(client.getOutputStream, true)
    while (true) writer.println(dataQueue.take)
    false
  }
}