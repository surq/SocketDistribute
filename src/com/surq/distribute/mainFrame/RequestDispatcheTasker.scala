package com.surq.distribute.mainFrame

import java.util.concurrent.LinkedBlockingQueue
import java.net.Socket
/**
 * 分发用户请求并行处理
 */
class RequestDispatcheTasker(userQueue: LinkedBlockingQueue[Socket]) extends Runnable {
  override def run = while (true) Main.executorsPool.submit(new SendRequestTasker(userQueue.take))
}