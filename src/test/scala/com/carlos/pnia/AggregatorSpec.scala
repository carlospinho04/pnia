package com.carlos.pnia

import cats.effect.IO
import com.carlos.pnia.config.ApplicationConf
import com.carlos.pnia.controllers.Aggregator
import com.carlos.pnia.rules.{BusinessSectorRules, PhoneNumberValidatorRules}
import com.carlos.pnia.services.AggregatorServices
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global

class AggregatorSpec extends CatsEffectSuite {
  test("Aggregator business sectors by prefix") {
    val body = """["+1983248", "001382355", "+147 8192", "+4439877"]"""
    val call = Request[IO](Method.POST, uri"/aggregate").withEntity(body)

    BlazeClientBuilder[IO](global).resource.use { client =>
      val applicationConf = ApplicationConf.load() match {
        case Right(c) => c
        case Left(err) => throw new IllegalStateException(s"Could not load AppConfig: ${err.toList.mkString("\n")}")
      }
      val phoneNumberValidatorRulesAlg = PhoneNumberValidatorRules.impl[IO]
      val businessSectorRulesAlg = BusinessSectorRules.impl[IO](client, applicationConf)
      val aggregatorServicesAlg = AggregatorServices.impl[IO](businessSectorRulesAlg, phoneNumberValidatorRulesAlg)
      val aggregator = Aggregator.impl[IO](aggregatorServicesAlg)
      val result = PniaRoutes.aggregatorRoutes(aggregator).orNotFound(call)
      assertIO(result.flatMap(_.as[String]), """{"44":{"Banking":1},"1":{"Clothing":1,"Technology":2}}""")
    }
  }
}
