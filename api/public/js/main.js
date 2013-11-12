(function() {
  var addCourse, bindChangeGroup, bindRemoveCourse, domain, format, listIds, recupSave;

  scheduler.config.readonly = true;

  scheduler.config.first_hour = 8;

  scheduler.config.last_hour = 20;

  scheduler.init('scheduler_here', new Date(), "week");

  if (window.location.host !== "api.olamas.me") {
    domain = "http://api.olamas.me/";
  } else {
    domain = "";
  }

  recupSave = function(data) {
    var course, sCourse, save, savedCourses, splited, _i, _len, _results;
    if (location.host !== "") {
      save = location.search.split("=")[1];
      savedCourses = decodeURIComponent(save).split("||");
      _results = [];
      for (_i = 0, _len = savedCourses.length; _i < _len; _i++) {
        sCourse = savedCourses[_i];
        splited = sCourse.split("|");
        _results.push((function() {
          var _j, _len1, _results1;
          _results1 = [];
          for (_j = 0, _len1 = data.length; _j < _len1; _j++) {
            course = data[_j];
            if (course.name === splited[0]) {
              _results1.push(addCourse(course.id, course.name, splited[1]));
            } else {
              _results1.push(void 0);
            }
          }
          return _results1;
        })());
      }
      return _results;
    }
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
      return $.get("" + domain + "courses/" + currentCourse + "?group=" + ($("option:selected", e.target).text()), function(data) {
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
          _results.push(listIds[currentCourse].push(course.id));
        }
        return _results;
      });
    });
  };

  format = function(item) {
    return item.name;
  };

  $.get("" + domain + "courses", function(data) {
    recupSave(data);
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

  addCourse = function(idCourse, name, group) {
    if (listIds[idCourse] === void 0) {
      return $.get("" + domain + "groups?course=" + idCourse, function(listGroup) {
        var dGroup, newLi, newSelect, _i, _len;
        newSelect = $("<select />");
        if (listGroup.length !== 1) {
          for (_i = 0, _len = listGroup.length; _i < _len; _i++) {
            dGroup = listGroup[_i];
            newSelect.append($("<option />").html(dGroup));
          }
        } else {
          newSelect.attr("disabled", "");
          newSelect.append($("<option />").html(listGroup[0]));
        }
        newLi = $("<li />").html($("<span />").append(name)).append($("<br />")).append(newSelect);
        $("nav ul").append(newLi.attr("data-id", idCourse).prepend($("<a />").attr("href", "#").html("X")));
        if (group === "") {
          group = listGroup[0];
        }
        return $.get("" + domain + "courses/" + idCourse + "?group=" + group, function(listEvents) {
          var cEvent, _j, _len1;
          scheduler.parse(listEvents, "json");
          listIds[idCourse] = [];
          for (_j = 0, _len1 = listEvents.length; _j < _len1; _j++) {
            cEvent = listEvents[_j];
            listIds[idCourse].push(cEvent.id);
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
  };

  $("#coursesPicker").on("change", function(e) {
    $("#coursesPicker").attr("disabled", "");
    return addCourse(e.added.id, e.added.name, "");
  });

  $("#getLink").click(function(e) {
    var saveList, url;
    saveList = "";
    $("nav ul li").each(function(index, tag) {
      if (index !== 0) {
        saveList += "||";
      } else {
        saveList = "";
      }
      return saveList += $("span:eq(0)", tag).text() + "|" + $("option:selected", tag).text();
    });
    url = ("http://" + location.host + "/index.html?save=") + encodeURIComponent(saveList);
    window.prompt("Copy to clipboard: Ctrl+C, Enter", url);
    return window.location.href = url;
  });

}).call(this);
