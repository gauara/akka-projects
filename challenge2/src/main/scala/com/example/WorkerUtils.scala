package com.example

object WorkerUtils {

  var nextWorkerId = 0

  def getNewWorkerId(): Int =  {
    val nextId = nextWorkerId
    nextWorkerId += 1
    nextId
  }

  case class Worker(
                   id: String,
                   jobId: String,
                   status: Int = 0  // 0 free 1 busy - lets see if we need it , remove if not
                   )

//  var busyPool = scala.collection.mutable.Set[Worker]
//  var freePool = scala.collection.mutable.Set[Worker]

}
