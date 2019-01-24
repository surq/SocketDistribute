package com.surq.distribute.mainFrame

import java.util.concurrent.Callable
import java.io.File
import scala.io.Source
import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable.ArrayBuffer

/**
 * @author 宿荣全
 * 加载本地文件，写入缓存队列
 */
class SourceReceiverTasker(file: File, dataQueueList: ArrayBuffer[LinkedBlockingQueue[String]]) extends Callable[String] {
  override def call = {
    val lineList = Source.fromFile(file).getLines.toList
    for (index <- 0 until lineList.size) dataQueueList(index % dataQueueList.size).offer(lineList(index))
    // 返回加载条数
    lineList.size.toString()
  }
}