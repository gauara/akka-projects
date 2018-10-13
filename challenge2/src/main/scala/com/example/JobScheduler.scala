package com.example

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn

object JobScheduler extends App {

  import Jobs._
  import JobExecuter._

  val jobsInput = ListBuffer.empty[String]
  val jobs = ListBuffer.empty[Job]

  val totalJobs = StdIn.readInt()

  var count = 0
  do {
    val input = StdIn.readLine().trim()
    jobsInput.append(input)
    count += 1
    //println(s"Input: $input ")
  } while (count < totalJobs)

  var jobId = 0
  jobsInput.map {j =>

    val hhmm = j.split(" ")(0)
    val duration = j.split(" ")(1)

    val hr = hhmm.take(2).toInt
    val mm = hhmm.drop(2).toInt

    val job = Job(
      jobId = "J"+jobId,
      startTime = hhmm,
      endTime = getEndTime(hr, mm, duration.toInt).toString,
      duration = duration
    )

    jobs.append(job)
  }

  //println(jobs)


  val jobsSortedByStartTime = jobs.sortBy(_.startTime)

  val res = executeJobs(jobsSortedByStartTime)
  println(res.n)

  res.jobs.foreach { j =>
    println(j.j + " " + j.w)
  }

}
