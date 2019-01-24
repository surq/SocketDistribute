package com.surq.distribute.mainFrame

import java.net.{ServerSocket,Socket}
import scala.collection.mutable.ArrayBuffer

class WorkerRegister(serverSocket: ServerSocket, socketQueue: ArrayBuffer[Socket]) extends Runnable {
  override def run = {
    try while (true) socketQueue+=serverSocket.accept  catch {
      case e: Exception => e.printStackTrace
    } finally if (serverSocket != null && !serverSocket.isClosed) serverSocket.close
  }
}