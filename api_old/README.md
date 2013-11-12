# VAMK Timetable API #

## Routes

`GET /groups`
```json
["I-IT-2N2","I-YT-5V","I-IT-1N3","I-YT-3N2","I-RT-1N1","I-TT-2N2","I-KT-3N1","I-TT-4N2"]
```

`GET /groups?course=238`
```json
["I-IT-1N2","I-IT-1N1","I-IT-1N3","I-IT-1N4"]
```

`GET /courses`
```json
[
  {"id":238,"name":"Communication Skills "},
  {"id":239,"name":"DC and AC Circuits "},
  {"id":240,"name":"Tuton Tutoring"},
  {"id":241,"name":"Tutol Tutoring"},
  {"id":242,"name":"Introduction to Technical Mathematics "},
  {"id":243,"name":"Basics of Electronics and Labs "},
  {"id":244,"name":"Orientation Day for Foreign Degree Students (RA231)"},
  {"id":245,"name":"Introduction Day"}
]
```

`GET /courses/238`
```json
[
  {"id":829,"text":"Communication Skills \n WB210","start_date":"09/17/2013 08:15","end_date":"09/17/2013 10:00"},
  {"id":831,"text":"Communication Skills \n WB210","start_date":"10/22/2013 08:15","end_date":"10/22/2013 10:00"},
  {"id":842,"text":"Communication Skills \n WC1160","start_date":"09/19/2013 10:15","end_date":"09/19/2013 11:00"},
  {"id":844,"text":"Communication Skills \n WC1160","start_date":"10/24/2013 10:15","end_date":"10/24/2013 11:00"},
  {"id":852,"text":"Communication Skills \n WC1160","start_date":"10/24/2013 11:45","end_date":"10/24/2013 12:30"}
]
```
`GET /courses/238?group=I-IT-1N1`
```json
[
  {"id":829,"text":"Communication Skills \n WB210","start_date":"09/17/2013 08:15","end_date":"09/17/2013 10:00"},
  {"id":831,"text":"Communication Skills \n WB210","start_date":"10/22/2013 08:15","end_date":"10/22/2013 10:00"},
  {"id":842,"text":"Communication Skills \n WC1160","start_date":"09/19/2013 10:15","end_date":"09/19/2013 11:00"}
]
```
