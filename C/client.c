#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include "client.h"



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
    	perror("Error : on addClient, cannot lock the first p_mutex\n");
    }
    if((client=findClient(socket, name))!=NULL) {
    /*Client déjà connecté*/
        if(client->isConnected==0) {
            printf("(addClient)Client %s deja connecte !\n", name);
            if(pthread_mutex_unlock(p_mutex) < 0) {
    			perror("Error : on addClient, cannot unlock the p_mutex when an already connected client asks for a new connection\n");
    		}
    	}
    /*Précédemment connecté, reprise de partie*/
        else {
            client->isConnected=0;
            client->socket = socket;
            printf("(addClient)Client %s se reconnecte !\n", name);
            if(pthread_mutex_lock(p_mutex) < 0) {
    			perror("Error : on addClient, cannot unlock the p_mutex when the client has already been connected before\n");
    		}
            nbClientsConnecte++;
        }
        return;
    }
    /*nouveau client*/
    client = (client_t*)malloc(sizeof(client_t));
    if (!client) {
		fprintf(stderr, "(addClient) out of memory.\n");
		exit(1);
    }
    client->socket = socket;
    client->name = name;
    client->isConnected = 0;
    client->score = 0;
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

    nbClients++;

    printf("add_request: added client with socket '%d'\n", client->socket);
    fflush(stdout);

    printClientsState(&client_mutex);
    if(pthread_mutex_unlock(p_mutex) < 0) {
    	perror("Error : on addClient, cannot unlock the final p_mutex, in the end of the instanciation of the new client\n");
    }
    
}


/******************************************
*                                         *
*  Enlève un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex.                                 *
*                                         *
*******************************************/

void rmClient(int socket, pthread_mutex_t* p_mutex) {
//TODO : Retirer un client de la liste.
//TODO : ce n'est pas ici qu'on envoie le message et qu'on clore la socket !!
    printClientsState(&client_mutex);
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
    	perror("Error : on printClientState, cannot lock the first p_mutex\n");
    }
    
    printf("Etat de la liste des %d clients : \n", nbClients);
    if(clients==NULL) {
        printf("Aucun client.\n");
    } else {
        int i = 1;
        client_t * client = clients;
        while(client != NULL) {
            printf("client %d : [name:%s ; socket:%d ; connected:", i, client->name, client->socket);
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
    	perror("Error : on printClientState, cannot unlock the final p_mutex\n");
    }
    
}

