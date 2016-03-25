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

// Coordonnées des robots    
extern int x_r;// = -1;
extern int y_r;// = -1;
extern int x_b;// = -1;
extern int y_b;// = -1;
extern int x_j;// = -1;
extern int y_j;// = -1;
extern int x_v;// = -1;
extern int y_v;// = -1;
extern int x_cible;// = -1;
extern int y_cible;// = -1;

extern char lettreCible;

int readGridFromFile(char *filename);
int isValideSolution(char *deplacements);

#endif