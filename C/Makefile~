
CC=gcc
CFLAGS=
LDFLAGS= -lpthread
EXEC=serveur

# Ensemble des exécutables à produire
all: $(EXEC)

serveur: serveur.o action.o gamestate.o task.o client.o grid.o 
	$(CC) -o serveur serveur.o action.o gamestate.o task.o client.o grid.o $(LDFLAGS)

serveur.o: action.o gamestate.o task.o client.o grid.o
	$(CC) -o serveur.o -c serveur.c $(CFLAGS) $(LDFLAGS)

action.o: client.o action.c action.h
	$(CC) -o action.o -c action.c $(CFLAGS) $(LDFLAGS)

gamestate.o: grid.o gamestate.c gamestate.h
	$(CC) -o gamestate.o -c gamestate.c $(CFLAGS) $(LDFLAGS)

client.o: client.c client.h
	$(CC) -o client.o -c client.c $(CFLAGS) $(LDFLAGS)

tasks.o: task.c task.h
	$(CC) -o task.o -c task.c $(CFLAGS) $(LDFLAGS)

grid.o: grid.c grid.h
	$(CC) -o grid.o -c grid.c $(CFLAGS) $(LDFLAGS)


clean:
	rm -rf *.o

cleanall: clean
	rm -rf $(EXEC)




