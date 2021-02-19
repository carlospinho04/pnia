package com.carlos.pnia.domain

sealed trait Error extends Exception
case class BusinessSectorApiError(e: String) extends Error
case class InvalidUri(e: String) extends Error