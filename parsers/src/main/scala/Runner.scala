import configuration.Scenario
import models.Schedule
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory
import outputs.base.Output
import scala.Array
import scala.collection.mutable
import scala.io.Source
import workflows.base.Workflow

/** Execute one or more scenarios defined in the specified configuration file. */
object Runner extends App {
  implicit val formats = DefaultFormats

  def logger = LoggerFactory.getLogger(this.getClass)

  var scenarios = Array.empty[Scenario]

  if (args.length > 0) {
    scenarios = parse(Source.fromFile(args(0)).mkString).extract[Array[Scenario]]
  } else {
    scenarios = parse(sys.env("RUNNER_CONFIGURATION")).extract[Array[Scenario]]
  }

  if (scenarios.isEmpty) {
    logger.warn("No scenario found")
  } else {
    logger.info("Loaded " + scenarios.length + " scenario(s)")
  }

  for (scenario <- scenarios) {
    val schedules = mutable.Set.empty[Schedule]

    for (workflowConfiguration <- scenario.workflows) {
      val workflow = Class.forName(workflowConfiguration.name).newInstance().asInstanceOf[Workflow]

      logger.info("Executing workflow " + workflowConfiguration.name)

      try {
        schedules ++= workflow.run(workflowConfiguration.args)
      } catch {
        case e: Exception => logger.error("Workflow " + workflowConfiguration.name + " failed: " + e.toString)
      }
    }

    for (outputConfiguration <- scenario.outputs) {
      val output = Class.forName(outputConfiguration.name).newInstance().asInstanceOf[Output]

      logger.info("Executing output " + outputConfiguration.name)

      try {
        output.execute(outputConfiguration.args, schedules.toArray)
      } catch {
        case e: Exception => logger.error("Output " + outputConfiguration.name + " failed: " + e.toString)
      }
    }
  }
}
