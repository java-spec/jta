# JTA

## Glossary

- transaction manager - provides the services and management functions required to support transaction demarcation, transactional resource management, synchronization, and transaction context propagation.

- application server - provides the infrastructure required to support the application run-time environment which includes transaction state management. An example of such an application server is an EJB server.

- resource manager - provides the application access to resources. The resource manager participates in distrubuted transactions by implementing a transaction resource interface used by the transaction manager to communicate transaction association, transaction completion and recovery work. An example of such a resource manager is a relational database server. 

- component-based transactional application that is developed to operate in a modern application server environment relies on application server to provide transaction management support through declarative transaction attribute settings. An example of this type of applications is an application developed using the industry standard Enterprise JavaBeans component architecture. In addition, some other stand-alone Java client programs may wish to control their transaction boundaries using a high-level interface provided by the application server or the transaction manager.

- communication resource manager - supports transaction context propagation and access to the transaction service for incoming and outgoing results. the JTA document does not specify requirements pertained to communication. Refer to the JTS Specification for more details on interoperability between Transaction Managers.

