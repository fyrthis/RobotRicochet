#ifndef UTILS_H
#define UTILS_H


#include <stdio.h>
#include <stdlib.h>

#include <string.h>


//Fonction de comparaison de strings, insensitifs à la casse
int strcicmp(char const *a, char const *b);
char * append_strings(const char * old, const char * new);
char* getCharFromCase(int grid, int i, int j);

#endif