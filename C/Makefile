
CC=gcc
CFLAGS= -Wall
LDFLAGS= -lpthread -lm
EXEC=serveur

# Ensemble des exécutables à produire
all: $(EXEC)

serveur: serveur.o action.o game_state.o task.o client.o grid.o utils.o
	$(CC) -o serveur serveur.o action.o game_state.o task.o client.o grid.o utils.o $(LDFLAGS)

serveur.o: task.o client.o action.o game_state.o grid.o utils.o
	$(CC) -o serveur.o -c serveur.c $(CFLAGS) $(LDFLAGS)

action.o: client.o utils.o action.c action.h
	$(CC) -o action.o -c action.c $(CFLAGS) $(LDFLAGS)

game_state.o: grid.o utils.o game_state.c game_state.h
	$(CC) -o game_state.o -c game_state.c $(CFLAGS) $(LDFLAGS)

client.o: utils.o client.c client.h
	$(CC) -o client.o -c client.c $(CFLAGS) $(LDFLAGS)

tasks.o: utils.o task.c task.h
	$(CC) -o task.o -c task.c $(CFLAGS) $(LDFLAGS)

grid.o: utils.o grid.c grid.h
	$(CC) -o grid.o -c grid.c $(CFLAGS) $(LDFLAGS)

utils.o: utils.c utils.h
	$(CC) -o utils.o -c utils.c $(CFLAGS) $(LDFLAGS)


clean:
	rm -rf *.o

cleanall: clean
	rm -rf $(EXEC)




