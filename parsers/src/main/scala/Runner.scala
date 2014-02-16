import configuration.Scenario
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory
import outputs.base.Output
import scala.Array
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
    val workflow = Class.forName(scenario.workflow.name).newInstance().asInstanceOf[Workflow]
    val output = Class.forName(scenario.output.name).newInstance().asInstanceOf[Output]

    logger.info("Executing workflow " + scenario.workflow.name + " with output " + scenario.output.name)
    try {
      output.execute(scenario.output.args, workflow.run(scenario.workflow.args).toArray)
    } catch {
      case e: Exception => logger.error("Workflow " + scenario.workflow.name + " failed: " + e.toString)
    }
  }
}
