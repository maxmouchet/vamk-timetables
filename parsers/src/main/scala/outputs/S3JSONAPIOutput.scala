package outputs

import models.Schedule
import java.util.UUID
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.{Regions, Region}
import outputs.base.Output
import java.io.File
import com.amazonaws.services.s3.transfer.TransferManager
import scala.reflect.io.Directory
import collection.JavaConversions._
import com.amazonaws.services.s3.model.CopyObjectRequest

class S3JSONAPIOutput extends Output {

  def execute(args: Array[String], schedules: Array[Schedule]) = {
    val tempDir = new File("s3jsonapi-temp-" + UUID.randomUUID)
    val jsonApiOutput = new JSONAPIOutput
    jsonApiOutput.execute(Array(tempDir.getPath), schedules)

    val credentials = new BasicAWSCredentials(args(0), args(1))
    val bucketName = args(2)
    val dirPrefix = args(3)

    val s3 = new AmazonS3Client(credentials)
    val euWest1 = Region.getRegion(Regions.EU_WEST_1)

    s3.setRegion(euWest1)

    val transferManager = new TransferManager(s3)
    val transfer = transferManager.uploadDirectory(bucketName, dirPrefix, tempDir, true)

    while (!transfer.isDone) {
      System.out.print(transfer.getProgress.getPercentTransferred + " ")
      Thread.sleep(250)
    }

    // TODO: Clean this part (setting content-type)
    var current = s3.listObjects(bucketName, dirPrefix)
    val keyList = current.getObjectSummaries
    current = s3.listNextBatchOfObjects(current)

    while (current.isTruncated) {
      keyList.addAll(current.getObjectSummaries)
      current = s3.listNextBatchOfObjects(current)
    }

    keyList.addAll(current.getObjectSummaries)

    for (objectSummary <- keyList.toList.par) {
      System.out.println("Setting content-type for " + objectSummary.getKey)
      var o = s3.getObject(bucketName, objectSummary.getKey)
      val cor = new CopyObjectRequest(bucketName, objectSummary.getKey, bucketName, objectSummary.getKey)
      val metadata = o.getObjectMetadata
      metadata.setContentType("application/json")
      cor.setNewObjectMetadata(metadata)
      o.close()
      s3.copyObject(cor)
    }

    new Directory(tempDir).deleteRecursively()
    transferManager.shutdownNow()
  }

}
