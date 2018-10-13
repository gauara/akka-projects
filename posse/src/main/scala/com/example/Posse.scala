package com.example

import scala.collection.mutable.ListBuffer

object Posse {

  import Validations._

  case class Cell(v: String, id: Int)

  case class Board(cells: List[Cell])

  def validateBoard(board: Board): Option[List[Cell]] = {
    val b = board.cells.toArray
    var result = ListBuffer.empty[Cell]

    for (i <- 0 to b.length-2) {
      for (j <- i+1 to b.length-1) {
        for (k <- 0 to b.length-1) {
          val cell1 = b(i)
          val cell2 = b(j)
          val cell3 = b(j+2)

          val valid = validateLength(cell1, cell2, cell3) &&
            validateCase(cell1, cell2, cell3) &&
            validateLetter(cell1, cell2, cell3) &&
            validatePrefix(cell1, cell2, cell3)

          if (valid) {
            val buffer = ListBuffer.empty[Cell]
            buffer.append(cell1)
            buffer.append(cell2)
            buffer.append(cell2)
            result = buffer
          }
        }
     }

      if (result.isEmpty) None
      else Some(result)
    }
    None
  }

}
