(function() {
  var bindChangeGroup, bindRemoveCourse, format, listCourses, listIds;

  scheduler.config.readonly = true;

  scheduler.config.first_hour = 8;

  scheduler.config.last_hour = 20;

  scheduler.init('scheduler_here', new Date(), "week");

  listCourses = {
    "Web Services": "I-IT-4N1",
    "Microsoft .NET Programming": "I-IT-4N1",
    "XML Technology": "I-IT-3N1",
    "Energy Technology ICT": "I-IT-3N1",
    "Microcontrollers Laboratory": "I-IT-3N1",
    "Wireless Networks": "I-IT-3N1",
    "Web Services": "I-IT-4N1"
  };

  listIds = [];

  bindRemoveCourse = function() {
    return $("nav li").click(function(e) {
      var cEvent, _i, _len, _ref;
      if ($(e.target).is("a") && listIds !== []) {
        _ref = listIds[$(this).attr("data-id")];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          cEvent = _ref[_i];
          scheduler.deleteEvent(cEvent);
          $(this).remove();
        }
        return listIds[$(this).attr("data-id")] = void 0;
      }
    });
  };

  bindChangeGroup = function() {
    return $("nav li select").change(function(e) {
      var currentCourse;
      currentCourse = $(e.target).parent().attr("data-id");
      return $.get("http://api.olamas.me/courses/" + currentCourse + "?group=" + ($("option:selected", e.target).text()), function(data) {
        var cEvent, course, _i, _j, _len, _len1, _ref, _results;
        _ref = listIds[currentCourse];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          cEvent = _ref[_i];
          scheduler.deleteEvent(cEvent);
        }
        scheduler.parse(data, "json");
        listIds[currentCourse] = [];
        _results = [];
        for (_j = 0, _len1 = data.length; _j < _len1; _j++) {
          course = data[_j];
          listIds[currentCourse].push(course.id);
          _results.push(console.log(JSON.stringify(listIds)));
        }
        return _results;
      });
    });
  };

  format = function(item) {
    return item.name;
  };

  $.get("http://api.olamas.me/courses", function(data) {
    return $("#coursesPicker").select2({
      placeholder: "Select a course",
      data: {
        results: data,
        text: 'name'
      },
      formatSelection: format,
      formatResult: format
    });
  });

  $("#coursesPicker").on("change", function(e) {
    $("#coursesPicker").attr("disabled", "");
    if (listIds[e.added.id] === void 0) {
      return $.get("http://api.olamas.me/groups?course=" + e.added.id, function(listGroup) {
        var group, newLi, newSelect, _i, _len;
        if (listGroup.length !== 1) {
          newSelect = $("<select />");
          for (_i = 0, _len = listGroup.length; _i < _len; _i++) {
            group = listGroup[_i];
            newSelect.append($("<option />").html(group));
          }
          newLi = $("<li />").html(e.added.name).append($("<br />")).append(newSelect);
        } else {
          newLi = $("<li />").html(e.added.name + " - " + listGroup[0]);
        }
        $("nav ul").append(newLi.attr("data-id", e.added.id).prepend($("<a />").attr("href", "#").html("X")));
        return $.get("http://api.olamas.me/courses/" + e.added.id + "?group=" + listGroup[0], function(listEvents) {
          var cEvent, _j, _len1;
          scheduler.parse(listEvents, "json");
          listIds[e.added.id] = [];
          for (_j = 0, _len1 = listEvents.length; _j < _len1; _j++) {
            cEvent = listEvents[_j];
            listIds[e.added.id].push(cEvent.id);
          }
          $("#coursesPicker").select2("val", "");
          $("#coursesPicker").prop("disabled", null);
          bindRemoveCourse();
          bindChangeGroup();
          return this;
        });
      });
    } else {
      console.log("Course already selected");
      $("#coursesPicker").select2("val", "");
      $("#coursesPicker").prop("disabled", null);
      bindRemoveCourse();
      bindChangeGroup();
      return this;
    }
  });

}).call(this);
