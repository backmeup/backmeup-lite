# BackMeUp Lite

A fork/simplified version of the BackMeUp framework, based on [Play!](http://www.playframework.com).

## Getting Started

1. Configure the application

* Create a copy of the file ``conf/application.conf.template`` named 
  ``conf/application.conf``. __Important__: make sure you really create
  a copy, and don't just rename the file. Otherwise you will likely erase
  the template from the Git repository on your next push!

* In the ``conf/application.conf.template`` file, configure the following
  application properties according to your environment:
  * Index server host and port information (can usually stay on the defaults)
  * Directory used for storing thumbnail images and thumbnail image dimensions
  * Application keys and secrets for connector plugins

2. Run the application using

     play -DapplyEvolutions.default=true run
