scheduler.config.readonly = true
scheduler.config.first_hour = 8;
scheduler.config.last_hour = 20;

scheduler.init('scheduler_here', new Date(),"week")

listCourses = {
  "Web Services": "I-IT-4N1"
  "Microsoft .NET Programming": "I-IT-4N1"
  "XML Technology": "I-IT-3N1"
  "Energy Technology ICT": "I-IT-3N1"
  "Microcontrollers Laboratory": "I-IT-3N1"
  "Wireless Networks": "I-IT-3N1"
  "Web Services": "I-IT-4N1"
}

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
    $.get "http://api.olamas.me/courses/#{currentCourse}?group=#{$("option:selected", e.target).text()}", (data)->
      for cEvent in listIds[currentCourse]
        scheduler.deleteEvent(cEvent)
      scheduler.parse(data, "json")
      listIds[currentCourse] = []
      for course in data
        listIds[currentCourse].push(course.id)
        console.log JSON.stringify(listIds)

format = (item) -> item.name

$.get "http://api.olamas.me/courses", (data)->
  $("#coursesPicker").select2
    placeholder: "Select a course"
    data: 
      results: data 
      text: 'name'
    formatSelection: format
    formatResult: format
 
$("#coursesPicker").on "change", (e)-> 
  $("#coursesPicker").attr "disabled", ""
  if listIds[e.added.id] is undefined
    $.get "http://api.olamas.me/groups?course=#{e.added.id}", (listGroup)->
      if listGroup.length isnt 1
        newSelect = $("<select />")
        for group in listGroup
          newSelect.append($("<option />").html(group))
        newLi = $("<li />").html(e.added.name).append($("<br />")).append(newSelect)
      else
        newLi = $("<li />").html(e.added.name+" - "+listGroup[0])
      $("nav ul").append(newLi.attr("data-id", e.added.id).prepend($("<a />").attr("href", "#").html("X")))
      $.get "http://api.olamas.me/courses/#{e.added.id}?group=#{listGroup[0]}", (listEvents)->
        scheduler.parse(listEvents, "json")
        listIds[e.added.id] = []
        for cEvent in listEvents
          listIds[e.added.id].push(cEvent.id)
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
