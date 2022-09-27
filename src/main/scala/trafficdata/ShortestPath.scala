package trafficdata

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import trafficdata.DataLoader.{Adjacency, Vertex}

import scala.collection.mutable

object ShortestPath {

  def main(args: Array[String]): Unit = {
    if (args.length != 5) {
      printUsage()
      System.exit(1)
    }
    val inputFile = args(0)
    val start = Vertex(args(1),args(2))
    val end = Vertex(args(3), args(4))
    val shortestPaths = search(inputFile, start)
    shortestPaths.get(end) match {
      case Some(_) => printResult(start, end, shortestPaths)
      case None => println(s"Could not find a path from ${start.avenue},${start.street} to ${end.avenue},${end.street}")
    }
  }

  /**
    * Uses Dijkstra's algorithm to find the shortest path from the start intersection to
    * every other intersection
    * @param start the root intersection from which to start the search
    * @return a map of shortest paths from the start to every other intersection
    */
  def search(inputFile: String, start: Vertex): Map[Vertex, PathElement] = {
    val adjacencyMap = DataLoader.loadTrafficData(inputFile)

    val travelTimes = initialTravelTimes(start, adjacencyMap)
    val shortestPaths = mutable.HashMap.newBuilder.addOne(start -> PathElement(0, start)).result()

    while (travelTimes.nonEmpty) {
      val currentVertex = travelTimes.values.toSeq.min
      travelTimes.remove(currentVertex.vertex)
      adjacencyMap.get(currentVertex.vertex).foreach {
        neighbors => {
          neighbors.foreach {
            neighbor => {
              travelTimes.get(neighbor.vertex).foreach {
                neighborVertex => {
                  val priorTravelTime = neighborVertex.travelTimeFromStart
                  val currentTravelTime = currentVertex.travelTimeFromStart + neighbor.averageTravelTime
                  if (currentTravelTime < priorTravelTime) {
                    travelTimes.put(neighborVertex.vertex, DistantVertex(neighborVertex.vertex, currentTravelTime))
                    shortestPaths.put(neighborVertex.vertex, PathElement(currentTravelTime, currentVertex.vertex))
                  }
                }
              }
            }
          }
        }
      }
    }
    shortestPaths.toMap
  }

  /**
    * @param end the destination intersection (vertex)
    * @param pathElements the map of shortest paths created during the search phase
    * @return the list of vertices forming the fastest route between the start and end
    */
  private def fastestRoute(end: Vertex, pathElements: Map[Vertex, PathElement]) = {
    var vertex = end
    var travelTime = pathElements.get(vertex).head.travelTimeFromStart
    val route = mutable.ListBuffer[Vertex]()
    route.append(vertex)
    while (travelTime != 0) {
      vertex = pathElements.get(vertex).head.priorVertex
      travelTime = pathElements.get(vertex).head.travelTimeFromStart
      route.append(vertex)
    }
    route.reverse
  }

  /**
    * @return a map containing all vertices with their initial travel times set to positive infinity. (The exception
    *         being the start node, which has an initial travel time of 0.) The resultant map will be updated as the
    *         search progresses.
    */
  private def initialTravelTimes(start: Vertex, adjacencyMap: Map[Vertex, Set[Adjacency]]) = {
    val travelTimes = mutable.HashMap.newBuilder.addAll(
      adjacencyMap
        .keySet
        .map(vertex => vertex -> DistantVertex(vertex, Double.PositiveInfinity)))
      .result()
    travelTimes.put(start, DistantVertex(start, 0))
    travelTimes
  }

  private def printResult(start: Vertex, end: Vertex, shortestPaths: Map[Vertex, PathElement]): Unit = {
    shortestPaths.get(end).foreach {
      pathElement => {
        val route = fastestRoute(end, shortestPaths)
        val roadSegments = mutable.ListBuffer[RoadSegment]()
        if (route.nonEmpty) {
          var fromVertex = route.head
          route.dropInPlace(1)
          while (route.nonEmpty) {
            val toVertex = route.head
            roadSegments.append(RoadSegment(s"${fromVertex.avenue},${fromVertex.street} -> ${toVertex.avenue},${toVertex.street}"))
            fromVertex = toVertex
            route.dropInPlace(1)
          }
        }
        val directions = Directions(
          s"${start.avenue},${start.street}",
          s"${end.avenue},${end.street}",
          roadSegments.toList,
          s"Total transit time: ${pathElement.travelTimeFromStart}"
        )
        implicit val formats: Formats = Serialization.formats(NoTypeHints)
        println(write(directions))
      }
    }
  }

  private def printUsage(): Unit = {
    println(
      """
        |
        |Usage:
        |  Provide a data file, a start intersection, and end intersection as arguments with each component separated by a space.
        |  For example:
        |
        |  >sbt run trafficMeasurements.json A 1 F 16
        |
        |  will find the shortest path between intersection A,1 and F,16
        |
      """.stripMargin)
  }

  case class PathElement(travelTimeFromStart: Double, priorVertex: Vertex)

  case class DistantVertex(vertex: Vertex, travelTimeFromStart: Double) extends Ordered[DistantVertex] {
    override def compare(that: DistantVertex): Int = {
      if (this.travelTimeFromStart > that.travelTimeFromStart) 1
      else if (this.travelTimeFromStart < that.travelTimeFromStart) -1
      else 0
    }
  }

  case class Directions(start: String,
                        end: String,
                        roadSegments: List[RoadSegment],
                        transitTime: String)

  case class RoadSegment(segment: String)

}
