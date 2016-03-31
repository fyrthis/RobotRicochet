#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "game_state.h"

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

char lettreCible = ' ';

int nbTours = 10;

/*********************************************
*                                            *
*  Crée une nouvelle enigme en initialisant  *
*  les coordoonées du robot aléatoirement    *
*                                            *
*********************************************/
//serveur.c
int setEnigma(){
    // nbRobots*nbCoordonnées*tailleCoordonnée + nbLettres + nbVirgule + parenthèses + lettreCible
    
    
    //if(firstLaunch == 0){
        // Generation aleatoire des positions des robots
        
        
        // Cible
        int center1_x = (size_x-1) / 2;
        int center1_y = (size_y-1) / 2;
        int center2_x = (size_x-1) / 2 + 1;
        int center2_y = (size_x-1) / 2;
        int center3_x = (size_x-1) / 2;
        int center3_y = (size_y-1) / 2 + 1;
        int center4_x = (size_x-1) / 2 + 1;
        int center4_y = (size_y-1) / 2 + 1;


        
        do { 
            // Rouge
            x_r = rand() % size_x;
            y_r = rand() % size_y;
            // Bleu
            x_b = rand() % size_x;
            y_b = rand() % size_y;
            // Jaune
            x_j = rand() % size_x;
            y_j = rand() % size_y;
            // Vert
            x_v = rand() % size_x;
            y_v = rand() % size_y;
        } while((x_r == x_b && y_r == y_b) || (x_r == x_j && y_r == y_j) || (x_r == x_v && y_r == y_v)
             || (x_b == x_j && y_b == y_j) || (x_b == x_v && y_b == y_v) || (x_j == x_v && y_j == y_v)
             || (x_r == center1_x && y_r == center1_y) || (x_r == center2_x && y_r == center2_y)
             || (x_r == center3_x && y_r == center3_y) || (x_r == center4_x && y_r == center4_y)
             || (x_b == center1_x && y_b == center1_y) || (x_b == center2_x && y_b == center2_y)
             || (x_b == center3_x && y_b == center3_y) || (x_b == center4_x && y_b == center4_y)
             || (x_j == center1_x && y_j == center1_y) || (x_j == center2_x && y_j == center2_y)
             || (x_j == center3_x && y_j == center3_y) || (x_j == center4_x && y_j == center4_y)
             || (x_v == center1_x && y_v == center1_y) || (x_v == center2_x && y_v == center2_y)
             || (x_v == center3_x && y_v == center3_y) || (x_v == center4_x && y_v == center4_y));

        
        x_cible = rand() % size_x;
        y_cible = rand() % size_y;
        while(grid[x_cible][y_cible] == 0
            || grid[x_cible][y_cible] == 1
            || grid[x_cible][y_cible] == 2
            || grid[x_cible][y_cible] == 4
            || grid[x_cible][y_cible] == 8
            || grid[x_cible][y_cible] == 5
            || grid[x_cible][y_cible] == 10
            || (x_cible == center1_x && y_cible == center1_y)
            || (x_cible == center2_x && y_cible == center2_y)
            || (x_cible == center3_x && y_cible == center3_y)
            || (x_cible == center4_x && y_cible == center4_y)){
            x_cible = rand() % size_x;
            y_cible = rand() % size_y;
        }

        // LettreCible
        int cible = rand() % 4;
        switch(cible){
            case 0:
                lettreCible = 'r';
                break;
            case 1:
                lettreCible = 'b';
                break;
            case 2:
                lettreCible = 'j';
                break;
            case 3:
                lettreCible = 'v';
                break;
            default:;
        }
    int sizeInts = getIntLength(x_r)+getIntLength(y_r)+getIntLength(x_b)+getIntLength(y_b)+getIntLength(x_j)+getIntLength(y_j);
    sizeInts+= getIntLength(x_v)+getIntLength(y_v)+getIntLength(x_cible)+getIntLength(y_cible);
    enigma = calloc(22 + sizeInts+1 + 1, sizeof(char));

    sprintf(enigma, "(%dr,%dr,%db,%db,%dj,%dj,%dv,%dv,%dc,%dc,%c)", x_r, y_r, x_b, y_b, x_j, y_j, x_v, y_v, x_cible, y_cible, lettreCible);
    return 0;
}

/*********************************************
*                                            *
*    Set le bilan de la session courante     *
*                                            *
*********************************************/
//serveur.c
int setBilanCurrentSession(){
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Setting the bilan of the current session:\n");

    if(clients == NULL) {
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : ERREUR: la liste des clients est nulle\n");
        exit(EXIT_FAILURE);
    }

    client_t* first_client = clients;

    int nbTourLength = getIntLength(nbTours);
    int sizeAll = nbTourLength;
    while(clients != NULL){
        int scoreLength = getIntLength(clients->score);
        sizeAll += (strlen(clients->name) + scoreLength + 3);
        clients = clients->next;
    }

    clients = first_client;
    bilan = (char *) malloc(sizeAll+1);
    sprintf(bilan, "%d", nbTours);    
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : %s", bilan);
    fprintf(stderr, "SizeAll : %d\n", sizeAll);
    fprintf(stderr, "toto1\n");
    while(clients != NULL){
        int scoreLength = getIntLength(clients->score);
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : scoreLength : %d\tclientNameLength : %zu\n", scoreLength, strlen(clients->name));
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : name : %s\n", clients->name);
        char *user = (char *)calloc(sizeof(char), strlen(clients->name)+scoreLength+4);
        sprintf(user, "(%s,%d)", clients->name, clients->score);
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : userBuffer : %s\n", user);
        
        sprintf(bilan,"%s%s", bilan, user);
        clients = clients->next;
    }

    clients = first_client;

    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Bilan : %s\n", bilan);
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Bilan current session set!\n");

    return 0;
}