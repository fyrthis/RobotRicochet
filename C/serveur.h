#ifndef SERVEUR_H
#define SERVEUR_H

#include <stdio.h>       /* standard I/O routines                     */
#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */

#include <netdb.h>
#include <netinet/in.h>
#include <string.h>

#include <sys/time.h>
#include <sys/ioctl.h>

#include <fcntl.h>
#include <errno.h>

#include <time.h>
#include <math.h>

#include "grid.h"
#include "client.h"
#include "task.h"

#include <ctype.h>
#include <unistd.h>

/* CONSTANTS */
#define NB_MAX_THREADS 8
#define NB_MAX_CLIENTS 50


int x_r = -1;
int y_r = -1;
int x_b = -1;
int y_b = -1;
int x_j = -1;
int y_j = -1;
int x_v = -1;
int y_v = -1;
int x_cible = -1;
int y_cible = -1;


char *gridStr;

char *enigma;
char *bilan;

int phase = 0;
int nbTour = 1;
int currentSolution = -1;
char *activePlayer;

// Variable qui permet de savoir si le client qui se connecte
// est le premier ou pas
int firstLaunch = 0;

char lettreCible;

void handle_request(task_t * task, int thread_id);
void * handle_tasks_loop(void* data);
int setEnigma();
int setBilanCurrentSession();
#endif