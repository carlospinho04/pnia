package com.carlos.pnia.rules

import cats.effect.{Effect, IO}
import com.carlos.pnia.domain.PhoneNumber

import scala.annotation.tailrec
import scala.collection.immutable.HashSet
import scala.io.Source

trait PhoneNumberValidatorRules[F[_]] {
  def checkAndReturnValidPrefix(phoneNumber: PhoneNumber): F[Option[String]]
}

object PhoneNumberValidatorRules {
  //Since the file is on resources, the application only starts if it finds the resource
  val lines: HashSet[String] = HashSet.from(Source.fromResource("prefixes.txt").getLines())

  def apply[F[_]](implicit ev: PhoneNumberValidatorRules[F]): PhoneNumberValidatorRules[F] = ev

  def impl[F[_]: Effect]: PhoneNumberValidatorRules[F] = new PhoneNumberValidatorRules[F] {

    override def checkAndReturnValidPrefix(phoneNumber: PhoneNumber): F[Option[String]] = {
      Effect[F].liftIO(IO(prefixValidator(getCleanPhoneNumber(phoneNumber))))
    }

    //Check if prefix is valid, if it is then it returns it
    @tailrec
    private def prefixValidator(phoneNumber: PhoneNumber, step: Int = 1): Option[String] = {
      phoneNumber.number.substring(0, step) match {
        case phoneNumber.number if lines.contains(phoneNumber.number) => Option(phoneNumber.number)
        case phoneNumber.number => None
        case substring if lines.contains(substring) => Option(substring)
        case _ => prefixValidator(phoneNumber: PhoneNumber, step + 1)
      }
    }

    //Remove any + or 00 to normalize all the phone numbers
    private def getCleanPhoneNumber(phoneNumber: PhoneNumber): PhoneNumber = {
      val cleanPhoneNumberStr = phoneNumber.number match {
        case s"+$number" => number
        case s"00$number" => number
        case numberWithoutPrefix => numberWithoutPrefix
      }
      PhoneNumber(cleanPhoneNumberStr)
    }
  }

}
