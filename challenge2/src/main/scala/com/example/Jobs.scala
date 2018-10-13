package com.example

import com.example.WorkerUtils.Worker

import scala.collection.mutable

object Jobs {

  import WorkerUtils.Worker._

  case class Job(
                jobId: String,
                startTime: String,
                endTime: String,
                duration: String,
                hr: Int = 0, // hr and mm not needed remove them
                mm: Int = 0
                )

  case class ScheduledJob(
                          job: Job,
                          worker: Worker
                          )

  def getEndTime(hr: Int, mm: Int, duration : Int): String = {

    val hrs = duration.toInt/60
    val mins = duration - (hrs * 60)

    val totalHrs = hr + hrs
    val totalMins  = mins
    s"$totalHrs$totalMins"
  }




}
