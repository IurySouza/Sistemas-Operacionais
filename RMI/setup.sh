#!/bin/bash

javac TicketClient.java
mkdir public_html
cp TicketServer.class TicketServerImpl_Stub.class ./public_html/
javac TicketServer.java TicketServerImpl.java -Xlint
rmic TicketServerImpl
javac TicketClient.java
cp TicketServer.class TicketServerImpl_Stub.class ./public_html/
echo "-------------------------------------------"
echo "RMI Pronto, agora abra um novo terminal e rode ./server.sh"
echo "-------------------------------------------"
rmiregistry