#include <stdio.h>
#include <stdlib.h>

#include <string.h>

typedef struct client {
    int socket;
    int isConnected; //0 if true
    char *name;
    int score;
    struct client * next;
}client_t;

client_t * clients = NULL;
client_t * last_client = NULL;

int nbClients = 0;
int nbClientsConnecte = 0;

void addClient(int socket, char *name, pthread_mutex_t* p_mutex);
void rmClient(int socket, pthread_mutex_t* p_mutex);
client_t *findClient(int socket, char * name);
void printClientsState(pthread_mutex_t* p_mutex);