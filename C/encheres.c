#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "enchere.h"


/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t enchere_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t enchere_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t enchere_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t enchere_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif


enchere_t * encheres = NULL;

int nbEncheres = 0;

/******************************************
*                                         *
*  Ajoute une enchere dans la liste des     *
*  encheres. Opération protégée par un     *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse ajouter un enchere à la   *
*  fois, i.e. que quelqu'un ne soit pas   *
*  présent deux fois dans la liste.       *
*                                         *
*******************************************/

void addEnchere(int socket, char *name, pthread_mutex_t* p_mutex) {
    enchere_t * enchere = NULL;
    if(pthread_mutex_lock(p_mutex) < 0) {
    	perror("(Server:enchere.c:addEnchere) : on addEnchere, cannot lock the first p_mutex\n");
    }

    /*nouvelle enchère*/
    enchere = (enchere_t*)malloc(sizeof(enchere_t));
    if (!enchere) {
		fprintf(stderr, "(Server:enchere.c:addEnchere) : out of memory.\n");
		exit(1);
    }
    enchere->socket = socket;
    enchere->name = name;
    enchere->nbCoups = 0;
    enchere->next = NULL;

    
    /* M.a.j. de la liste : insertion au bon endroit, i.e. au début puisqu'on accepte si elle est inf.*/
    if (nbEncheress == 0) {
        encheres = enchere;
    }
    else { //Ajout en tête de liste
        enchere->next = encheres;
        encheres = enchere;

    }

    nbEncheres++;

    printf("(Server:enchere.c:addEnchere) : added enchere with socket '%d'\n", enchere->socket);
    fflush(stdout);

    printEncheresState(&enchere_mutex);
    if(pthread_mutex_unlock(p_mutex) < 0) {
    	perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
    }
    
}


/******************************************
*                                         *
*  Enlève un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex.                                 *
*                                         *
*******************************************/

enchere_t * getEnchere(int socket, pthread_mutex_t* p_mutex) {
    enchere_t * enchere = encheres;
    encheres = encheres->next;
    nbEncheres--;
    return enchere;
    //printClientsState(&client_mutex);
}

/******************************************
*                                         *
*  Retourne le client dans la liste des   *
*  clients. Opération protégée par un     *
*  mutex afin  de s'assurer qu'une seule  *
*  entité accède aux clients à la fois,   *
*  renvoie NULL si non trouvé.            *
*                                         *
*******************************************/

//Mutex dans addClient
client_t *findClient(int socket, char * name) {
    client_t * client = clients;
    while(client != NULL) {
        if(strcicmp(name, client->name)==0/* || socket==client->socket*/) {
            return client;
        }
        client = client->next;
    }
    return NULL;
}


void printClientsState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0) {
    	perror("(Server:client.c:printClientsState) : cannot lock the first p_mutex\n");
    }
    
    printf("(Server:client.c:printClientsState) : Etat de la liste des %d clients : \n", nbClients);
    if(clients==NULL) {
        printf("(Server:client.c:printClientsState) : Aucun client.\n");
    } else {
        int i = 1;
        client_t * client = clients;
        while(client != NULL) {
            printf("(Server:client.c:printClientsState) : client %d : [name:%s ; socket:%d ; connected:", i, client->name, client->socket);
            if(client->isConnected==0) {
                puts("true].\n");
            } else {
                puts("false].\n");
            }
            client = client->next;
            i++;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0) {
    	perror("(Server:client.c:printClientsState) : cannot unlock the final p_mutex\n");
    }
    
}

