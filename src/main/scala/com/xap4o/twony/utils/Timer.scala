package com.xap4o.twony.utils


object Timer {
  type CreateTimer = () => () => Long

  val system: CreateTimer = () => {
    def now(): Long = System.currentTimeMillis()
    val start = now()
    () => now() - start
  }
}
