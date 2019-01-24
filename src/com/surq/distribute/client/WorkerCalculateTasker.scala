package com.surq.distribute.client
import java.net.Socket
import java.io.PrintStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import com.surq.distribute.taskers.WorkerCalculate
import com.surq.distribute.util.{ Util, LoadConfFile }

/**
 * 获取用户交互参数，计算结果返回
 */
class WorkerCalculateTasker(request: Socket) extends Callable[Boolean] {
  val date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val machineInfo = Util.getMachineInfo(request)._1
  val properties = LoadConfFile.confFile
  val separator = properties.getProperty("result.data.show.split").trim
  // 客户端是否打印交互日志
  val print_flg = properties.getProperty("request.log.open.flg").trim.toBoolean

  override def call = {
    // 读取用户上传参数
    val reader = new BufferedReader(new InputStreamReader(request.getInputStream))
    val param = reader.readLine
    if (print_flg) printLog(" 参数：", param)
    val result = WorkerCalculate.pictureCalculate(param)
    // 响应用户上传数据请求结果
    val writer = new PrintStream(request.getOutputStream, true)
    writer.println(result.mkString(separator))
    if (print_flg) printLog(" 结果：", result.mkString(separator))
    request.close
    true
  }
  def printLog(param1: String, param2: String) = println(date_format.format(System.currentTimeMillis) + "---" + this.getClass.toString() + param1 + param2)
}