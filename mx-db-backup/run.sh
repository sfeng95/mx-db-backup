#! /bin/bash
image_name="zkys-sys-canal"
port=7030

echo "----------Prepare to deploy app"${image_name}"-------------"
echo "--  power by Liyewang "
echo ""
echo ""
cat>Dockerfile<<EOF
FROM kdvolder/jdk8
MAINTAINER liyewang
EXPOSE ${port}
ADD target/*.jar  /app.jar
ENTRYPOINT ["java", "-Dhttp.proxyHost=47.113.102.165", "-Dhttp.proxyPort=3128","-Dhttps.proxyHost=47.113.102.165","-Dhttps.proxyPort=3128","-Xmx512m","-Xms512m","-Xmn256m","-Xss512k","-Dfile.encoding=utf-8","-jar","/app.jar","--spring.config.location=/home/app/application.yml"]
EOF
docker rm -f ${image_name}
docker rmi -f ${image_name}
docker build --no-cache -t ${image_name} .
#docker run -d --name ${image_name} -p ${port}:${port}  --add-host=nacos.host:${nacos_host} --add-host=canal-client:${canal_client}  --restart=always   ${image_name}
echo ""
echo ""
echo "----------build app completed !-------------"