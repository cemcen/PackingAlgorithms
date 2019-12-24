package lib

import java.io.File

object WebpackBuildFile {
  private val d = new File("public/bundle")
  private val style = new File("public/bundle/style")
  val jsBundle: String = if(d.exists && d.isDirectory) {
    d.listFiles.filter(_.isFile).toList.find(f => f.getName.contains("js.bundle.")).get.getName.replace(".gz", "")
  } else ""

  val cssBundle: String = if(style.exists && style.isDirectory) {
    style.listFiles.filter(_.isFile).toList.find(f => f.getName.contains("pack-vex.min.")).get.getName.replace(".gz", "")
  } else ""

}
