#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "client.h"


/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif


client_t * clients = NULL;
client_t * last_client = NULL;

int nbClients = 0;
int nbClientsConnecte = 0;

/******************************************
*                                         *
*  Ajoute un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse ajouter un client à la   *
*  fois, i.e. que quelqu'un ne soit pas   *
*  présent deux fois dans la liste.       *
*                                         *
*******************************************/
/*
3 cas possibles :
1) Le client est déjà connecté (son nom, OU sa socket est déjà en cours d'utilisation), dans ce cas on ne fait rien
2) Le client n'est pas connecté, mais l'a été AU COURS DE LA SESSION COURANTE, on met simplement son tag isConnected à vrai.
3) Le client n'est pas connecté et nest pas dans le cas (2), on crée un client qu'on met dans la liste
*/

void addClient(int socket, char *name, pthread_mutex_t* p_mutex) {
    client_t * client = NULL;
    if(pthread_mutex_lock(p_mutex) < 0) {
    	perror("(Server:client.c:addClient) : on addClient, cannot lock the first p_mutex\n");
    }
    if((client=findClient(name))!=NULL) {
    /*Client déjà connecté*/
        if(client->isConnected==0) {
            printf("(Server:client.c:addClient) : Client %s deja connecte !\n", name);
            if(pthread_mutex_unlock(p_mutex) < 0) {
    			perror("(Server:client.c:addClient) : on addClient, cannot unlock the p_mutex when an already connected client asks for a new connection\n");
    		}
    	}
    /*Précédemment connecté, reprise de partie*/
        else {
            client->isConnected=0;
            client->socket = socket;
            printf("(Server:client.c:addClient) : Client %s se reconnecte !\n", name);
            if(pthread_mutex_lock(p_mutex) < 0) {
    			perror("(Server:client.c:addClient) : on addClient, cannot unlock the p_mutex when the client has already been connected before\n");
    		}
            nbClientsConnecte++;
        }
        return;
    }
    /*nouveau client*/
    client = (client_t*)malloc(sizeof(client_t));
    if (!client) {
		fprintf(stderr, "(Server:client.c:addClient) : out of memory.\n");
		exit(1);
    }
    client->socket = socket;
    client->name = name;
    client->isConnected = 0;
    client->score = 0;
    client->nbCoups=0;
    client->next = NULL;

    
    /* M.a.j. de la liste*/
    if (nbClients == 0) {
        clients = client;
        last_client = client;
    }
    else {
        last_client->next = client;
        last_client = client;
    }

    nbClientsConnecte++;
    nbClients++;

    printf("(Server:client.c:addClient) : added client with socket '%d'\n", client->socket);
    fflush(stdout);

    printClientsState(&client_mutex);
    if(pthread_mutex_unlock(p_mutex) < 0) {
    	perror("(Server:client.c:addClient) : cannot unlock the final p_mutex, in the end of the instanciation of the new client\n");
    }
    
}


/******************************************
*                                         *
*  déconnecte un client dans la liste des *
*  clients. Opération protégée par un     *
*  mutex.                                 *
*                                         *
*******************************************/

void disconnectClient(char* name, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0) { perror("(Server:client.c:printClientsState) : cannot lock the first p_mutex\n"); }
    client_t *client = findClient(name);
    if(client != NULL) {
        client->isConnected = 1;
        nbClientsConnecte--;
    }
    if(pthread_mutex_lock(p_mutex) < 0) { perror("(Server:client.c:printClientsState) : cannot lock the first p_mutex\n"); }
}

/******************************************
*                                         *
*  Enlève un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex.                                 *
*                                         *
*******************************************/

void rmClient(int socket, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0) { perror("(Server:client.c:printClientsState) : cannot lock the first p_mutex\n"); }
//TODO : Retirer un client de la liste.
//TODO : ce n'est pas ici qu'on envoie le message et qu'on clore la socket !!
    printClientsState(&client_mutex);
    if(pthread_mutex_lock(p_mutex) < 0) { perror("(Server:client.c:printClientsState) : cannot lock the first p_mutex\n"); }
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
client_t *findClient(char * name) {
    client_t * client = clients;
    while(client != NULL) {
        if(strcicmp(name, client->name)==0) {
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

