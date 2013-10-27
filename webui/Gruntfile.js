module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    coffee: {
      compile: {
        files: {
          '../api/public/js/main.js': 'js/main.coffee'
        }
      }
    },
    stylus: {
      compile: {
        options: {
          urlfunc: 'embedurl', // use embedurl('test.png') in our code to trigger Data URI embedding
          use: [
            
          ],
          import: [ 
          ]
        },
        files: {
          '../api/public/css/style.css': 'style/source.styl', // 1:1 compile
        }
      }
    },
    connect: {
      server: {
        options: {
          livereload: true,
          port: 8080,
          base: '../api/public',
        }
      }
    },
    /*clean: {
      release :['../public/*', '../public/js/**', '../public/css/**'],
      options:{
        force: true
      }
    },*/
    copy: {
      main:{
        src: ['vendor/**', 'index.html'],
        dest: '../api/public/'
      }
    },
    watch: {
      coffee: {
        files: ['js/main.coffee'],
        tasks: ['coffee'],
        options: {
          livereload: true,
        },
      },
      stylus: {
        files: ['style/source.styl'],
        tasks: ['stylus'],
        options: {
          livereload: true,
        }
      },
      copy: {
        files: ['vendor/**', 'index.html'],
        tasks: ['copy'],
        options: {
          livereload: true,
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-coffee');
  //grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-contrib-stylus');
  grunt.loadNpmTasks('grunt-contrib-copy');

  grunt.registerTask('default', ['copy', 'coffee', 'stylus']);
  grunt.registerTask('dev', ['default', 'connect', 'watch']);
  //grunt.registerTask('release', ['clean', 'jade:release', 'coffee:release', 'compass:release']);
};