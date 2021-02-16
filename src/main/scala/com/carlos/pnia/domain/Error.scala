package com.carlos.pnia.domain

sealed trait Error extends Exception
case class BusinessSectorError(e: String) extends Error