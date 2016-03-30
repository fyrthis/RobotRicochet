#ifndef ACTION_H
#define ACTION_H

#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <math.h>
#include <unistd.h>

#include "client.h"

#include "game_state.h"
#include "client.h" //Pour envoyer les messages à tout le monde.
 


int sendMessageAll(char *msg, pthread_mutex_t* p_mutex);
int sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex);

/***************************
*  PHASE D'INITIALISATION  *
****************************/
int send_bienvenue(char *username, int socket);
int send_connexion(char *username, int socket);
int send_deconnexion(char *username, int socket);


/************************
*   PHASE DE REFLEXION  *
*************************/

// Grid Part
int sendGrid(char *gridStr, int socket);
// Enigma + Bilan Parts
int sendEnigmaBilan(char *enigma, char *bilan);
int tuAsTrouve(int socket);
int ilATrouve(char *username, int solution, int socket);
int finReflexion();


/********************
*  PHASE D'ENCHERE  *
*********************/
int send_validation(int socket);
int send_echec(char *username, int socket);
int send_nouvelleEnchere(char *username, int nbCoups, int socket);
int send_finEnchere(char *username, int nbCoups, int socket);


/************************
*  PHASE DE RESOLUTION  *
*************************/

int solutionActive(char *username, char *deplacement, int socket);
int send_bonneSolution();
int send_mauvaiseSolution(char *username);
int send_finReso();
int send_tropLong(char *username);


/*********
*  CHAT  *
**********/

int envoyerMessageAuxAutres(char *user,  char *message, int socket);

#endif
