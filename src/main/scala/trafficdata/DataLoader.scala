package trafficdata

import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, Formats}

import scala.io.Source
import scala.util.{Failure, Success, Using}

object DataLoader {

  /**
    * Loads traffic data from the json resource and produces an adjacency map suitable for graph processing
    * @return the adjacency map, where each vertex is associated with its neighbors
    */
  def loadTrafficData(dataFilePath: String): Map[Vertex, Set[Adjacency]] = {
    implicit val formats: Formats = DefaultFormats

    Using(Source.fromFile(dataFilePath)) { bufferedSource =>
      val fileContent = bufferedSource.getLines().reduce(_ + _)
      val json = parse(fileContent)
      val trafficMeasurementsJson = json \ "trafficMeasurements"
      val trafficMeasurements = trafficMeasurementsJson.extract[List[TrafficMeasurements]]

      val edges = trafficMeasurements
        .flatMap(_.measurements)
        .groupBy(edge)
        .map(edgeWithAverageMeasurement)

      adjacencyMap(edges)
    } match {
      case Success(adjacencyMap) => adjacencyMap
      case Failure(e) =>
        e.printStackTrace()
        sys.error("Error reading input file")
    }
  }

  /**
    * @return the edge data from a traffic measurement, where an edge is represented by two vertices (intersections)
    */
  private def edge(measurement: TrafficMeasurement) = {
    Edge(
      Vertex(measurement.startAvenue, measurement.startStreet),
      Vertex(measurement.endAvenue, measurement.endStreet)
    )
  }

  /**
    * @return the edge data associated with its average measurement
    */
  private def edgeWithAverageMeasurement(edgeWithMeasurements: (Edge, List[TrafficMeasurement])) = {
    val edge = edgeWithMeasurements._1
    val measurements = edgeWithMeasurements._2
    val length = measurements.length
    val averageMeasurement = measurements.map(_.transitTime).sum / length
    AverageEdge(edge.from, edge.to, averageMeasurement)
  }

  /**
    * @return a map of vertices, each associated with its set of adjacencies (neighbors) and respective travel times
    */
  private def adjacencyMap(edges: Iterable[AverageEdge]) = {
    val fromVertices = edges
      .groupBy(_.from)
      .map(vertexWithAdjacencies(_.to))

    val toVertices = edges
      .groupBy(_.to)
      .map(vertexWithAdjacencies(_.from))

    union(fromVertices, toVertices)
  }

  /**
    * @return a vertex with its associated adjacencies, including travel times
    */
  private def vertexWithAdjacencies(vertexSupplier: AverageEdge => Vertex)(vertexWithAdjacencies: (Vertex, Iterable[AverageEdge])) = {
    val vertex = vertexWithAdjacencies._1
    val edges = vertexWithAdjacencies._2
    val adjacencies = edges.map(edge => Adjacency(vertexSupplier(edge), edge.averageTravelTime))
    vertex -> adjacencies
  }

  /**
    * @return the union of two maps containing iterable values. The values in the resultant map are flattened sets
    */
  private def union[K, V](m1: Map[K, Iterable[V]], m2: Map[K, Iterable[V]]) = {
    val m = (m1.toSeq ++ m2.toSeq).groupMap(_._1)(_._2)
    m.map {
      case (k, v) => k -> v.flatten.toSet
    }
  }

  case class TrafficMeasurement(startAvenue: String,
                                startStreet: String,
                                transitTime: Double,
                                endAvenue: String,
                                endStreet: String)

  case class TrafficMeasurements(measurementTime: Long, measurements: List[TrafficMeasurement])

  case class Vertex(avenue: String, street: String)

  case class Edge(from: Vertex, to: Vertex)

  case class AverageEdge(from: Vertex, to: Vertex, averageTravelTime: Double)

  case class Adjacency(vertex: Vertex, averageTravelTime: Double)
}
