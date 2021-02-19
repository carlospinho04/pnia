package com.carlos.pnia.config

import pureconfig.error.ConfigReaderFailures
import pureconfig.module.enumeratum._
import pureconfig.generic.auto._

final case class ApplicationConf(server: ServerConf, businessSectorApi: BusinessSectorApiConf)

object ApplicationConf extends {

  import com.typesafe.config.ConfigFactory
  import pureconfig.syntax._

  def load(): Either[ConfigReaderFailures, ApplicationConf] = ConfigFactory.load.to[ApplicationConf]

}
