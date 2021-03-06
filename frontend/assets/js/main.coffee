# Configuration
# API calls configuration
apiPrefix = 'api'
# apiPrefix = 'http://timetables.maxmouchet.com/api'

# Scheduler configuration
scheduler.config.readonly = true
scheduler.config.first_hour = 8
scheduler.config.last_hour  = 20

# API methods
get = (path, callback) ->
  $.get "#{ apiPrefix }/#{ path }", callback

getStatus = (callback) ->
  get 'status.json', callback

getCourses = (callback) ->
  get 'courses.json', callback

getGroups = (courseId, callback) ->
  get "courses/#{ courseId }/groups.json", callback

getSchedules = (courseId, group, callback) ->
  get "courses/#{ courseId }/schedules.json", (data) ->
    schedules = []

    for schedule in data
      if schedule.group == group
        schedules.push schedule

    callback(schedules)

# Some classes used later
class ActiveArray
  constructor: (@addCallback, @deleteCallback) -> @array = []

  add: (item) ->
    @array.push item
    @addCallback item

  # delete: (item) ->
  #   @array.splice(@array.indexOf(item), 1)
  #   @deleteCallback item

  deleteById: (id) ->
    for item in @array
      if item.id == id
        @array.splice(@array.indexOf(item), 1)
        @deleteCallback item

  exists: (id) ->
    found = false
    for item in @array
      found = true if item.id == id
    return found

class CachedCourse
  constructor: (@id, @name, @groups) -> @selectedGroup = @groups[0]

  setSchedules: (schedules) -> @schedules = schedules

  setGroups: (groups) -> @groups = groups

  setSelectedGroup: (group) -> @selectedGroup = group

# UI methods
# Enable the status popover
enableStatusPopover = (status) ->
  $('#status_link').popover({
    html: true,
    content: "<strong>Last update: </strong>#{ status.update_time }<br /><strong>Courses: </strong>#{ status.courses_count }<br /><strong>Schedules: </strong>#{ status.schedules_count }"
  })


# Populate the picker from an array of courses
populatePicker = (data) ->
  format = (item) -> item.name

  $('#coursesPicker').select2
    placeholder: 'Select a course'
    data:
      results: data
      text: 'name'
    formatSelection: format
    formatResult: format

# Create the JSON data to be displayed by the scheduler
# Required syntax is here: http://docs.dhtmlx.com/scheduler/how_to_start.html#step5loadingdata
buildSchedulerData = (course) ->
  data = []

  for schedule in course.schedules
    data.push {
      id: schedule.id,
      text: "#{ schedule.courseName }\n#{ schedule.room }\n#{ schedule.professor }",
      start_date: moment(schedule.startDateTime).format("MM/DD/YYYY HH:mm"),
      end_date: moment(schedule.endDateTime).format("MM/DD/YYYY HH:mm")
    }

  data

addCourseToList = (course) ->
  liElement = $('<li />')
  liElement.attr('data-id', course.id)

  # Build removal link
  removalLink = $('<a />')
  removalLink.html('X')
  removalLink.attr('href', 'javascript:void(0)')
  removalLink.addClass('removalLink')
  removalLink.click (e) ->
    id = $(e.target).parent().data('id')
    selectedCourses.deleteById(id)

  liElement.html(course.name)
  liElement.append(removalLink)

  # Build select
  selectElement = $('<select />')
  selectElement.change (e) ->
    id    = $(e.target).parent().data('id')
    group = e.target.value

    getSchedules id, group, (schedules) ->
      course.setSelectedGroup(group)

      deleteCourseFromScheduler(course)
      addCourseToScheduler(course, group)

      updateUrl(course)

  for group in course.groups
    node = '<option />'
    node = '<option selected="selected" />' if group == course.selectedGroup

    selectElement.append($(node).html(group))

  selectElement.attr('disabled', '') if course.groups.length <= 1

  # Append li
  liElement.append(selectElement)
  $('nav ul').append(liElement)

deleteCourseFromList = (course) ->
  $("li[data-id='#{ course.id }']").remove()


addCourseToScheduler = (course) ->
  getSchedules course.id, course.selectedGroup, (schedules) ->
    course.setSchedules(schedules)
    scheduler.parse(buildSchedulerData(course), 'json')

deleteCourseFromScheduler = (course) ->
  for schedule in course.schedules
    scheduler.deleteEvent(schedule.id)

# Methods for loading and saving data to the URL
loadFromUrl = (courses) ->
  return if location.search == ""

  savedCourses = location.search.split('=')[1].split(',')

  for savedCourse in savedCourses
    shortName  = savedCourse.split(':')[0]
    group = savedCourse.split(':')[1]

    for course in courses
      if shorten(course.name).toLowerCase() == shortName.toLowerCase()
        populateCachedCourse course.id, course.name, group, (course) -> selectedCourses.add(course)

updateUrl = (dummy) ->
  url = location.origin + location.pathname + '?courses='

  for course in selectedCourses.array
    url += shorten(course.name) + ':' + course.selectedGroup + ','

  window.history.pushState(dummy, 'dummy', url)

shorten = (name) ->
  name.replace(/\s+|,|\.|:/g, '')

populateCachedCourse = (courseId, courseName, selectedGroup, callback) ->
  getGroups courseId, (groups) ->
    course = new CachedCourse(courseId, courseName, groups)
    course.setSelectedGroup(selectedGroup) if selectedGroup != undefined
    callback(course)

# Methods for generating and saving the ics file
generateiCal = ->
  calendar = new iCalendar.CalendarBuilder

  for course in selectedCourses.array
    for schedule in course.schedules
      eventBuilder = new iCalendar.EventBuilder
      eventBuilder.setSummary("#{ schedule.courseName } #{ schedule.room } #{ schedule.professor }")
      eventBuilder.setStartDate(moment(schedule.startDateTime).toDate())
      eventBuilder.setEndDate(moment(schedule.endDateTime).toDate())
      calendar.addEvent(eventBuilder.getEvent())

  return calendar.getCalendar()

saveiCal = ->
  calendar = generateiCal()

  # Code from http://stackoverflow.com/a/18197341/1262501
  pom = document.createElement('a');
  pom.setAttribute('href', 'data:text/calendar;charset=utf-8,' + encodeURIComponent(calendar.toString()))
  pom.setAttribute('download', 'VAMK.ics')

  document.body.appendChild(pom)
  pom.click()
  document.body.removeChild(pom)

# Application
scheduler.init('scheduler_here', new Date, 'week')

courseAdded = (course) ->
  addCourseToList(course)
  addCourseToScheduler(course)
  updateUrl(course)

courseDeleted = (course) ->
  deleteCourseFromList(course)
  deleteCourseFromScheduler(course)
  updateUrl(course)

selectedCourses = new ActiveArray(courseAdded, courseDeleted)

$('#coursesPicker').on 'change', (e) ->
  getGroups e.added.id, (groups) ->
    unless selectedCourses.exists(e.added.id)
      selectedCourses.add(new CachedCourse(e.added.id, e.added.name, groups))

getStatus(enableStatusPopover)

getCourses(loadFromUrl)
getCourses(populatePicker)

$('#downloadLink a').on 'click', ->
  saveiCal()

#
# Idea: Cache list in local storage so it loads faster next times.
# TODO: Manage previous/next states
#
