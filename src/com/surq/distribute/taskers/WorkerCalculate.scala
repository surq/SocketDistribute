package com.surq.distribute.taskers

import com.surq.distribute.client.ClientMain
import com.surq.distribute.util.LoadConfFile

object WorkerCalculate {
  val properties = LoadConfFile.confFile
  val diffNum = properties.getProperty("picture.code.diffNum").trim.toInt
  def pictureCalculate (param:String) ={
    val char_param = param.toCharArray()
   ClientMain.souceDataList.map(pictureId=>{
     val differenceNum = pictureId.toCharArray().zip(char_param).map(f=>if (f._1-f._2 == 0) 0 else 1).sum
      (pictureId,differenceNum)
    }).filter(f=> f._2<=diffNum).map(line=>line._2+":"+line._1).toList
  }
}