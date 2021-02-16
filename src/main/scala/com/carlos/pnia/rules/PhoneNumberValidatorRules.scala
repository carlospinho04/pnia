package com.carlos.pnia.rules

import better.files._
import cats.effect.Sync

trait PhoneNumberValidatorRules[F[_]] {
  def get: Set[String]
}

object PhoneNumberValidatorRules {
  def apply[F[_]](implicit ev: PhoneNumberValidatorRules[F]): PhoneNumberValidatorRules[F] = ev

  def impl[F[_] : Sync]: PhoneNumberValidatorRules[F] = new PhoneNumberValidatorRules[F] {

    override def get: Set[String] = {
      val f = File("/pnia/prefixes.txt2")
      println(s"E o preco deste montra final é: ${f.lineCount}!!!!!!!!!!!!!!!!")
      val lines: Iterator[String] = f.lineIterator
      println("AAAIAIAIAIAIAIIAIAIAIAIIAIAIIAiAIA")
      println(lines)
      lines.toSet
    }
  }
}


