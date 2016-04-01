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

#define __USE_GNU 
#include <pthread.h>
#include <unistd.h>

/* CONSTANTS */
#define NB_MAX_THREADS 8
#define NB_MAX_CLIENTS 50

#define REFLEXION 0
#define ENCHERE 1
#define RESOLUTION 2

#include <signal.h>
#include "game_state.h"
#include "task.h"
#include "action.h"
#include "grid.h"
#include "client.h"
#include "game_state.h"
#include "utils.h"
#include "encheres.h"

/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t ticTac_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t ticTac_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t ticTac_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t ticTac_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif

extern pthread_cond_t  cond_got_task;//   = PTHREAD_COND_INITIALIZER;



int phase = REFLEXION;

// Variable qui permet de savoir si le client qui se connecte
// est le premier ou pas
int firstLaunch = 0;


void handle_request(task_t * task, int thread_id);
void * handle_tasks_loop(void* data);
void shutdown_server(int sig);
void * session_loop(void * nbToursSession);


#endif