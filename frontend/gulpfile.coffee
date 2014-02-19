gulp = require 'gulp'

sass       = require 'gulp-sass'
clean      = require 'gulp-clean'
concat     = require 'gulp-concat'
rename     = require 'gulp-rename'
uglify     = require 'gulp-uglify'
coffee     = require 'gulp-coffee'
coffeelint = require 'gulp-coffeelint'
livereload = require 'gulp-livereload'

buildPath = 'build/'
jsPath    = 'assets/js/'
cssPath   = 'assets/css/'

gulp.task 'html', ->
  gulp.src('index.html')
    .pipe(gulp.dest(buildPath))

gulp.task 'vendor', ->
  gulp.src('vendor/**')
    .pipe(gulp.dest(buildPath))

gulp.task 'lint', ->
  gulp.src(jsPath + '*.coffee')
    .pipe(coffeelint())
    .pipe(coffeelint.reporter())

gulp.task 'coffee', ->
  gulp.src(jsPath + '*.coffee')
    .pipe(coffee())
    .pipe(concat('all.js'))
    .pipe(uglify())
    .pipe(rename('all.min.js'))
    .pipe(gulp.dest(buildPath + jsPath))

gulp.task 'sass', ->
  gulp.src(cssPath + 'main.scss')
    .pipe(sass({ outputStyle: 'compressed' }))
    .pipe(rename('main.min.css'))
    .pipe(gulp.dest(buildPath + cssPath))

gulp.task 'watch', ->
  server = livereload()
  gulp.watch(jsPath  + '*.coffee', [ 'lint', 'coffee' ])
  gulp.watch(cssPath + '*.scss',   [ 'sass' ])
  gulp.watch('index.html', [ 'html' ])
  gulp.watch(buildPath + 'assets/**').on  'change', (file) -> server.changed(file.path)
  gulp.watch(buildPath + 'index.html').on 'change', (file) -> server.changed(file.path)

gulp.task 'clean', ->
  gulp.src(buildPath, { read: false })
    .pipe(clean())

gulp.task 'build', [ 'html', 'vendor', 'lint', 'coffee', 'sass' ]

gulp.task 'default', [ 'build', 'watch' ]
