package trafficdata

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import trafficdata.DataLoader.Vertex

class ShortestPathSpec extends AnyFunSpec with Matchers {

  /**
    *  Test data in the test/resources folder represents a grid in the following
    *  configuration, which mirrors the real data. Note there is an "expressway"
    *  connecting intersection A1 with B3, representing a "shortcut"
    *
    *
    *                A                  B                  C
    *                |                  |                  |
    *         1 -----|------- 3 --------|-------- 6 -------|-----
    *              / |                  |                  |
    *            /   |                  |                  |
    *          /     4                  8                  16
    *        /       |                  |                  |
    *      /         |                  |                  |
    *    /    2 -----|------- 9 --------|-------- 12 ------|-----
    *    |           |                  |                  |
    *    |           |                  |                  |
    *    |           20                 24                 28
    *    |           |                  |                  |
    *    |           |                  |                  |
    *    |    3 -----|------- 15 -------|-------- 18 ------|------
    *    |           |                / |                  |
    *    |           |              /   |                  |
    *    |                        /
    *    |                      /
    *    |                    /
    *    |------ 30 --------/
    *
    */
  describe("Shortest Path Spec") {

    it("should determine the shortest path from A,1 to C,2 as through B,2 at 23.0 units") {
      val shortestPaths = ShortestPath.search("testTrafficMeasurements.json", Vertex("A", "1"))
      val pathElementOption = shortestPaths.get(Vertex("C", "2"))
      pathElementOption shouldBe defined

      val pathElement = pathElementOption.get
      pathElement.travelTimeFromStart shouldEqual 23.0
      pathElement.priorVertex shouldEqual Vertex("B", "2")
    }

    it("should determine the shortest path from A,1 to C,3 as through B,3 at 48.0 units") {
      val shortestPaths = ShortestPath.search("testTrafficMeasurements.json", Vertex("A", "1"))
      val pathElementOption = shortestPaths.get(Vertex("C", "3"))
      pathElementOption shouldBe defined

      val pathElement = pathElementOption.get
      pathElement.travelTimeFromStart shouldEqual 48.0
      pathElement.priorVertex shouldEqual Vertex("B", "3")
    }
  }
}
