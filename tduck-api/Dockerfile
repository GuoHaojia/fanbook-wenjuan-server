FROM tarscloud/tars.javabase
EXPOSE 8999

# /etc/localtime as a solt link will block container mount /etc/localtime from host
RUN rm -rf /etc/localtime

VOLUME /tmp

ADD target/tduck-api.jar /app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar", "-Duser.timezone='Asia/Shanghai'","/app.jar"]
