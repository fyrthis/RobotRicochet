#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "action.h"


int sendMessageAll(char *msg, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0){
    	perror("(Server:action.c:sendMessageAll) : Error on sendMessageAll, cannot lock the p_mutex\n");
    }
    
    if(clients==NULL) {
        printf("(Server:action.c:sendMessageAll) : Aucun client. Should never happened\n");
    //} else if(clients) { TODO : Si qu'un client
    //    printf("(sendMessageAll)Un seul client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0) {
                if(write(client->socket,msg,(strlen(msg)*sizeof(char))) < 0){
                    perror("(Server:action.c:sendMessageAll) : Erreur in tuAsTrouve, cannot write on socket\n");
                }
                else {
                    fprintf(stderr, "(Server:action.c:sendMessageAll) : Message send to all : %s\n", msg);
                }
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0){
    	perror("(Server:action.c:sendMessageAll) : Error on sendMessageAll, cannot unlock the p_mutex\n");
    }

    return 0;
}

int sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex) { //Except client with this name
    if(pthread_mutex_lock(p_mutex) < 0){
    	perror("(Server:action.c:sendMessageAllExceptOne) : Error on sendMessageAllExceptOne, cannot lock the p_mutex\n");
    }
    fprintf(stderr, "\ttest : %s\n", msg);
    
    if(clients==NULL) {
        printf("(Server:action.c:sendMessageAllExceptOne) : Aucun client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0 && strcmp(client->name, name)!=0) {
                if(write(client->socket,msg,(strlen(msg)*sizeof(char))) < 0){
                    perror("(Server:action.c:sendMessageAllExceptOne) : Erreur in tuAsTrouve, cannot write on socket\n");
                }
                else {
                    fprintf(stderr, "(Server:action.c:sendMessageAllExceptOne) : Message send to %s : %s\n", client->name, msg);
                }
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0){
    	perror("(Server:action.c:sendMessageAllExceptOne) : cannot unlock the p_mutex\n");
    }

    return 0;
}




/***************************
*  PHASE D'INITIALISATION  *
****************************/

/***************************
*         CONNEXION        *
*  		 DECONNEXION  	   *
****************************/

// S -> C : BIENVENUE/user
int send_bienvenue(char *username, int socket) {
	//S -> C : BIENVENUE/user/
    char *msg = (char*)calloc(13+strlen(username), sizeof(char));
    sprintf(msg, "BIENVENUE/%s/\n", username);
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
        perror("(Server:action.c:bienvenue) :  cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:bienvenue) : Message send to %s : %s\n", username, msg);
    }

    return 0;
}

// S -> C : CONNECTE/user/
int send_connexion(char *username, int socket) {
    char *msg = (char*)calloc(12+strlen(username), sizeof(char));
    sprintf(msg, "CONNECTE/%s/\n", username);
    printf("%s", msg);
    sendMessageAllExceptOne(msg, username, &client_mutex);

    return 0;
}

// S -> C : DECONNEXION/user/
int send_deconnexion(char *username, int socket) {
	char *msg = (char*)calloc(15+strlen(username), sizeof(char));
    sprintf(msg, "DECONNEXION/%s/\n", username);
    sendMessageAllExceptOne(msg, username, &client_mutex);
    close(socket);

    return 0;
}


/*************************
*   DEBUT D'UNE SESSION  *
**************************/

/****************
*   GRID PART   *
*****************/

// S -> C : SESSION/plateau/
int sendGrid(char *gridStr, int socket) {
    fprintf(stderr, "(Server:action.c:sendGrid) : Sending the Grid:\n");
   /*Here is the mistake : do not put a char** into a strcat !!*/
    char *msg = (char*)calloc(4096, sizeof(char));
    
    sprintf(msg, "SESSION/%s/\n", gridStr);
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
    	perror("(Server:action.c:sendGrid) : Erreur in sendGrid, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:sendGrid) : Grid send!\n");
    }

    return 0;
}

/************************
*   PHASE DE REFLEXION  *
*************************/

/******************
*   ENIGMA PART   *
*******************/

// S -> C : TOUR/enigme/bilan
int sendEnigmaBilan(char *enigma, char *bilan) {
    fprintf(stderr, "(Server:action.c:sendEnigmaBilan) : Sending the Enigma:\n");
    char *msg = (char*)calloc(strlen(enigma)+strlen(bilan)+9, sizeof(char));
    
    sprintf(msg, "TOUR/%s/%s/\n", enigma, bilan);

    printf("%s", msg);
    sendMessageAll(msg, &client_mutex);

    return 0;
}


// S -> C : TUASTROUVE/
int tuAsTrouve(int socket) {
	char *msgActivePlayer = (char*)calloc(13, sizeof(char));
    sprintf(msgActivePlayer, "TUASTROUVE/\n");
    fprintf(stderr, "%s\n", msgActivePlayer);
    if(write(socket,msgActivePlayer,(strlen(msgActivePlayer))*sizeof(char)) < 0){
    	perror("(Server:action.c:tuAsTrouve) : Erreur in tuAsTrouve, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:tuAsTrouve) : Message send to activePlayer : %s\n", msgActivePlayer);
    }
    return 0;
}

// S -> C : ILATROUVE/user/coups/
int ilATrouve(char *activePlayer, int solution, int socket) {
	// On indique aux autres players qu'un joueur a proposé une solution
    int currentSolutionLength = getIntLength(solution);
    char *msgOtherPlayers = (char*)calloc(15+strlen(activePlayer)+currentSolutionLength, sizeof(char));
    sprintf(msgOtherPlayers, "ILATROUVE/%s/%d/\n", activePlayer, solution);
   
    fprintf(stderr, "(Server:action.c:ilATrouve) : %s\n", msgOtherPlayers);
    
	sendMessageAllExceptOne(msgOtherPlayers, activePlayer, &client_mutex);
    return 0;
}

// S -> C : FINREFLEXION/
int finReflexion() {
    char *msg = (char*) calloc (15, sizeof(char));
    sprintf(msg, "FINREFLEXION/\n");
    sendMessageAll(msg, &client_mutex);
    return 0;
}

/********************
*  PHASE D'ENCHERE  *
*********************/

// S -> C : VALIDATION/
int send_validation(int socket) {
    char *msg = (char*)calloc(13, sizeof(char));
    sprintf(msg, "VALIDATION/\n");
    fprintf(stderr, "(Server:action.c:validation) : %s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("(Server:action.c:validation) : Erreur in validation(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:validation) : Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : ECHEC/user/
int send_echec(char *username, int socket) {
	char *msg = (char*)calloc(9 + strlen(username), sizeof(char));
    sprintf(msg, "ECHEC/%s/\n", username);
    fprintf(stderr, "(Server:action.c:echec) : %s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("(Server:action.c:echec) : Erreur in echec(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:echec) : Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : NOUVELLEENCHERE/user/coups/
int send_nouvelleEnchere(char *username, int nbCoups, int socket) {
    int nbCoupsLength = getIntLength(nbCoups);
	char *msg = (char*)calloc(20 + strlen(username) + nbCoupsLength, sizeof(char));
    sprintf(msg, "NOUVELLEENCHERE/%s/%d/\n", username, nbCoups);
    fprintf(stderr, "(Server:action.c:nouvelleEnchere) : %s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("(Server:action.c:nouvelleEnchere) : Erreur in nouvelleEnchere(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:nouvelleEnchere) : Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : FINENCHERE/user/coups/
int send_finEnchere(char *username, int nbCoups, int socket) {
    int nbCoupsLength = getIntLength(nbCoups);
    char *msg = (char*)calloc(15 + strlen(username) + nbCoupsLength, sizeof(char));
    sprintf(msg, "FINENCHERE/%s/%d/\n", username, nbCoups);
    fprintf(stderr, "(Server:action.c:finEnchere) : %s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("(Server:action.c:finEnchere) : Erreur in nouvelleEnchere(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "(Server:action.c:finEnchere) : Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

/************************
*  PHASE DE RESOLUTION  *
*************************/

// S -> C : SASOLUTION/user/deplacements/
int solutionActive(char *username, char *deplacements, int socket) {
	char *msg = (char*)calloc(15 + strlen(username) + strlen(deplacements), sizeof(char));
    sprintf(msg, "SASOLUTION/%s/%s/\n", username, deplacements);
    fprintf(stderr, "(Server:action.c:solutionActive) : %s\n", msg);
    sendMessageAllExceptOne(msg, username, &client_mutex);
    return 0;
}

// S -> C : BONNE/
int send_bonneSolution() {
	char *msg = (char*)calloc(8, sizeof(char));
    sprintf(msg, "BONNE/\n");
    fprintf(stderr, "(Server:action.c:bonneSolution) : %s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : MAUVAISE/user/
int send_mauvaiseSolution(char *username) {
	char *msg = (char*)calloc(12 + strlen(username), sizeof(char));
    sprintf(msg, "MAUVAISE/%s/\n", username);
    fprintf(stderr, "(Server:action.c:mauvaiseSolution) : %s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : FINRESO/
int send_finReso() {
	char *msg = (char*)calloc(10, sizeof(char));
    sprintf(msg, "FINRESO/\n");
    fprintf(stderr, "(Server:action.c:finResolution) : %s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : TROPLONG/user/
int send_tropLong(char *username) {
	char *msg = (char*)calloc(12 + strlen(username), sizeof(char));
    sprintf(msg, "TROPLONG/%s/\n", username);
    fprintf(stderr, "(Server:action.c:tropLong) : %s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : MESSAGE/user/message
int envoyerMessageAuxAutres(char *user,  char *message, int socket) {
    char *msg = (char*)calloc(strlen(user) + strlen(message) + 11 + 1, sizeof(char));
    sprintf(msg, "MESSAGE/%s/%s/\n", user, message);
    fprintf(stderr, "(Server:action.c:envoyerMessageAuxAutres) : %s\n", msg);
    sendMessageAllExceptOne(msg, user, &client_mutex);
    return 0;
}