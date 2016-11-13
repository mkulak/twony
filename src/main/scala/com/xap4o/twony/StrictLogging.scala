package com.xap4o.twony

import org.slf4j.LoggerFactory


trait StrictLogging {
  val LOG = LoggerFactory.getLogger(getClass)
}
