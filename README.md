Проект состоит из 2-х подгрупп:

 **spring-servers** - сервисы спринга
- config-server - хранит конфиг в гите, предоставляет API
- eureka-server - предоставляет сервис дисковери
- gateway-server - единая точка входа в API

 **voip-services** - бизнес логика
- cucm-soap-module - классы для работы с API Cisco Call Manager
-  phone-service - предоствляет апи для конфигурации телефонии
-  statistics-service - предоставляет апи для получения информации по звонкам, операторам и тд
-  vc-service - апи для взаимодействия с видеоконферцсвязью
