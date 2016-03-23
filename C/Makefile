
CC=gcc
CFLAGS=
LDFLAGS= -lpthread
EXEC=server

# Ensemble des exécutables à produire
all: $(EXEC)

server: server.o action.o gamestate.o tasks.o clients.o grid.o 
	$(CC) -o server server.o action.o gamestate.o tasks.o clients.o grid.o $(LDFLAGS)

server.o: action.o gamestate.o tasks.o clients.o grid.o
	$(CC) -o server.o -c server.c $(CFLAGS) $(LDFLAGS)

action.o: clients.o action.c action.h
	$(CC) -o action.o -c action.c $(CFLAGS) $(LDFLAGS)

gamestate.o: grid.o gamestate.c gamestate.h
	$(CC) -o gamestate.o -c gamestate.c $(CFLAGS) $(LDFLAGS)

clients.o: clients.c clients.h
	$(CC) -o clients.o -c clients.c $(CFLAGS) $(LDFLAGS)

tasks.o: tasks.c tasks.h
	$(CC) -o tasks.o -c tasks.c $(CFLAGS) $(LDFLAGS)

grid.o: grid.c grid.h
	$(CC) -o grid.o -c grid.c $(CFLAGS) $(LDFLAGS)


clean:
	rm -rf *.o

cleanall: clean
	rm -rf $(EXEC)




