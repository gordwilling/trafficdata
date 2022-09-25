package trafficdata

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import trafficdata.DataLoader.{Adjacency, Vertex, loadTrafficData}

class DataLoaderSpec extends AnyFunSpec with Matchers {

  describe("Data Loader Spec") {

    it("should create an adjacency list with average travel times between vertices") {
      val adjacencies = loadTrafficData("./testTrafficMeasurements.json")
      adjacencies.size shouldEqual 9
      adjacencies should contain(Vertex("A", "1") -> Set(Adjacency(Vertex("B", "1"), 3.0), Adjacency(Vertex("A", "2"), 4.0), Adjacency(Vertex("B", "3"), 30.0)))
      adjacencies should contain(Vertex("B", "1") -> Set(Adjacency(Vertex("C", "1"), 6.0), Adjacency(Vertex("B", "2"), 8.0)))
      adjacencies should contain(Vertex("C", "1") -> Set(Adjacency(Vertex("C", "2"), 16.0)))

      adjacencies should contain(Vertex("A", "2") -> Set(Adjacency(Vertex("B", "2"), 9.0), Adjacency(Vertex("A", "3"), 20.0)))
      adjacencies should contain(Vertex("B", "2") -> Set(Adjacency(Vertex("C", "2"), 12.0), Adjacency(Vertex("B", "3"), 24.0)))
      adjacencies should contain(Vertex("C", "2") -> Set(Adjacency(Vertex("C", "3"), 28.0)))

      adjacencies should contain(Vertex("A", "3") -> Set(Adjacency(Vertex("B", "3"), 15.0)))
      adjacencies should contain(Vertex("B", "3") -> Set(Adjacency(Vertex("C", "3"), 18.0)))
      adjacencies should contain(Vertex("C", "3") -> Set())
    }
  }
}
