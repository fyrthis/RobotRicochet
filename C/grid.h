#ifndef GRID_H
#define GRID_H

#include <stdio.h>       /* standard I/O routines                     */
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */


int size_x = 0, size_y = 0;
int **grid;
char *gridStr;

int readGridFromFile(char *filename);

#endif