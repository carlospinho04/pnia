package com.carlos.pnia.rules

import cats.effect.Sync
import cats.implicits.catsSyntaxApplicativeId
import com.carlos.pnia.domain.PhoneNumber

import scala.annotation.tailrec
import scala.collection.immutable.HashSet
import scala.io.Source

trait PhoneNumberValidatorRules[F[_]] {
  def hasValidPrefix(phoneNumber: PhoneNumber): F[Boolean]
}

object PhoneNumberValidatorRules {
  //Since the file is on resources, the application only starts if it finds the resource
  val lines: HashSet[String] = HashSet.from(Source.fromResource("prefixes.txt").getLines())

  def apply[F[_]](implicit ev: PhoneNumberValidatorRules[F]): PhoneNumberValidatorRules[F] = ev

  def impl[F[_] : Sync]: PhoneNumberValidatorRules[F] = new PhoneNumberValidatorRules[F] {

    override def hasValidPrefix(phoneNumber: PhoneNumber): F[Boolean] = {
      checkValidPrefix(phoneNumber).pure[F]
    }

    @tailrec
    private def checkValidPrefix(phoneNumber: PhoneNumber, step: Int = 1): Boolean = {
      phoneNumber.number.substring(0, step) match {
        case phoneNumber.number => lines.contains(phoneNumber.number)
        case substring if lines.contains(substring) => true
        case _ => checkValidPrefix(phoneNumber: PhoneNumber, step + 1)
      }
    }
  }
}


