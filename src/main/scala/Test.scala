import java.io.InputStream

import gopkg.in.bblfsh.sdk.v1.uast.generated.{Node, Role}
import org.bblfsh.client.BblfshClient

object Test {
  def main(args: Array[String]): Unit = {
    val client = BblfshClient("0.0.0.0", 9432)
    val filename = "Test.java"
    val stream: InputStream = getClass.getResourceAsStream(filename)
    val lines = scala.io.Source.fromInputStream(stream).getLines
    val fileContent = lines.mkString("\n")
    val resp = client.parse(filename, fileContent)

    println("Language: " + resp.language)

    println(resp.uast.get)
    // Filtered response
    println(client.filter(resp.uast.get, "//TypeDeclaration"))
    println(client.filter(resp.uast.get, "//SimpleName").head.roles.contains(Role.IDENTIFIER))

    print_node (resp.uast.get)

    println()
    // Filtered responses using XPath functions returning types
    // other than NodeLists (Bool, Number, String):
    println(client.filterBool(resp.uast.get, "boolean(//*[@strtOffset or @endOffset])"))
    println(client.filterString(resp.uast.get, "name(//*[1])"))
    println(client.filterNumber(resp.uast.get, "count(//*)"))
  }

  def print_node(node:Node, n:Int = 0): Unit = {
    println(node.internalType + ": " + node.token)
    print(" " * n)
    for (child <- node.children) {
      print_node(child, n + 1)
    }
  }
}
