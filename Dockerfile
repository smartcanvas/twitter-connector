FROM java:7

MAINTAINER Fábio Franco Uechi <fabio.uechi@gmail.com>

ADD build/install/smartcanvas-twitter-connector/ /usr/src/app

CMD ["/usr/src/app/bin/smartcanvas-twitter-connector"]