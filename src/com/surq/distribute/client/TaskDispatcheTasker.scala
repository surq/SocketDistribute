package com.surq.distribute.client

import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
/**
 * 启用多线程计算用户用的请求
 */
class TaskDispatcheTasker(userQueue: LinkedBlockingQueue[Socket]) extends Runnable {
  override def run =  while (true) ClientMain.executorsPool.submit(new WorkerCalculateTasker(userQueue.take))
}