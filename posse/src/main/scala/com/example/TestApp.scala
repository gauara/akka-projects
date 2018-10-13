package com.example

import com.example.Posse.{Board, Cell}

object TestApp extends App {

  val cells = Array("+a", "+o", "+u", "+A", "+O", "+U", "+Ä", "+Ö", "+Ü", "=a", "=o", "=u", "=A", "=O", "=U", "=Ä", "=Ö", "=Ü", "-a", "-o", "-u",
  "-A", "-O", "-U", "-Ä", "-Ö", "-Ü", "+aa", "+oo", "+uu", "+AA", "+OO", "+UU", "+ÄÄ", "+ÖÖ", "+ÜÜ", "=aa", "=oo", "=uu", "=AA",
  "=OO", "=UU", "=ÄÄ", "=ÖÖ", "=ÜÜ", "-aa", "-oo", "-uu", "-AA", "-OO", "-UU", "-ÄÄ", "-ÖÖ", "-ÜÜ", "+aaa", "+ooo", "+uuu",
  "+AAA", "+OOO", "+UUU", "+ÄÄÄ", "+ÖÖÖ", "+ÜÜÜ", "=aaa", "=ooo", "=uuu", "=AAA", "=OOO", "=UUU", "=ÄÄÄ", "=ÖÖÖ", "=ÜÜÜ", "-aaa",
  "-ooo", "-uuu", "-AAA", "-OOO", "-UUU", "-ÄÄÄ", "-ÖÖÖ", "-ÜÜÜ")

  val board =     Board(cells = cells.zipWithIndex.map(d => Cell(d._1, d._2)).toList)

  Posse.validateBoard(board)

}
