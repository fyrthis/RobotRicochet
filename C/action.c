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
                if(write(client->socket,msg,strlen(msg)*sizeof(char)) < 0){
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
                if(write(client->socket,msg,strlen(msg)*sizeof(char)) < 0){
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

int bienvenue(char *username, int socket) {
	//S -> C : BIENVENUE/user/
    char *msg = (char*)calloc(sizeof(char), 13+strlen(username));
    strcpy(msg, "BIENVENUE/");
    strcat(msg, username);
    strcat(msg,"/\n");
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
        perror("Erreur in bienvenue(), cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to %s : %s\n", username, msg);
    }

    return 0;
}

int connexion(char *username, int socket) {
    //S -> C : CONNECTE/user/
    char *msg2 = (char*)calloc(sizeof(char), (12+strlen(username)));
    strcpy(msg2, "CONNECTE/");
    strcat(msg2, username);
    strcat(msg2,"/\n");
    printf("%s", msg2);
    sendMessageAllExceptOne(msg2, username, &client_mutex);

    return 0;
}

int deconnexion(char *username, int socket) {
	//S -> C : DECONNEXION/user/
    char *msg = (char*)calloc(sizeof(char), 15+strlen(username));
    strcpy(msg, "DECONNEXION/");
    strcat(msg, username);
    strcat(msg,"/\n");
    printf("%s", msg);
    sendMessageAllExceptOne(msg, username, &client_mutex);
    close(socket);
    fprintf(stderr, " handle Task SORT\n");
    return 0;
}


/************************
*   PHASE DE REFLEXION  *
*************************/

/****************
*   GRID PART   *
*****************/

int sendGrid(char *gridStr, int socket) {
    fprintf(stderr, "Sending the Grid:\n");
   /*Here is the mistake : do not put a char** into a strcat !!*/
    char *msg = (char*)calloc(sizeof(char), 4096);
    strcpy(msg, "SESSION/");
    strcat(msg, gridStr);
    strcat(msg,"/\n");
    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0){
    	perror("Erreur in sendGrid, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Grid send!\n");
    }

    return 0;
}

/******************
*   ENIGMA PART   *
*******************/

int sendEnigmaBilan(char *enigma, char *bilan, int socket) {
    fprintf(stderr, "Sending the Enigma:\n");
    char *msg = (char*)calloc(sizeof(char), strlen(enigma)+strlen(bilan)+9);
    strcpy(msg, "TOUR/");
    strcat(msg, enigma);
    strcat(msg,"/");
    strcat(msg, bilan);
    strcat(msg,"/\n");

    printf("%s", msg);
    if(write(socket,msg,(strlen(msg)+1)*sizeof(char)) < 0) {
    	perror("Erreur in sendEnigmaBilan, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Enigma + bilan send!\n");
    }

    return 0;
}

int tuAsTrouve(int socket) {
	char *msgActivePlayer = (char*)calloc(sizeof(char), 13);
    strcpy(msgActivePlayer, "TUASTROUVE/\n");
    fprintf(stderr, "%s\n", msgActivePlayer);
    if(write(socket,msgActivePlayer,(strlen(msgActivePlayer)+1)*sizeof(char)) < 0){
    	perror("Erreur in tuAsTrouve, cannot write on socket\n");
    }
    else {
        fprintf(stderr, "Message send to activePlayer : %s\n", msgActivePlayer);
    }
    return 0;
}

int ilATrouve(char *activePlayer, int solution, int socket) {
	// On indique aux autres players qu'un joueur a proposé une solution
    int currentSolutionLength = 1;
    if(solution >= 10)
        currentSolutionLength = floor(log10(abs(currentSolutionLength))) + 1;

    char *msgOtherPlayers = (char*)calloc(sizeof(char), (14+strlen(activePlayer)+currentSolutionLength));
    sprintf(msgOtherPlayers, "ILATROUVE/%s/%d/\n", activePlayer, currentSolution);
   
    fprintf(stderr, "%s\n", msgOtherPlayers);
    
	sendMessageAllExceptOne(msgOtherPlayers, activePlayer, &client_mutex);
    return 0;
}

int finReflexion() {
    char *msg = (char*) calloc (sizeof(char), 15);
    sprintf(msg, "FINREFLEXION/\n");
    sendMessageAll(msg, &client_mutex);
    return 0;
}

/********************
*  PHASE D'ENCHERE  *
*********************/

int validation(int socket) {
	return 0;
}

int echec(char *username, int socket) {
	return 0;
}

int nouvelleEnchere(char *username, int nbCoups, int socket) {
	return 0;
}

int finEnchere(char *username, int nbCoups, int socket) {
    return 0;

}

/************************
*  PHASE DE RESOLUTION  *
*************************/

int solutionActive(char *username, char *deplacement, int socket) {
	return 0;
}

int bonneSolution(int socket) {
	return 0;
}

int mauvaiseSolution(char *username, int socket) {
	return 0;
}

int finResolution(int socket) {
	return 0;
}

int tropLong(char *username, int socket) {
	return 0;
}
