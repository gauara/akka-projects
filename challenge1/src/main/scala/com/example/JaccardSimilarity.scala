package com.example

import scala.collection.mutable.ListBuffer

object JaccardSimilarity extends App {


  def makeShingles(text: String, stopWords: List[String], size: Int = 3): List[String] = {
    var shingles = ListBuffer.empty[String]
    val tokens = text.split(" ")

    for (i <- 0 to tokens.length-3) {
      var limit = 0
      var j = i
      var l = ListBuffer.empty[String]

      println(s"w : ${tokens(j)}")
      while (!stopWords.contains("|" + tokens(j))) {
        j += 1
      }

      while (limit < size) {
        l.append(tokens(j))
        j += 1
        limit += 1
      }

      if (l.nonEmpty) shingles.append(l.mkString(" "))
    }
      shingles.toList
    }

  val text = """For the string "Indiana Jones will return to the big screen on July 19, 2019, for a fifth epic"""

  println(makeShingles(text, stopWords = Utils.stopWords, 3))


  def jaccardSimilarity(shingles1: List[String], shingles2: List[String]): Double = {

    val s1 = shingles1.toSet
    val s2 = shingles2.toSet

    val intersection = s1 intersect(s2)
    val union = s1 union(s2)

    if (union.size > 0) intersection.size.toDouble/union.size.toDouble
    else 0.0
  }

  val s1 = makeShingles(
    """Ellie Walton, 3, has been fighting a rare brain tumor since she was four months old. Unable to fly, she’s recently begun exploring the world through postcards sent from supporters after her mother put the word out six weeks ago.
      |
      |ADVERTISEMENT
      |“The world has so much to offer you,” reads one sent from Turkey, according to KOMO News. “You should be able to travel and see different countries.”
      |
      |"Not only is it a postcard that shows her a different place, but it also shows her that people believe in her," Ellie’s mother, Sarah Walton, told KOMO News, "and they believe in how strong she is and they know she can beat this."
      |
      |Ellie has stage IV glioblastoma, which doctors say is terminal. She’s undergone 17 surgeries, including 14 brain surgeries and five tumor removals. The girl, who lives in Spanaway, Wash., has also suffered meningitis and septic shock, and must have chemotherapy every other week, KOMO News reported.
      |""", Utils.stopWords, 3)

    val s2 = makeShingles(
      """
        |Ellie Walton, 3, has been fighting a rare brain tumor since she was four months old. Unable to fly, she’s recently begun exploring the world through postcards sent from supporters after her mother put the word out six weeks ago.
        |
        |“The world has so much to offer you,” reads one sent from Turkey, according to KOMO News. “You should be able to travel and see different countries.”
        |
        |"Not only is it a postcard that shows her a different place, but it also shows her that people believe in her," Ellie’s mother, Sarah Walton, told KOMO News, "and they believe in how strong she is and they know she can beat this."
        |
        |Ellie has stage IV glioblastoma, which doctors say is terminal. She’s undergone 17 surgeries, including 14 brain surgeries and five tumor removals. The girl, who lives in Spanaway, Wash., has also suffered meningitis and septic shock, and must have chemotherapy every other week, KOMO News reported.
        |
      """, Utils.stopWords, 3)


  println(jaccardSimilarity(s1, s2))


}
