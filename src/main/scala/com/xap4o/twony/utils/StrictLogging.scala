package com.xap4o.twony.utils

import org.slf4j.LoggerFactory

trait StrictLogging {
  val LOG = LoggerFactory.getLogger(getClass)
}
