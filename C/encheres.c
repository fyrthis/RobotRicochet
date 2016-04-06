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
*  Ajoute une enchere dans la liste des   *
*  encheres. Opération protégée par un    *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse ajouter un enchere à la  *
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

int rmEnchere(char *username) {
    enchere_t * enchere = encheres;
    enchere_t * previous_enchere = NULL;
    //On cherche si le joueur a une enchère. Si oui, enchere = cette enchere, sinon enchere = NULL
    while(enchere != NULL && strcmp(username, enchere->name)!=0) {
        previous_enchere = enchere;
        enchere = enchere->next;
    }

    if(enchere!=NULL) { //Joueur a une enchère
        if(previous_enchere==NULL) { // maj tête de liste
            encheres = enchere->next;
        }
        if(previous_enchere != NULL) {
            previous_enchere->next = enchere->next;
        }
        nbEncheres--;
        free(enchere);
    }
    return 0;
}

int checkAndAddEnchere(int socket, char *username, int betSolution, pthread_mutex_t* p_mutex) {
    pthread_mutex_lock(p_mutex);

    if(betSolution <= 0){
        pthread_mutex_unlock(p_mutex);
        fprintf(stderr, "(Server:enchere.c:addEnchere) : on checkEnchere, the solution is negative...\n");
        return -1;
    }


    enchere_t * enchere = encheres;
    enchere_t * previous_enchere = NULL;
    //On cherche si le joueur a une enchère. Si oui, enchere = cette enchere, sinon enchere = NULL
    while(enchere != NULL && strcmp(username, enchere->name)!=0) {
        if(enchere->nbCoups==betSolution) { //Si quelqu'un propose déjà ce nombre de coup, on refuse.
            pthread_mutex_unlock(p_mutex);
            return -1;
        }
        previous_enchere = enchere;
        enchere = enchere->next;
    }

    if(enchere!=NULL) { //Joueur a déjà une enchère
        if(enchere->nbCoups <= betSolution){ //Sa nouvelle enchère n'est pas meilleure
            pthread_mutex_unlock(p_mutex);
            return -1;
        }

        if(enchere->nbCoups > betSolution ) { // Sa nouvelle enchère est meilleure
            if(previous_enchere==NULL) { // maj tête de liste
                encheres = enchere->next;
            }
            if(previous_enchere != NULL) {
                previous_enchere->next = enchere->next;
            }
            nbEncheres--;
            free(enchere);
        }
    }
    //A partir d'ici :
    // Soit le joueur n'avait pas d'enchère
    // Soit le joueur avait une enchère, qui vient d'être enlevée.
    // =>>> Reste à insérer la nouvelle enchère à la bonne place.
    enchere_t * new_enchere = (enchere_t*)malloc(sizeof(enchere_t));
    if (!new_enchere) {
        pthread_mutex_unlock(p_mutex); 
        fprintf(stderr, "(Server:enchere.c:addEnchere) : out of memory.\n");
        exit(1);
    }
    new_enchere->socket = socket;
    new_enchere->name = username;
    new_enchere->nbCoups = betSolution;
    new_enchere->next = NULL;
    nbEncheres++;

    enchere = encheres;
    previous_enchere = NULL;
    while(enchere != NULL && new_enchere->nbCoups > enchere->nbCoups) {
        previous_enchere = enchere;
        enchere = enchere->next;
    }

    //On doit insérer juste avant enchere.
    if(enchere==NULL) {
        if(previous_enchere==NULL) { //Insertion tête/queue de liste (seul élément)
            encheres=new_enchere;
            pthread_mutex_unlock(p_mutex);
            return 0;
        } else { //Insertion queue de liste
            previous_enchere->next = new_enchere;
            pthread_mutex_unlock(p_mutex);
            return 0;
        }
    } else {
        if(previous_enchere==NULL) { //Insertion tête de liste
            new_enchere->next = encheres;
            encheres = new_enchere;
            pthread_mutex_unlock(p_mutex);
            return 0;
        } else { //Insertion en plein milieu
            previous_enchere->next = new_enchere;
            new_enchere->next = enchere;
            pthread_mutex_unlock(p_mutex);
            return 0;
        }
    }
    pthread_mutex_unlock(p_mutex);
    return -1; //Should be unreachable
}