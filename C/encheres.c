#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "encheres.h"


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

//Tête de liste
void addEnchere(int socket, char *name, int solution, pthread_mutex_t* p_mutex) {
    enchere_t * enchere = NULL;
    if(pthread_mutex_lock(p_mutex) != 0) {
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
    enchere->nbCoups = solution;
    enchere->next = NULL;

    
    /* M.a.j. de la liste : insertion au bon endroit, i.e. au début puisqu'on accepte si elle est inf.*/
    if (nbEncheres == 0) {
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
    
    if(pthread_mutex_unlock(p_mutex) != 0) {
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

enchere_t * getEnchere(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex)!= 0) {
        perror("(Server:encheres.c:printEncheresState) : cannot lock the first p_mutex\n");
    }
    enchere_t * enchere = encheres;
    encheres = encheres->next;
    nbEncheres--;


    printEncheresState(p_mutex);

    if(pthread_mutex_unlock(p_mutex) != 0) {
        perror("(Server:encheres.c:printEncheresState) : cannot unlock the final p_mutex\n");
    }

    return enchere;
    //printClientsState(&client_mutex);

}

void rmEncheres(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) {
        perror("(Server:encheres.c:printEncheresState) : cannot lock the first p_mutex\n");
    }
    enchere_t * enchere;
    while(encheres!=NULL) {
        enchere = encheres;
        encheres = encheres->next;
        free(enchere);
    }
    if(pthread_mutex_unlock(p_mutex) != 0) {
        perror("(Server:encheres.c:printEncheresState) : cannot unlock the final p_mutex\n");
    }
}



void printEncheresState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) {
    	perror("(Server:encheres.c:printEncheresState) : cannot lock the first p_mutex\n");
    }
    
    printf("(Server:encheres.c:printEncheresState) : Etat de la liste des %d encheres : \n", nbEncheres);
    if(encheres==NULL) {
        printf("(Server:encheres.c:printEncheresState) : Aucune enchere.\n");
    } else {
        int i = 1;
        enchere_t * enchere = encheres;
        while(enchere != NULL) {
            printf("(Server:encheres.c:printEncheresState) : enchere %d : [name:%s ; socket:%d ; nbCoups: %d]\n", i, enchere->name, enchere->socket, enchere->nbCoups);
            enchere = enchere->next;
            i++;
        }
    }
    if(pthread_mutex_unlock(p_mutex) != 0) {
    	perror("(Server:encheres.c:printEncheresState) : cannot unlock the final p_mutex\n");
    }
    
}

int checkEnchere(char *username, int betSolution, pthread_mutex_t* p_mutex) {
    if(encheres==NULL) {
        printf("Aucune enchere.\n");
    } else {
        int i = 0;
        enchere_t * enchere = encheres;
        enchere_t * last_enchere = NULL;
        while(enchere != NULL && strcmp(username, enchere->name)!=0) {
            last_enchere = enchere;
            enchere = enchere->next;
            i++;
        }
        //Ici, soit enchere est NULL, soit on a trouvé la meilleure enchère actuelle du joueur
        if(enchere==NULL) return -1;
        if(betSolution>=enchere->nbCoups) return -2;
        //Ici, on sait que notre enchère est correcte, on peut l'insérer
        return 0;
    }
    return -3;
}