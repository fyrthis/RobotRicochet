#ifndef TASK_H
#define TASK_H

#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif

pthread_cond_t  cond_got_task   = PTHREAD_COND_INITIALIZER;

typedef struct task {
    int socket;
    char *command;
    struct task * next;
}task_t;

task_t * tasks = NULL;
task_t * last_task = NULL;

int nbTasks = 0;


void addTask(int socket, char *command, pthread_mutex_t* p_mutex, pthread_cond_t*  p_cond_var);
task_t * getTask(pthread_mutex_t* p_mutex);
void printTasksState(pthread_mutex_t* p_mutex);

#endif