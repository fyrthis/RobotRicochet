

#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "utils.h"


//Fonction de comparaison de strings, insensitifs à la casse
int strcicmp(char const *a, char const *b) {
    for (;; a++, b++) {
        int d = tolower(*a) - tolower(*b);
        if (d != 0 || !*a)
            return d;
    }
}

char * append_strings(const char * old, const char * new) {
    // find the size of the string to allocate
    const size_t old_len = strlen(old), new_len = strlen(new);
    const size_t out_len = old_len + new_len + 1;

    // allocate a pointer to the new string
    char *out = malloc(out_len);

    // concat both strings and return
    memcpy(out, old, old_len);
    memcpy(out + old_len, new, new_len + 1);

    return out;
}

int getIntLength(int i) {
    int length = 1;
    if(i >= 10)
        length = floor(log10(abs(i))) + 1;
    return length;
}

char* getCharFromCase(int grid, int i, int j) {
    char* chaine = calloc(sizeof(char), (2*8 + 20 + 1));
    switch(grid){
        case 0:
            //fprintf(stderr, "%s", chaine);
            break;
        case 1:
            sprintf(chaine, "(%d,%d,H)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 2:
            sprintf(chaine, "(%d,%d,D)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 3:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 4:
            sprintf(chaine, "(%d,%d,B)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 5:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 6:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 7:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 8:
            sprintf(chaine, "(%d,%d,G)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 9:
            sprintf(chaine, "(%d,%d,H)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 10:
            sprintf(chaine, "(%d,%d,D)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 11:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 12:
            sprintf(chaine, "(%d,%d,B)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 13:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 14:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 15:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        default:;
    }
    return chaine;
}


