#ifndef GRID_H
#define GRID_H

#include <stdio.h>       /* standard I/O routines                     */
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */
#include "utils.h"		 /* getCharFromCase()						  */

extern int size_x;// = 0;
extern int size_y;// = 0;
extern int **grid;
extern char *gridStr;

int readGridFromFile(char *filename);

#endif