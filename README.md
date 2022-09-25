## Traffic Data Exercise

This project reads a file containing traffic flow information on some ficitious streets. It can then calculated the shortest path between any two intersections.

### Usage

Input the coordinates of the start intersection and the end intersection to find the shortest path

```
sbt run trafficMeasurements.json A 1 F 22
```


```json
{
  "start": "A,1",
  "end": "F,22",
  "roadSegments": [
    {
      "segment": "A,1 -> A,30"
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
      "segment": "D,23 -> E,23"
    },
    {
      "segment": "E,23 -> F,23"
    },
    {
      "segment": "F,23 -> F,22"
    }
  ],
  "transitTime": "Total transit time: 414.02394752464375"
}

```
