#ifndef GAME_STATE_H
#define GAME_STATE_H

#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include "client.h"
#include "grid.h"


client_t *activePlayer;
char *enigma;
char *bilan;
extern int nbTours;
//char *activePlayer;
int setEnigma();
int setBilanCurrentSession();

#endif