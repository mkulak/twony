package com.xap4o.twony.utils

class Timer {
  val start: Long = now()

  def now(): Long = System.currentTimeMillis()

  def duration(): Long = now() - start
}
