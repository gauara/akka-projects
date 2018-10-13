package com.example

import com.example.Posse.Cell

object Validations {
  def validateLength(cell1: Cell, cell2: Cell, cell3: Cell): Boolean = {

    def isSame(): Boolean = cell1.v.size == cell2.v.size && cell1.v.size == cell3.v.size

    def isDiff(): Boolean = {
      (cell1.v.size != cell2.v.size && cell1.v.size != cell3.v.size) &&
        (cell2.v.size != cell1.v.size && cell2.v.size != cell3.v.size) &&
        (cell3.v.size != cell1.v.size && cell3.v.size != cell2.v.size) &&
    }
    isSame() || isDiff()
  }

  def validateCase(cell1: Cell, cell2: Cell, cell3: Cell): Boolean = {
    def isSame(): Boolean = cell1.v == cell2.v && cell1.v == cell3.v

    def isDiff(): Boolean = {
      (cell1.v != cell2.v && cell1.v != cell3.v) &&
        (cell2.v != cell1.v && cell3.v != cell2.v) &&
        (cell3.v != cell1.v && cell3.v != cell2.v) &&
    }
    isSame() || isDiff()
  }

  def validatePrefix(cell1: Cell, cell2: Cell, cell3: Cell): Boolean = {
    val cell1p = cell1.v(0) // extracts first char from string OR prefix
    val cell2p = cell1.v(0)
    val cell3p = cell1.v(0)

    def isSame(): Boolean = cell1p == cell2p && cell1p == cell3p

    def isDiff(): Boolean = {
      (cell1p != cell2p && cell1p != cell3p) &&
        (cell2p != cell1p && cell3p != cell2p) &&
        (cell3p != cell1p && cell3p != cell2p) &&
    }
    isSame() || isDiff()

  }

  def validateLetter(cell1: Cell, cell2: Cell, cell3: Cell): Boolean = {

    def unUmaut(c: Char): Char = {
      val umlautLower = c.toLower
      val umlautsLowerCaseMap: Map[Char, Char] = Map(
        'ä' -> 'a',
        'ö' -> 'o',
        'ü' -> 'u'
      )
      umlautsLowerCaseMap.getOrElse(umlautLower, c) // return same char if cant find
    }

    def isSame(): Boolean = (cell1.v == cell2.v || cell1.v.toLowerCase == unUmaut(cell2.v.head)) &&
      (cell1.v == cell3.v || cell1.v.toLowerCase == unUmaut(cell3.v.head))

    def isDiff(): Boolean = {
      ((cell1.v != cell2.v) && cell1.v != unUmaut(cell2.v.head)  && (cell1.v != cell3.v) && cell1.v != unUmaut(cell3.v.head)) &&
        ((cell2.v != cell3.v) && cell2.v != unUmaut(cell3.v.head)  && (cell2.v != cell1.v) && cell2.v != unUmaut(cell1.v.head)) &&
        ((cell3.v != cell1.v) && cell3.v != unUmaut(cell1.v.head)  && (cell3.v != cell2.v) && cell3.v != unUmaut(cell2.v.head))
    }
    isSame() || isDiff()
  }

}
