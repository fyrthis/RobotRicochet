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

int alreadyExist(int betSolution, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) {
         perror("(Server:encheres.c:printEncheresState) : cannot lock the first p_mutex\n");
    }
    enchere_t * enchere = encheres;
    while(enchere != NULL) {
        if(betSolution == enchere->nbCoups){
            if(pthread_mutex_unlock(p_mutex) != 0) {
                perror("(Server:encheres.c:printEncheresState) : cannot unlock the final p_mutex\n");
            }
            fprintf(stderr, "(Server:enchere.c:alreadyExist) : votre enchère a déjà été proposée par %s, %d... et vous avez proposé : %d\n", enchere->name, enchere->nbCoups, betSolution);
            return 0;
        }
        enchere = enchere->next;
    }
    free(enchere);
    if(pthread_mutex_unlock(p_mutex) != 0) {
        perror("(Server:encheres.c:printEncheresState) : cannot unlock the final p_mutex\n");
    }
    return -1;
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

int checkEnchere(int socket, char *username, int betSolution, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) {
        perror("(Server:enchere.c:addEnchere) : on checkEnchere, cannot lock the first p_mutex\n");
    }

    if(betSolution <= 0){
        if(pthread_mutex_unlock(p_mutex) != 0) {
            perror("(Server:enchere.c:addEnchere) : on checkEnchere, cannot unlock the first p_mutex\n");
        }
        fprintf(stderr, "(Server:enchere.c:addEnchere) : on checkEnchere, the solution is negative...\n");
        return -1;
    }
    fprintf(stderr, "\t======> DEBUG START\n");
    enchere_t * enchere = encheres;
    enchere_t * previous_enchere = NULL;
    while(enchere != NULL && strcmp(username, enchere->name)!=0) {
        previous_enchere = enchere;
        enchere = enchere->next;
    }
    fprintf(stderr, "\t======> debug 3\n");
    // si la nouvelle solution n'améliore pas celle de son enchère précédente, alors il ne faut pas ajouter sa nouvelle solution
    if(enchere != NULL && enchere->nbCoups <= betSolution){
        fprintf(stderr, "\t======> debug 3.1\n");
        if(pthread_mutex_unlock(p_mutex) != 0) {
            perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
        }
        return -1;
    }
    else {
        // Si le joueur essaye d'enchérir une valeur déjà existante, alors on le lui interdit
        if(alreadyExist(betSolution, p_mutex) == 0){
            if(pthread_mutex_unlock(p_mutex) != 0) {
                perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
            }
            return -1;
        }

        //  si enchere != NULL && enchere->nbCoup > betSolution : il faut supprimer l'ancienne enchere qui devient obsolète
        if(enchere != NULL && enchere->nbCoups > betSolution ){
            previous_enchere->next = enchere->next;
            free(enchere);
        }
        
        fprintf(stderr, "\t======> debug 3.2\n");
        // A partir de cet endroit, on est sûr que la liste d'enchère ne contient pas d'enchère du même joueur:
        //      - soit on l'a supprimée car elle n'améliore pas le score;
        //      - soit on l'a gardée car la nouvelle solution n'est pas meilleure que celle de l'enchère,
        //   auquel cas on est déjà sorti de la fonction
        // donc dans tous les cas, il ne nous reste plus qu'à ajouter la nouvelle enchère à la liste
        enchere = encheres;
        previous_enchere = NULL;

        /*nouvelle enchère*/
        enchere_t * new_enchere = (enchere_t*)malloc(sizeof(enchere_t));
        if (!new_enchere) {
            if(pthread_mutex_unlock(p_mutex) != 0) {
                perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
            }  
            fprintf(stderr, "(Server:enchere.c:addEnchere) : out of memory.\n");
            exit(1);
        }
        new_enchere->socket = socket;
        new_enchere->name = username;
        new_enchere->nbCoups = betSolution;
        new_enchere->next = NULL;
        nbEncheres++;

        fprintf(stderr, "\t======> debug 6\n");
        while(enchere != NULL && enchere->nbCoups < new_enchere->nbCoups) {
            previous_enchere = enchere;
            enchere = enchere->next;
        }
        
        fprintf(stderr, "\t======> debug 7\n");
        if(previous_enchere == NULL){
            addEnchere(socket, username, betSolution, p_mutex);
            fprintf(stderr, "\t======> debug 7.1\n");
        }
        else {
            // Si il y a déjà une enchère avec le nombre de coups, on accepte pas la solution
            fprintf(stderr, "\t======> debug 7.2\n");
            if(previous_enchere->nbCoups == new_enchere->nbCoups){
                fprintf(stderr, "\t======> debug 7.2.1\n");
                if(pthread_mutex_unlock(p_mutex) != 0) {
                    perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
                }
                return -1;
            }
            else {
                fprintf(stderr, "\t======> debug 7.2.2\n");
                previous_enchere->next = new_enchere;
                new_enchere->next = enchere;    
            }
        }
    }

    if(pthread_mutex_unlock(p_mutex) != 0) {
        perror("(Server:enchere.c:addEnchere) : cannot unlock the final p_mutex, in the end of the instanciation of the new enchere\n");
    }
    fprintf(stderr, "\t======> debug end\n");
    return 0;
}