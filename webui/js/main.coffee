scheduler.config.readonly = true
scheduler.config.first_hour = 8;
scheduler.config.last_hour = 20;

totalTime = 0

scheduler.init('scheduler_here', new Date(),"week")

if window.location.host isnt "api.olamas.me"
  domain = "http://146.185.153.218/legacy_api/"
else
  domain = "legacy_api/"

recupSave = (data)->
  if location.host isnt ""
    save = location.search.split("=")[1]
    savedCourses = decodeURIComponent(save).split("||")
    for sCourse in savedCourses
      splited = sCourse.split("|")
      for course in data
        if course.name is splited[0]
          addCourse(course.id, course.name, splited[1])

listIds = []

bindRemoveCourse = ()->
  $("nav li").click (e)->
    if $(e.target).is("a") and listIds isnt []
      for cEvent in listIds[$(this).attr("data-id")]
        scheduler.deleteEvent(cEvent)
        $(this).remove()
      listIds[$(this).attr("data-id")] = undefined

bindChangeGroup = ()->
  $("nav li select").change (e)->
    currentCourse = $(e.target).parent().attr("data-id")
    $.get "#{domain}courses/#{currentCourse}?group=#{$("option:selected", e.target).text()}", (data)->
      for cEvent in listIds[currentCourse]
        scheduler.deleteEvent(cEvent)
      scheduler.parse(data, "json")
      listIds[currentCourse] = []
      for course in data
        listIds[currentCourse].push(course.id)

format = (item) -> item.name

$.get "#{domain}courses", (data)->
  recupSave(data)
  $("#coursesPicker").select2
    placeholder: "Select a course"
    data: 
      results: data 
      text: 'name'
    formatSelection: format
    formatResult: format

addCourse = (idCourse, name, group)->
  if listIds[idCourse] is undefined
    $.get "#{domain}groups?course=#{idCourse}", (listGroup)->
      newSelect = $("<select />")
      if listGroup.length isnt 1
        for dGroup in listGroup
          newSelect.append($("<option />").html(dGroup))
      else
        newSelect.attr("disabled", "")
        newSelect.append($("<option />").html(listGroup[0]))
      newLi = $("<li />").html($("<span />").append(name)).append($("<br />")).append(newSelect)
      $("nav ul").append(newLi.attr("data-id", idCourse).prepend($("<a />").attr("href", "#").html("X")))
      if group is "" then group = listGroup[0]
      $.get "#{domain}courses/#{idCourse}?group=#{group}", (listEvents)->
        scheduler.parse(listEvents, "json")
        listIds[idCourse] = []
        for cEvent in listEvents
          listIds[idCourse].push(cEvent.id)
          console.log cEvent.start_date
        $("#coursesPicker").select2 "val", ""
        $("#coursesPicker").prop "disabled", null
        bindRemoveCourse()
        bindChangeGroup()
        @
  else
    console.log "Course already selected"
    $("#coursesPicker").select2 "val", ""
    $("#coursesPicker").prop "disabled", null
    bindRemoveCourse()
    bindChangeGroup()
    @

addWeekHours = (currentMinDate, currentMaxDate, event)->
  @

getHour = (date)->
  console.log date
  date.split(" ")

getMinute = (date)->
  date.split " "[4].split ":"[1]

getDay = (date)->
  date.split " "[2]

getMonth = (date)->
  date.split " "[1]


$("#coursesPicker").on "change", (e)-> 
  $("#coursesPicker").attr "disabled", ""
  addCourse(e.added.id, e.added.name, "")

$("#getLink").click (e)->
  saveList = ""
  $("nav ul li").each (index, tag)->
    if index isnt 0 then saveList += "||" else saveList = ""
    saveList += $("span:eq(0)", tag).text()+"|"+$("option:selected", tag).text()
  url = "http://#{location.host}/index.html?save="+encodeURIComponent(saveList)
  window.prompt "Copy to clipboard: Ctrl+C, Enter", url
  window.location.href = url
