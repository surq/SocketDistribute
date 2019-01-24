package com.surq.distribute.util

import java.io.File
import java.util.Properties
import java.io.FileInputStream

object LoadConfFile {
  
  val confFile = loadProperties()
    /**
   * 指定config/文件夹下的配置文件名，
   * 默认会加载config下的所有.properties的文件
   */
  def loadProperties(fileName: String = "*.properties") = {
    val fileseparator = System.getProperty("file.separator")
    val jarName = this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath
    val properties = new Properties
    val jarpath = jarName.substring(0, jarName.lastIndexOf(fileseparator))
    // 默认配置文件：/../config/config.properties 
      val confPath = jarpath + "/../config/"
    // TODO  本机测试用
//        val confPath = "/moxiu/workspace/SocketDistribute/config/"
    if (fileName == "*.properties") new File(confPath).listFiles.foreach(f => properties.load(new FileInputStream(f.getAbsolutePath)))
    else properties.load(new FileInputStream(confPath + fileName))
    properties
  }
}