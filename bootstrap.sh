#!/usr/bin/env bash

# Provisioning script for Vagrant/Ubuntu 12.04.

sudo apt-get update
sudo apt-get upgrade

sudo apt-get install postgresql redis-server

# Don't work
sudo -u postgres psql -U postgres -d postgres -c "alter user postgres with password 'postgres';"
sudo -u postgres createdb dbdev

\curl -L https://get.rvm.io | bash -s stable --ruby=jruby
rvm rvmrc warning ignore allGemfiles
