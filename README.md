# Graph Analytics Project Report

## Data Summary

| Field            | Meaning                                         |
|------------------|-------------------------------------------------|
| **Vertices**     | number of vertices in original graph            |
| **Edges**        | number of edges in original graph               |
| **directed**     | always true for this assignment context         |
| **source**       | designated source vertex for DAG Shortest-Paths |
| **weight_model** | additive integer weights                        |


| Field         | Meaning                                                    |
| ------------- | ---------------------------------------------------------- |
| **SCC count** | number of strongly connected components found via Kosaraju |
| **C**         | number of nodes in condensation DAG                        |

All experimental results are stored into a single CSV file: `results.csv`

---

## Results (CSV)

| Column        | Meaning                                     |
|---------------| ------------------------------------------- |
| dataset       | file name                                   |
| Vertices      | original vertices                           |
| Edges         | original edges                              |
| SCC_count     | number of strongly connected components     |
| condensed_C   | number of DAG components after condensation |
| shortest_dist | best (max) reachable shortest path dist     |
| longest_value | longest critical path value                 |
| SCC_time_ns   | Kosaraju runtime (ns)                       |
| SCC_dfsVisits | dfs visits count                            |
| SCC_dfsEdges  | dfs edges processed                         |
| topo_time_ns  | Kahn runtime (ns)                           |
| topo_pushes   | # queue pushes                              |
| topo_pops     | # queue pops                                |
| dagsp_time_ns | DAG-SP runtime (ns)                         |
| dagsp_relax   | relaxations count                           |

---

## Analysis

### SCC / Condensation

* Kosaraju cost is dominated by DFS.
* High density graphs → more DFS edges → longer runtime.
* Condensation dramatically reduces problem size.
  Often C << n

### Topological Sort (Kahn)

* Performance influenced by structure of condensation DAG.
* wide DAG → more pushes, shallow pop chain
* deep linear DAG → pop operations dominate

### DAG Shortest Paths

* Always linear O(C + edges_in_condensed_DAG)
* zero PQ overhead
* extremely efficient after condensation

### Structural Effect Summary

| Case                  | Effect                                                   |
| --------------------- | -------------------------------------------------------- |
| 1 huge SCC            | condensation trivial C=1 → topo + dagSP essentially free |
| many small SCCs       | condensation still reduces cycles → fast DAG processing  |
| very dense original G | SCC DFS work increases significantly                     |

---

## Conclusions & Recommendations

* Always do SCC first if cycles exist (this is the only scalable pattern).
* Condense before any DAG processing.
* Kahn topo sort is most stable and predictable once we are in DAG territory.
* DAG Shortest Path algorithms are the fastest possible shortest path strategy when cycles are eliminated.