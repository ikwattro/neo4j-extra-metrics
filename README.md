# Neo4j Extra Metrics extension

Extension providing additional metrics for database labels and relationship type counts.

## Installation

Clone this repository

```bash
git clone git@github.com:ikwattro/neo4j-extra-metrics
```

Build the package

```bash
mvn clean package -DskipTests
```

Install the produced jar file to the `plugins` directory of your Neo4j instance

Add the following setting to your `neo4j.conf`

```
server.metrics.enabled=true
server.metrics.namespaces.enabled=true
server.metrics.prometheus.enable=true
server.metrics.prometheus.endpoint=0.0.0.0:2004
server.metrics.filter=*
```

or in your docker compose neo4j service environment

```
- NEO4J_server_metrics_enabled=true
- NEO4J_server_metrics_namespaces_enabled=true
- NEO4J_server_metrics_prometheus_enabled=true
- NEO4J_server_metrics_prometheus_endpoint=0.0.0.0:2004
- NEO4J_server_metrics_filter=*
```

Note that this enables all metrics, refer to the [`server.metrics.filter`](https://neo4j.com/docs/operations-manual/current/reference/configuration-settings/#config_server.metrics.filter) setting , 
this plugin metrics are registered under the `extra` label.

Restart your neo4j server

Once restarted, you can inspect metrics on http://localhost:2004/metrics

```
neo4j_database_test3_extra_label_GunCrime_total 636.0
# HELP neo4j_database_neo4j_extra_internal_query_time_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.internal_query_time, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_internal_query_time_total counter
neo4j_database_neo4j_extra_internal_query_time_total 0.0
# HELP neo4j_database_test6_extra_label_Person_total Generated from Dropwizard metric import (metric=neo4j.database.test6.extra.label.Person, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test6_extra_label_Person_total counter
neo4j_database_test6_extra_label_Person_total 42006.0
# HELP neo4j_database_test4_extra_label_Arrest_total Generated from Dropwizard metric import (metric=neo4j.database.test4.extra.label.Arrest, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test4_extra_label_Arrest_total counter
neo4j_database_test4_extra_label_Arrest_total 85659.0
# HELP neo4j_database_test4_extra_label_BusStop_total Generated from Dropwizard metric import (metric=neo4j.database.test4.extra.label.BusStop, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test4_extra_label_BusStop_total counter
neo4j_database_test4_extra_label_BusStop_total 1213.0
# HELP neo4j_database_test3_extra_label_Gun_total Generated from Dropwizard metric import (metric=neo4j.database.test3.extra.label.Gun, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test3_extra_label_Gun_total counter
neo4j_database_test3_extra_label_Gun_total 269.0
# HELP neo4j_database_test3_extra_label_Location_total Generated from Dropwizard metric import (metric=neo4j.database.test3.extra.label.Location, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test3_extra_label_Location_total counter
neo4j_database_test3_extra_label_Location_total 93197.0
# HELP neo4j_database_test4_extra_label_GunCrime_total Generated from Dropwizard metric import (metric=neo4j.database.test4.extra.label.GunCrime, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_test4_extra_label_GunCrime_total counter
neo4j_database_test4_extra_label_GunCrime_total 636.0
# HELP neo4j_database_ukrail_extra_label_Path_total Generated from Dropwizard metric import (metric=neo4j.database.ukrail.extra.label.Path, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_ukrail_extra_label_Path_total counter
neo4j_database_ukrail_extra_label_Path_total 21769.0
```



