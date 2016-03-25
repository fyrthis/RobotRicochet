#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "action.h"


int sendMessageAll(char *msg, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0){
    	perror("Error on sendMessageAll, cannot lock the p_mutex\n");
    }
    
    if(clients==NULL) {
        printf("(sendMessageAll)Aucun client. Should never happened\n");
    //} else if(clients) { TODO : Si qu'un client
    //    printf("(sendMessageAll)Un seul client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0) {
                if(write(client->socket,msg,(strlen(msg)*sizeof(char))) < 0){
                    perror("Erreur in tuAsTrouve, cannot write on socket\n");
                }
                else {
                    fprintf(stderr, "Message send to all : %s\n", msg);
                }
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0){
    	perror("Error on sendMessageAll, cannot unlock the p_mutex\n");
    }

    return 0;
}

int sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex) { //Except client with this name
    if(pthread_mutex_lock(p_mutex) < 0){
    	perror("Error on sendMessageAllExceptOne, cannot lock the p_mutex\n");
    }
    fprintf(stderr, "\ttest : %s\n", msg);
    
    if(clients==NULL) {
        printf("(sendMessageAll)Aucun client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0 && strcmp(client->name, name)!=0) {
                if(write(client->socket,msg,(strlen(msg)*sizeof(char))) < 0){
                    perror("Erreur in tuAsTrouve, cannot write on socket\n");
                }
                else {
                    fprintf(stderr, "Message send to %s : %s\n", client->name, msg);
                }
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0){
    	perror("Error on sendMessageAllExceptOne, cannot lock the p_mutex\n");
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
int bienvenue(char *username, int socket) {
	//S -> C : BIENVENUE/user/
    char *msg = (char*)calloc(13+strlen(username), sizeof(char));
    sprintf(msg, "BIENVENUE/%s/\n", username);
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
        perror("Erreur in bienvenue(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to %s : %s\n", username, msg);
    }

    return 0;
}

// S -> C : CONNECTE/user/
int connexion(char *username, int socket) {
    char *msg = (char*)calloc(12+strlen(username), sizeof(char));
    sprintf(msg, "CONNECTE/%s/\n", username);
    printf("%s", msg);
    sendMessageAllExceptOne(msg, username, &client_mutex);

    return 0;
}

// S -> C : DECONNEXION/user/
int deconnexion(char *username, int socket) {
	char *msg = (char*)calloc(15+strlen(username), sizeof(char));
    sprintf(msg, "DECONNEXION/%s/\n", username);
    sendMessageAllExceptOne(msg, username, &client_mutex);
    close(socket);
    fprintf(stderr, " handle Task SORT\n");
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
    fprintf(stderr, "Sending the Grid:\n");
   /*Here is the mistake : do not put a char** into a strcat !!*/
    char *msg = (char*)calloc(4096, sizeof(char));
    
    sprintf(msg, "SESSION/%s/\n", gridStr);
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
    	perror("Erreur in sendGrid, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Grid send!\n");
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
int sendEnigmaBilan(char *enigma, char *bilan, int socket) {
    fprintf(stderr, "Sending the Enigma:\n");
    char *msg = (char*)calloc(strlen(enigma)+strlen(bilan)+9, sizeof(char));
    
    sprintf(msg, "TOUR/%s/%s/\n", enigma, bilan);

    printf("%s", msg);
    if(write(socket,msg,(strlen(msg)*sizeof(char))) < 0) {
    	perror("Erreur in sendEnigmaBilan, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Enigma + bilan send!\n");
    }

    return 0;
}


// S -> C : TUASTROUVE/
int tuAsTrouve(int socket) {
	char *msgActivePlayer = (char*)calloc(13, sizeof(char));
    sprintf(msgActivePlayer, "TUASTROUVE/\n");
    fprintf(stderr, "%s\n", msgActivePlayer);
    if(write(socket,msgActivePlayer,(strlen(msgActivePlayer))*sizeof(char)) < 0){
    	perror("Erreur in tuAsTrouve, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to activePlayer : %s\n", msgActivePlayer);
    }
    return 0;
}

// S -> C : ILATROUVE/user/coups/
int ilATrouve(char *activePlayer, int solution, int socket) {
	// On indique aux autres players qu'un joueur a proposé une solution
    int currentSolutionLength = getIntLength(solution);
    char *msgOtherPlayers = (char*)calloc(15+strlen(activePlayer)+currentSolutionLength, sizeof(char));
    sprintf(msgOtherPlayers, "ILATROUVE/%s/%d/\n", activePlayer, currentSolution);
   
    fprintf(stderr, "%s\n", msgOtherPlayers);
    
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
int validation(int socket) {
    char *msg = (char*)calloc(13, sizeof(char));
    sprintf(msg, "VALIDATION/\n");
    fprintf(stderr, "%s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("Erreur in validation(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : ECHEC/user/
int echec(char *username, int socket) {
	char *msg = (char*)calloc(9 + strlen(username), sizeof(char));
    sprintf(msg, "ECHEC/%s/\n", username);
    fprintf(stderr, "%s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("Erreur in echec(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : NOUVELLEENCHERE/user/coups/
int nouvelleEnchere(char *username, int nbCoups, int socket) {
    int nbCoupsLength = getIntLength(nbCoups);
	char *msg = (char*)calloc(20 + strlen(username) + nbCoupsLength, sizeof(char));
    sprintf(msg, "NOUVELLEENCHERE/%s/%d/\n", username, nbCoups);
    fprintf(stderr, "%s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("Erreur in nouvelleEnchere(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to the bet sender : %s\n", msg);
    }
    return 0;
}

// S -> C : FINENCHERE/user/coups/
int finEnchere(char *username, int nbCoups, int socket) {
    int nbCoupsLength = getIntLength(nbCoups);
    char *msg = (char*)calloc(15 + strlen(username) + nbCoupsLength, sizeof(char));
    sprintf(msg, "FINENCHERE/%s/%d/\n", username, nbCoups);
    fprintf(stderr, "%s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("Erreur in nouvelleEnchere(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to the bet sender : %s\n", msg);
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
    fprintf(stderr, "%s\n", msg);
    sendMessageAllExceptOne(msg, username, &client_mutex);
    return 0;
}

// S -> C : BONNE/
int bonneSolution(int socket) {
	char *msg = (char*)calloc(8, sizeof(char));
    sprintf(msg, "BONNE/\n");
    fprintf(stderr, "%s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : MAUVAISE/user/
int mauvaiseSolution(char *username, int socket) {
	char *msg = (char*)calloc(12 + strlen(username), sizeof(char));
    sprintf(msg, "MAUVAISE/%s/\n", username);
    fprintf(stderr, "%s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : FINRESO/
int finResolution(int socket) {
	char *msg = (char*)calloc(10, sizeof(char));
    sprintf(msg, "FINRESO/\n");
    fprintf(stderr, "%s\n", msg);
    sendMessageAll(msg, &client_mutex);
    return 0;
}

// S -> C : TROPLONG/user/
int tropLong(char *username, int socket) {
	char *msg = (char*)calloc(12 + strlen(username), sizeof(char));
    sprintf(msg, "TROPLONG/%s/\n", username);
    fprintf(stderr, "%s\n", msg);
    if(write(socket,msg,(strlen(msg))*sizeof(char)) < 0){
        perror("Erreur in tropLong(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to the bet sender : %s\n", msg);
    }
    return 0;
}
