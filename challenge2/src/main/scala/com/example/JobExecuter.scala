package com.example

import com.example.Jobs.{Job, ScheduledJob}
import com.example.WorkerUtils.Worker

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object JobExecuter {

  // add jobs to scheduled jobs pool
  var scheduledJobsMinHeap = mutable.PriorityQueue.empty(Ordering.by[ScheduledJob, String](_.job.endTime).reverse)


  def getWorker(newJobStartTime: String): Option[Worker] = {
    if (scheduledJobsMinHeap.size == 0)
      return None

    if (scheduledJobsMinHeap.head.job.endTime < newJobStartTime) {
      val w = scheduledJobsMinHeap.dequeue().worker
      return Some(w)
    } else {
      None
    }
  }


  case class JobItem(j: String, w:String)
  case class Result(
                   n: Int,
                   jobs: ListBuffer[JobItem]
                   )

  def executeJobs(jobs: ListBuffer[Job]): Result = {

    val res = ListBuffer.empty[JobItem]


    for (job <- jobs) {
      // Find a worker and schedule this job

      getWorker(job.startTime) match {
        case Some(w) =>
          scheduledJobsMinHeap.enqueue(ScheduledJob(job = job, worker = w))
          res.append(JobItem(job.jobId, w.id))
        case _ =>
          val w = Worker(
            id = "W" + WorkerUtils.getNewWorkerId.toString,
            jobId = job.jobId
          )
          scheduledJobsMinHeap.enqueue(ScheduledJob(job = job, worker = w))
          res.append(JobItem(job.jobId, w.id))
      }
    }

    Result(
      n = scheduledJobsMinHeap.size,
      jobs = res
    )

  }

}
