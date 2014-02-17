
## Features
- JSON API for courses, groups, and schedules.
- Web user interface for creating and sharing custom timetables.
- Extensible parsing system that supports multiple sources and outputs.

## Deployment
### Amazon S3


## Extension
### Workflows

### Outputs

### Scenarios

## Planned features
- iCal export (WIP in `add_ical_export` branch)

Official timetables for students & professors at the Vaasa University of Applied Sciences are available online as tables in HTML pages.
There is several problems to this approach :
- There is no semantic, humans can read the schedules but they are hard to parse for a computer.
- You can't export them easily to Google Calendar or your favorite calendar app.
- It's not mobile friendly.
