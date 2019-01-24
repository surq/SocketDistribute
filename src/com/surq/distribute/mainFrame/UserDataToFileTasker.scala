package com.surq.distribute.mainFrame

import java.util.concurrent.LinkedBlockingQueue
import com.surq.distribute.util.LoadConfFile
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.io.File

class UserDataToFileTasker(msgQueue: LinkedBlockingQueue[String]) extends Runnable {
  val properties = LoadConfFile.confFile
  val sourcePath = properties.getProperty("source.data.path").trim
  val lineLimit = properties.getProperty("user.dataFile.line.limit").trim.toInt
  val date_format = new SimpleDateFormat("yyyy_MM_dd_HHmmssSSS")
  override def run = {
    var fileWriter = getFileWriter
    var writer = fileWriter._2
    var index = 0
    while (true) {
      writer.write(msgQueue.take)
      writer.write(" \n")
      writer.flush
      index += 1
      // 达到预期文件大小重新生成文件
      if (index == lineLimit) {
        writer.close()
        fileWriter._1.renameTo(new File(fileWriter._3))
        fileWriter = getFileWriter
        writer = fileWriter._2
        index = 0
      }
    }
  }
  def getFileWriter = {
    val path = sourcePath + System.getProperty("file.separator") + date_format.format(System.currentTimeMillis)
    val fileName = path + ".tmp"
    val file = new File(fileName)
    Console println "=====【实时用户上传数据落地文件模块】生成新文件：" + fileName
    (file, new FileWriter(file), path)
  }
}