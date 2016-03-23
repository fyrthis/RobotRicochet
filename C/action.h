#ifndef ACTION_H
#define ACTION_H

#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "client.h"
#include "game_state.h"



int sendMessageAll(char *msg, pthread_mutex_t* p_mutex);
int sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex);

/***************************
*  PHASE D'INITIALISATION  *
****************************/
int bienvenue(char *username, int socket);
int connexion(char *username, int socket);
int deconnexion(char *username, int socket);


/************************
*   PHASE DE REFLEXION  *
*************************/

// Grid Part
int sendGrid(char **gridStr, int socket);
// Enigma + Bilan Parts
int sendEnigmaBilan(char *enigma, char *bilan, int socket);
int tuAsTrouve(int socket);
int ilATrouve(char *username, int solution, int socket);
int finReflexion(int socket);


/********************
*  PHASE D'ENCHERE  *
*********************/
int validation(int socket);
int echec(char *username, int socket);
int nouvelleEnchere(char *username, int nbCoups, int socket);
int finEnchere(char *username, int nbCoups, int socket);


/************************
*  PHASE DE RESOLUTION  *
*************************/

int solutionActive(char *username, char *deplacement, int socket);
int bonneSolution(int socket);
int mauvaiseSolution(char *username, int socket);
int finResolution(int socket);
int tropLong(char *username, int socket);

#endif