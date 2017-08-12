FROM openjdk:8

ADD ./build/libs/Bookmarksync-all-1.0.jar /Bookmarksync.jar

CMD ["java","-jar","Bookmarksync.jar"]