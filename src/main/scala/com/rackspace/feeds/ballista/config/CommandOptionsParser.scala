package com.rackspace.feeds.ballista.config

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scopt.Read

case class CommandOptions(runDate: DateTime = DateTime.now.minusDays(1).withTimeAtStartOfDay(),
                          dbNames: Set[String] = AppConfig.export.from.dbs.dbConfigMap.keySet,
                          tenantIds: Set[String] = Set.empty,
                          scponly: Boolean = false,
                          dryrun: Boolean = false)

object CommandOptionsParser {

  implicit val jodaTimeRead: Read[DateTime] = scopt.Read.reads {
    DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime
  }
  
  implicit val setOfStringsRead: Read[Set[String]] = scopt.Read.reads {
    (s: String) => s.split(",").map(_.trim).toSet
  }
  
  private val dbNamesSet = AppConfig.export.from.dbs.dbConfigMap.keySet
  
  val parser = new scopt.OptionParser[CommandOptions]("com.rackspace.feeds.ballista.AppMain") {
    head("Ballista")
    opt[DateTime]('d', "runDate") action { (dateTime, callback) =>
        callback.copy(runDate = dateTime) 
      } validate { dateTime =>
        val todaysDate: DateTime = DateTime.now.withTimeAtStartOfDay
        if (isValidRunDate(dateTime, todaysDate))
          success 
        else 
          failure(s"Invalid runDate[$dateTime] specified. runDate should be less than or equal to today and within ${AppConfig.export.daysDataAvailable} days back")
      } text {
        """
          |runDate is a date in the format yyyy-MM-dd.
          |Data belonging to this date will be exported
        """.stripMargin.replaceAll("\n", " ")
      }
    opt[Set[String]]('n', "dbNames") action { (x, c) =>
        c.copy(dbNames = x) 
      } validate { dbs =>
        if (dbs.size > 0 && dbs.subsetOf(dbNamesSet))
          success
        else
          failure(s"Invalid dbNames[${dbs.toArray.mkString(",")}] specified. Available dbNames: ${dbNamesSet.mkString(",")}")
      } text {
        s"""
          |dbNames is comma separated list of database names to be exported
          |Available dbNames for this configuration: ${dbNamesSet.mkString(",")}
        """.stripMargin.replaceAll("\n", " ")
      }
    opt[Set[String]]('t', "tenantIds") action { (x, c) =>
      c.copy(tenantIds = x)
    } validate { tenantIds =>
      if (tenantIds.size > 0)
        success
      else
        failure(s"Tenant Ids must be specified and cannot be empty.")
    } text {
      s"""
          |tenantIds is comma separated list of tenant ids.
          |Data belonging to these tenant ids will be exported
        """.stripMargin.replaceAll("\n", " ")
    }
    opt[Unit]("scponly") action { (x, c) =>
      c.copy(scponly = true)
    } text {
      """
        |scponly is a true/false flag. When used along with dryrun flag does not have any affect.
        |Set this flag to scp the file present in temporary path(mentioned in conf file to remote server.
      """.stripMargin.replaceAll("\n", " ")
    }
    opt[Unit]("dryrun") action { (x, c) =>
      c.copy(dryrun = true)
    } text {
      """
        |dryrun is a true/false flag.
        |Set this flag to test connections to DB and remote systems.
        |When this option is set other options are ignored.
      """.stripMargin.replaceAll("\n", " ")
    }
    help("help") text {
      """
        |Use this option to get detailed usage information of this utility.
      """.stripMargin.replaceAll("\n", " ")
    }
  }


  def isValidRunDate(dateTime: DateTime, referenceDate: DateTime): Boolean = {
    // if reference date is today, the below condition will return true for today's date and
    // any date which is not earlier than ${AppConfig.export.daysDataAvailable} days from todays date
    dateTime.isBefore(referenceDate.plusDays(1)) &&
      !dateTime.isBefore(referenceDate.minusDays(AppConfig.export.daysDataAvailable))
  }

  def getCommandOptions(args: Array[String]): Option[CommandOptions] = {
    CommandOptionsParser.parser.parse(args, CommandOptions())
  }

}
