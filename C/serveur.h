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

#include <ctype.h>
#include <unistd.h>

/* CONSTANTS */
#define NB_MAX_THREADS 8
#define NB_MAX_CLIENTS 50

#include "game_state.h"
#include "task.h"
#include "action.h"
#include "grid.h"
#include "client.h"


char *enigma;
char *bilan;

int phase = 0;
int nbTour = 1;
char *activePlayer;

// Variable qui permet de savoir si le client qui se connecte
// est le premier ou pas
int firstLaunch = 0;


void handle_request(task_t * task, int thread_id);
void * handle_tasks_loop(void* data);
int setEnigma();
int setBilanCurrentSession();
#endif