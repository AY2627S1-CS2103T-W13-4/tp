---
  layout: default.md
  title: "Logging guide"
---

# Logging guide

* The project uses the `java.util.logging` package for logging.
* `LogsCenter` manages logging levels and destinations.
* A class obtains a `Logger` with `LogsCenter.getLogger(Class)`, which logs messages at the configured level.
* Log messages are written to the console and a `.log` file.
* The output logging level can be controlled by changing the `LOG_LEVEL` constant in the `LogsCenter` class.
* **When choosing a level for a log message**, follow the conventions given in [_[se-edu/guides] Java: Logging conventions_](https://se-education.org/guides/conventions/java/logging.html).

For meeting filtering, `ListMeetingCommand` records an `INFO` message after applying the filter and refreshing the order.
For example, `Meeting list refreshed: tag filter applied=true, displayed meetings=2` helps explain which display state
the command produced. `ModelManager` records the matching count at `FINE` level when a meeting filter changes.
Enable `FINE` through `LogsCenter.LOG_LEVEL` when debugging those updates; the default level is `INFO`.
