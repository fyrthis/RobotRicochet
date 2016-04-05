#ifndef CLIENT_H
#define CLIENT_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

#include "utils.h"
#include "encheres.h"

extern pthread_mutex_t client_mutex;

typedef struct client {
    int socket;
    int isConnected; //0 if true
    char *name;
    int score;
    int nbCoups;
    int points;
    struct client * next;
}client_t;

extern client_t * clients;
extern client_t * last_client;

extern int nbClients;
extern int nbClientsConnecte;

extern pthread_cond_t  cond_at_least_2_players;


void addClient(int socket, char *name, pthread_mutex_t* p_mutex);
void rmClient(int socket, pthread_mutex_t* p_mutex);
void disconnectClient(char *username, pthread_mutex_t* p_mutex);
client_t *findClient(char * name);
void updateBilan(enchere_t *best_enchere);
void resetNbCoups();
void printClientsState(pthread_mutex_t* p_mutex);
int checkIdPlayer(client_t *client, int socket, pthread_mutex_t* p_mutex);

#endif