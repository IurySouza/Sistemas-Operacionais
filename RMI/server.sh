echo "-------------------------------------------"
echo "Servidor rodando agora abra um novo terminal e rode ./client.sh"
echo "-------------------------------------------"
java -Djava.security.policy=java.policy  TicketServerImpl rmi://:1099/ticket1 & \
java -Djava.security.policy=java.policy  TicketServerImpl rmi://:1099/ticket2 & \
java -Djava.security.policy=java.policy  TicketServerImpl rmi://:1099/ticket4
