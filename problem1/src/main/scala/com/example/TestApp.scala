package com.example

import javax.swing.plaf.synth.SynthFormattedTextFieldUI

import scala.collection.mutable.ArrayBuffer

object TestApp extends App {

  println("Hello World - Problem 1")

  case class CacheItem(
                      key: String,
                      value: String,
                      timestamp: Long
                      )

  case class Cache(
                  name: String
                  ) {
    var cache: scala.collection.mutable.Map[String, ArrayBuffer[CacheItem]] = scala.collection.mutable.Map.empty

    def set(key: String, value: String): Long = {

      val ts = System.currentTimeMillis()

      // assuming its always valid key and value
      val item = CacheItem(key, value, ts)
      val items = cache.getOrElse(key, ArrayBuffer.empty)

      items.append(item)
      cache.+=(key -> items)
      ts
    }

    def get(key: String, timestamp: Long = - 1, fuzzy: Boolean = false): CacheItem = {

      if (fuzzy) {
        val res = cache.get(key)
        println(s"Res items: ${res}")
        val items = res.getOrElse(ArrayBuffer.empty)
        val sortedItems = items.sortBy(_.timestamp).reverse
        println(s"Sorted items: ${sortedItems}")
        val item = sortedItems.head
        return item
      }

      // assuming ts is valid if its given
      if (timestamp != -1) {

        //println(cache.get(key))
        val res = cache.get(key)
        if (fuzzy) {
          val items = res.getOrElse(ArrayBuffer.empty).filter(i => i.timestamp <= timestamp)
          val sortedItems = items.sortBy(_.timestamp)
          println(s"Sorted items: ${sortedItems}")
          val item = sortedItems.head
          item
        } else {
          res.getOrElse(ArrayBuffer.empty).filter(_.timestamp == timestamp).head // assumption is unique item with this timestamp
        }
      } else { // no timestamp provided
        val items: CacheItem = cache.getOrElse(key, ArrayBuffer.empty).head // assuming there is always a valie key
        items
      }
    }
  }


  val cache = Cache("simplecache")

  val ts1 = cache.set("foo", "bar")
  Thread.sleep(1)

  val ts2 = cache.set("foo", "bar2")

  val i1 = cache.get("foo", ts1)
  val i2 = cache.get("foo", ts2)
  val i = cache.get("foo", timestamp = -1, fuzzy = true)

  println(s"Item value : ${i1.value} ")
  println(s"Item value : ${i2.value} ")
  println(s"Item value : ${i.value} ")

}
