The Service Level Agreements for OGC Web Services (SLA4OWS) framework is an Open Source framework for the adaption of Service Level Agreements (SLAs) in Spatial Data Infrastructures (SDIs) that are based on standards developed by the Open Geospatial Consortium (OGC).

The SLA4OWS framework enables service providers to offer their services with different service quality levels and along different business values. Furthermore, the framework utilizes Cloud Computing infrastructures to implement the offered Quality of Service (QoS) in an economical fashion. The chosen standards and technologies do not require changes to other OGC standards or compliant implementations. The SLA4OWS framework enables all geospatial service providers to extend their services offerings with SLA capabilities.

The following features are supported by default.

*   On-demand negotiation of SLAs for any kind of OGC Web Service (OWS)
* Permanent monitoring of OWS (service availability, response time and performance)
*   Mail notifications in case of service quality violations
*   Amazon EC2 infrastructure management

The following features are planned for the near future.

*   Export of advanced usage statistics and quality reports   
*   Securing REST Web Services with OAuth
*   Integration of DBMS (PostgreSQL, CouchDB, Derby)

The SLA4OWS framework is based on the following framework and technologies.

*   Maven for project management
*   Jersey for building REST web services.
*   Velocity for generating mails

A detailed installation and configuration tutorial can be found in the file 'tutorial.zip'.

For more information, please contact Bastian Baranski (<baranski@uni-muenster.de>).