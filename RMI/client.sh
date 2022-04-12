echo "-------------------------------------------"
echo "Cliente rodando"
echo "-------------------------------------------"
java -Djava.security.policy=java.policy  TicketClient rmi://localhost:1099/ticket1 rmi://localhost:1099/ticket2 rmi://localhost:1099/ticket3 rmi://localhost:1099/ticket4
