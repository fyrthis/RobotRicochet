#ifndef ENCHERES_H
#define ENCHERES_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

extern pthread_mutex_t enchere_mutex;

typedef struct enchere {
    int socket;
    char *name;
    int nbCoups;
    struct enchere * next;
}enchere_t;

extern enchere_t * encheres;

extern int nbEncheres;


enchere_t * getEnchere(pthread_mutex_t* p_mutex);
void rmEncheres(pthread_mutex_t* p_mutex);
int rmEnchere(char * username);
void printEncheresState(pthread_mutex_t* p_mutex);
int checkAndAddEnchere(int socket, char *username, int betSolution, pthread_mutex_t* p_mutex);

#endif