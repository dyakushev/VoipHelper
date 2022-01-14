The project consists of 2 groups:

 **spring-servers** - spring services
- config-server - holds configuration in git, provides API
- eureka-server - provides discovery service
- gateway-server - API entry point

 **voip-services** - business logic
- cucm-soap-module - classes to work with a Cisco Call Manager API
-  phone-service - telephone configuration API
-  statistics-service - call, operators statistics
-  vc-service - videoconference API
