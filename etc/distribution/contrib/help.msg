Usage: genesis [-options]
Options are: 

    -h            this help message
    -v            prints product version and exists

  these options are the names of definition files in
  the Genesis Configuration Hierarchy:
    -e            enterprise
    -d            datacentre
    -r            rack
    -n            node
    -s            service
    -i            environment

    -a            the application archetype 
    -t            the ant target to execute 

For example:
genesis -e MyEnterprise -d MyDatacentre -r MyRack -n MyNode -s MyService -i MyEnvironment -a MyApplication -t build

Supported Archetypes:

    MyApplication - An example application archetype
    FC9AHS22DB    - Apache HTTP Server default build for Fedora Core 9
    wlserver103   - Oracle WebLogic Server 10.3

