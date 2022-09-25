## Traffic Data Exercise

This project reads a file containing traffic flow information on some ficitious streets. It can then calculate the shortest path between any two intersections.

### Usage

Specify the input file and the coordinates of the start intersection and the end intersection to find the shortest path

```
sbt run trafficMeasurements.json A 1 F 22
```

### Sample Output

```json
{
  "start": "A,2",
  "end": "F,22",
  "roadSegments": [
    {
      "segment": "A,2 -> A,1"
    },
    {
      "segment": "A,1 -> T,1"
    },
    {
      "segment": "T,1 -> T,30"
    },
    {
      "segment": "T,30 -> A,30"
    },
    {
      "segment": "A,30 -> A,29"
    },
    {
      "segment": "A,29 -> A,28"
    },
    {
      "segment": "A,28 -> B,28"
    },
    {
      "segment": "B,28 -> B,27"
    },
    {
      "segment": "B,27 -> B,26"
    },
    {
      "segment": "B,26 -> B,25"
    },
    {
      "segment": "B,25 -> B,24"
    },
    {
      "segment": "B,24 -> C,24"
    },
    {
      "segment": "C,24 -> D,24"
    },
    {
      "segment": "D,24 -> D,23"
    },
    {
      "segment": "D,23 -> D,22"
    },
    {
      "segment": "D,22 -> E,22"
    },
    {
      "segment": "E,22 -> F,22"
    }
  ],
  "transitTime": "Total transit time: 586.6639316189231"
}
```
