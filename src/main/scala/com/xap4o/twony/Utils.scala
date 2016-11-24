package com.xap4o.twony

class Timer {
  val start = now()

  def now() = System.currentTimeMillis()

  def duration() = now() - start
}
